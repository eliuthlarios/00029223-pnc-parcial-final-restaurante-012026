package com.uca.pncparcialfinalrestaurante.business.service;

import com.uca.pncparcialfinalrestaurante.business.exception.BusinessException;
import com.uca.pncparcialfinalrestaurante.data.entity.RestaurantBranch;
import com.uca.pncparcialfinalrestaurante.data.repository.RestaurantBranchRepository;
import com.uca.pncparcialfinalrestaurante.presentation.dto.branch.BranchRequest;
import com.uca.pncparcialfinalrestaurante.presentation.dto.branch.BranchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final RestaurantBranchRepository branchRepository;

    @Transactional
    public BranchResponse create(BranchRequest request) {
        if (branchRepository.existsByNameIgnoreCase(request.name())) {
            throw BusinessException.badRequest("Ya existe una sucursal con ese nombre");
        }
        RestaurantBranch branch = RestaurantBranch.builder()
                .name(request.name())
                .address(request.address())
                .active(true)
                .build();
        return toResponse(branchRepository.save(branch));
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> findAll() {
        return branchRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public BranchResponse findById(Long id) {
        return toResponse(branchRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Sucursal no encontrada")));
    }

    private BranchResponse toResponse(RestaurantBranch branch) {
        return new BranchResponse(branch.getId(), branch.getName(), branch.getAddress(), branch.isActive());
    }
}
