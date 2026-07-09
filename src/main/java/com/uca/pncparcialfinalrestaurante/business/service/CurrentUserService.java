package com.uca.pncparcialfinalrestaurante.business.service;

import com.uca.pncparcialfinalrestaurante.business.exception.BusinessException;
import com.uca.pncparcialfinalrestaurante.data.entity.AppUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    public AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUser user)) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
        return user;
    }
}
