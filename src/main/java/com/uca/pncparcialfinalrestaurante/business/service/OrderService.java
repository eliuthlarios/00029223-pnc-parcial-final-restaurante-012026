package com.uca.pncparcialfinalrestaurante.business.service;

import com.uca.pncparcialfinalrestaurante.business.exception.BusinessException;
import com.uca.pncparcialfinalrestaurante.data.entity.*;
import com.uca.pncparcialfinalrestaurante.data.enums.OrderStatus;
import com.uca.pncparcialfinalrestaurante.data.enums.RoleName;
import com.uca.pncparcialfinalrestaurante.data.enums.TableStatus;
import com.uca.pncparcialfinalrestaurante.data.repository.AppUserRepository;
import com.uca.pncparcialfinalrestaurante.data.repository.ProductRepository;
import com.uca.pncparcialfinalrestaurante.data.repository.RestaurantOrderRepository;
import com.uca.pncparcialfinalrestaurante.data.repository.RestaurantTableRepository;
import com.uca.pncparcialfinalrestaurante.presentation.dto.order.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final RestaurantOrderRepository orderRepository;
    private final RestaurantTableRepository tableRepository;
    private final ProductRepository productRepository;
    private final AppUserRepository userRepository;
    private final BranchAuthorizationService branchAuthorizationService;

    @Transactional
    public OrderResponse create(AppUser user, OrderCreateRequest request) {
        RestaurantTable table = tableRepository.findById(request.tableId())
                .orElseThrow(() -> BusinessException.notFound("Mesa no encontrada"));

        if (table.getStatus() == TableStatus.FUERA_DE_SERVICIO) {
            throw BusinessException.badRequest("La mesa está fuera de servicio");
        }

        AppUser customer = resolveCustomer(user, request.customerId());
        if (user.getRole() != RoleName.CLIENTE) {
            branchAuthorizationService.assertCanManageBranch(user, table.getBranch().getId());
        }

        RestaurantOrder order = RestaurantOrder.builder()
                .customer(customer)
                .table(table)
                .status(OrderStatus.PENDIENTE)
                .total(BigDecimal.ZERO)
                .createdAt(Instant.now())
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> BusinessException.notFound("Producto no encontrado: " + itemRequest.productId()));
            if (!product.isActive()) {
                throw BusinessException.badRequest("Producto inactivo: " + product.getName());
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.quantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();
            order.addItem(item);
            total = total.add(subtotal);
        }

        order.setTotal(total);
        table.setStatus(TableStatus.OCUPADA);
        return toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findMine(AppUser user) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findByBranch(AppUser user, Long branchId) {
        branchAuthorizationService.assertCanManageBranch(user, branchId);
        return orderRepository.findByTableBranchIdOrderByCreatedAtDesc(branchId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(AppUser user, Long id) {
        RestaurantOrder order = getOrderAndCheckVisibility(user, id);
        return toResponse(order);
    }

    @Transactional
    public OrderResponse confirm(AppUser user, Long id) {
        RestaurantOrder order = orderRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Pedido no encontrado"));
        branchAuthorizationService.assertCanManageBranch(user, order.getTable().getBranch().getId());

        if (order.getStatus() != OrderStatus.PENDIENTE) {
            throw BusinessException.badRequest("Solo se pueden confirmar pedidos pendientes");
        }

        order.setStatus(OrderStatus.CONFIRMADO);
        order.setUpdatedAt(Instant.now());
        return toResponse(order);
    }

    @Transactional
    public OrderResponse cancel(AppUser user, Long id) {
        RestaurantOrder order = orderRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Pedido no encontrado"));

        if (user.getRole() == RoleName.CLIENTE) {
            if (!order.getCustomer().getId().equals(user.getId())) {
                throw BusinessException.forbidden("No puede cancelar pedidos de otro cliente");
            }
            if (order.getStatus() != OrderStatus.PENDIENTE) {
                throw BusinessException.badRequest("El cliente solo puede cancelar pedidos pendientes");
            }
        } else {
            branchAuthorizationService.assertCanManageBranch(user, order.getTable().getBranch().getId());
        }

        order.setStatus(OrderStatus.CANCELADO);
        order.setUpdatedAt(Instant.now());
        order.getTable().setStatus(TableStatus.DISPONIBLE);
        return toResponse(order);
    }

    private AppUser resolveCustomer(AppUser user, Long requestedCustomerId) {
        if (user.getRole() == RoleName.CLIENTE) {
            if (requestedCustomerId != null && !requestedCustomerId.equals(user.getId())) {
                throw BusinessException.forbidden("Un cliente solo puede crear pedidos para sí mismo");
            }
            return user;
        }

        if (requestedCustomerId == null) {
            throw BusinessException.badRequest("Debe indicar customerId cuando el pedido lo crea personal del restaurante");
        }

        AppUser customer = userRepository.findById(requestedCustomerId)
                .orElseThrow(() -> BusinessException.notFound("Cliente no encontrado"));
        if (customer.getRole() != RoleName.CLIENTE) {
            throw BusinessException.badRequest("El usuario indicado no tiene rol CLIENTE");
        }
        return customer;
    }

    private RestaurantOrder getOrderAndCheckVisibility(AppUser user, Long id) {
        RestaurantOrder order = orderRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Pedido no encontrado"));

        if (user.getRole() == RoleName.CLIENTE && order.getCustomer().getId().equals(user.getId())) {
            return order;
        }

        if (branchAuthorizationService.canManageBranch(user, order.getTable().getBranch().getId())) {
            return order;
        }

        throw BusinessException.forbidden("No tiene permiso para ver este pedido");
    }

    private OrderResponse toResponse(RestaurantOrder order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getFullName(),
                order.getTable().getId(),
                order.getTable().getCode(),
                order.getTable().getBranch().getId(),
                order.getTable().getBranch().getName(),
                order.getStatus(),
                order.getTotal(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                items
        );
    }
}
