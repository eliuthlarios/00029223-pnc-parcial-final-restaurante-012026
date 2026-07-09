package com.uca.pncparcialfinalrestaurante.business.service;

import com.uca.pncparcialfinalrestaurante.business.exception.BusinessException;
import com.uca.pncparcialfinalrestaurante.data.entity.Product;
import com.uca.pncparcialfinalrestaurante.data.repository.ProductRepository;
import com.uca.pncparcialfinalrestaurante.presentation.dto.product.ProductRequest;
import com.uca.pncparcialfinalrestaurante.presentation.dto.product.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsByNameIgnoreCase(request.name())) {
            throw BusinessException.badRequest("Ya existe un producto con ese nombre");
        }
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .active(true)
                .build();
        return toResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findActive() {
        return productRepository.findByActiveTrue().stream().map(this::toResponse).toList();
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.isActive());
    }
}
