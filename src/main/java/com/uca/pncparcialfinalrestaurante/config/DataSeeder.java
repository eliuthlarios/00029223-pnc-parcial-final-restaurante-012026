package com.uca.pncparcialfinalrestaurante.config;

import com.uca.pncparcialfinalrestaurante.data.entity.*;
import com.uca.pncparcialfinalrestaurante.data.enums.RoleName;
import com.uca.pncparcialfinalrestaurante.data.enums.TableStatus;
import com.uca.pncparcialfinalrestaurante.data.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {
    private final RestaurantBranchRepository branchRepository;
    private final RestaurantTableRepository tableRepository;
    private final ProductRepository productRepository;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        RestaurantBranch centro = branchRepository.save(RestaurantBranch.builder()
                .name("Sucursal Centro")
                .address("San Salvador Centro")
                .active(true)
                .build());

        RestaurantBranch escalon = branchRepository.save(RestaurantBranch.builder()
                .name("Sucursal Escalón")
                .address("Colonia Escalón")
                .active(true)
                .build());

        tableRepository.save(RestaurantTable.builder()
                .code("M-01")
                .capacity(4)
                .status(TableStatus.DISPONIBLE)
                .branch(centro)
                .build());
        tableRepository.save(RestaurantTable.builder()
                .code("M-02")
                .capacity(2)
                .status(TableStatus.DISPONIBLE)
                .branch(escalon)
                .build());

        productRepository.save(Product.builder()
                .name("Hamburguesa clásica")
                .description("Hamburguesa con papas")
                .price(new BigDecimal("6.50"))
                .active(true)
                .build());
        productRepository.save(Product.builder()
                .name("Limonada")
                .description("Bebida natural")
                .price(new BigDecimal("1.75"))
                .active(true)
                .build());

        userRepository.save(AppUser.builder()
                .username("admin")
                .fullName("Administrador General")
                .passwordHash(passwordEncoder.encode("Admin123"))
                .role(RoleName.ADMINISTRADOR)
                .active(true)
                .tokenVersion(0)
                .passwordChangedAt(Instant.now())
                .build());

        userRepository.save(AppUser.builder()
                .username("turno.centro")
                .fullName("Encargado Centro")
                .passwordHash(passwordEncoder.encode("Turno123"))
                .role(RoleName.ENCARGADO_TURNO)
                .branch(centro)
                .active(true)
                .tokenVersion(0)
                .passwordChangedAt(Instant.now())
                .build());

        userRepository.save(AppUser.builder()
                .username("cliente")
                .fullName("Cliente Demo")
                .passwordHash(passwordEncoder.encode("Cliente123"))
                .role(RoleName.CLIENTE)
                .active(true)
                .tokenVersion(0)
                .passwordChangedAt(Instant.now())
                .build());
    }
}
