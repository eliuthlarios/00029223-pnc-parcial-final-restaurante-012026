package com.uca.pncparcialfinalrestaurante.business.service;

import com.uca.pncparcialfinalrestaurante.business.exception.BusinessException;
import com.uca.pncparcialfinalrestaurante.data.entity.AppUser;
import com.uca.pncparcialfinalrestaurante.data.entity.RestaurantBranch;
import com.uca.pncparcialfinalrestaurante.data.entity.RestaurantTable;
import com.uca.pncparcialfinalrestaurante.data.enums.RoleName;
import com.uca.pncparcialfinalrestaurante.data.repository.RestaurantBranchRepository;
import com.uca.pncparcialfinalrestaurante.data.repository.RestaurantTableRepository;
import com.uca.pncparcialfinalrestaurante.presentation.dto.table.TableRequest;
import com.uca.pncparcialfinalrestaurante.presentation.dto.table.TableResponse;
import com.uca.pncparcialfinalrestaurante.presentation.dto.table.UpdateTableStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableService {
    private final RestaurantTableRepository tableRepository;
    private final RestaurantBranchRepository branchRepository;
    private final BranchAuthorizationService branchAuthorizationService;

    @Transactional
    public TableResponse create(AppUser user, TableRequest request) {
        branchAuthorizationService.assertCanManageBranch(user, request.branchId());

        RestaurantBranch branch = branchRepository.findById(request.branchId())
                .orElseThrow(() -> BusinessException.notFound("Sucursal no encontrada"));

        if (tableRepository.existsByBranchIdAndCodeIgnoreCase(request.branchId(), request.code())) {
            throw BusinessException.badRequest("Ya existe una mesa con ese código en la sucursal");
        }

        RestaurantTable table = RestaurantTable.builder()
                .code(request.code())
                .capacity(request.capacity())
                .status(request.status())
                .branch(branch)
                .build();

        return toResponse(tableRepository.save(table));
    }

    @Transactional(readOnly = true)
    public List<TableResponse> findVisibleTables(AppUser user) {
        if (user.getRole() == RoleName.ADMINISTRADOR) {
            return tableRepository.findAll().stream().map(this::toResponse).toList();
        }
        if (user.getRole() == RoleName.ENCARGADO_TURNO && user.getBranch() != null) {
            return tableRepository.findByBranchId(user.getBranch().getId()).stream().map(this::toResponse).toList();
        }
        return tableRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TableResponse> findByBranch(AppUser user, Long branchId) {
        if (user.getRole() != RoleName.CLIENTE) {
            branchAuthorizationService.assertCanManageBranch(user, branchId);
        }
        return tableRepository.findByBranchId(branchId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public TableResponse updateStatus(AppUser user, Long id, UpdateTableStatusRequest request) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Mesa no encontrada"));
        branchAuthorizationService.assertCanManageBranch(user, table.getBranch().getId());
        table.setStatus(request.status());
        return toResponse(table);
    }

    private TableResponse toResponse(RestaurantTable table) {
        return new TableResponse(
                table.getId(),
                table.getCode(),
                table.getCapacity(),
                table.getStatus(),
                table.getBranch().getId(),
                table.getBranch().getName()
        );
    }
}
