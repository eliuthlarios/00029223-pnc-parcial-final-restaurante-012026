package com.uca.pncparcialfinalrestaurante.business.service;

import com.uca.pncparcialfinalrestaurante.business.exception.BusinessException;
import com.uca.pncparcialfinalrestaurante.data.entity.AppUser;
import com.uca.pncparcialfinalrestaurante.data.entity.RestaurantBranch;
import com.uca.pncparcialfinalrestaurante.data.enums.RoleName;
import com.uca.pncparcialfinalrestaurante.data.repository.AppUserRepository;
import com.uca.pncparcialfinalrestaurante.data.repository.RestaurantBranchRepository;
import com.uca.pncparcialfinalrestaurante.presentation.dto.user.CreateUserRequest;
import com.uca.pncparcialfinalrestaurante.presentation.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AppUserRepository userRepository;
    private final RestaurantBranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw BusinessException.badRequest("El username ya existe");
        }

        RestaurantBranch branch = null;
        if (request.role() == RoleName.ENCARGADO_TURNO) {
            if (request.branchId() == null) {
                throw BusinessException.badRequest("El encargado de turno debe tener sucursal asignada");
            }
            branch = branchRepository.findById(request.branchId())
                    .orElseThrow(() -> BusinessException.notFound("Sucursal no encontrada"));
        }

        if (request.role() == RoleName.CLIENTE && request.branchId() != null) {
            throw BusinessException.badRequest("El cliente no debe tener sucursal asignada");
        }

        AppUser user = AppUser.builder()
                .username(request.username())
                .fullName(request.fullName())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .branch(branch)
                .active(true)
                .tokenVersion(0)
                .passwordChangedAt(Instant.now())
                .build();

        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse toResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole(),
                user.getBranch() == null ? null : user.getBranch().getId(),
                user.getBranch() == null ? null : user.getBranch().getName(),
                user.isActive()
        );
    }
}
