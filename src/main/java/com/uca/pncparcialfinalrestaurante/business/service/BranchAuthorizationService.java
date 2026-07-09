package com.uca.pncparcialfinalrestaurante.business.service;

import com.uca.pncparcialfinalrestaurante.business.exception.BusinessException;
import com.uca.pncparcialfinalrestaurante.data.entity.AppUser;
import com.uca.pncparcialfinalrestaurante.data.enums.RoleName;
import org.springframework.stereotype.Service;

@Service
public class BranchAuthorizationService {
    public void assertCanManageBranch(AppUser user, Long branchId) {
        if (user.getRole() == RoleName.ADMINISTRADOR) {
            return;
        }

        if (user.getRole() == RoleName.ENCARGADO_TURNO
                && user.getBranch() != null
                && user.getBranch().getId().equals(branchId)) {
            return;
        }

        throw BusinessException.forbidden("No tiene permiso para gestionar recursos de esta sucursal");
    }

    public boolean canManageBranch(AppUser user, Long branchId) {
        if (user.getRole() == RoleName.ADMINISTRADOR) {
            return true;
        }
        return user.getRole() == RoleName.ENCARGADO_TURNO
                && user.getBranch() != null
                && user.getBranch().getId().equals(branchId);
    }
}
