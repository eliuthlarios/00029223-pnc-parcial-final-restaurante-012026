# Parcial Final - Sistema de Pedidos de Restaurante

API REST desarrollada con Spring Boot, arquitectura N-Capas, JWT, Refresh Tokens, roles, autorización por sucursal, Docker y CI/CD.

## 1. Arquitectura N-Capas

El proyecto está dividido en capas:

- **Presentación**: controllers y DTOs en `presentation/`. Recibe peticiones HTTP y valida entrada.
- **Lógica de negocio**: services y excepciones en `business/`. Aquí viven las reglas del restaurante y la autorización por sucursal.
- **Acceso a datos**: entities, enums y repositories en `data/`. Maneja JPA y PostgreSQL.
- **Seguridad transversal**: configuración de Spring Security, JWT y filtro de autenticación en `security/` y `config/`.

## 2. Roles

| Rol | Permisos |
| --- | --- |
| `ADMINISTRADOR` | Acceso total: sucursales, mesas, usuarios, productos y pedidos de todas las sucursales. |
| `ENCARGADO_TURNO` | Gestiona mesas y pedidos solo de su propia sucursal. |
| `CLIENTE` | Crea, ve y cancela únicamente sus propios pedidos. |

## 3. Regla de negocio no trivial implementada

Se implementó **autorización por atributo usando sucursal**.

No basta con validar que el usuario tenga rol `ENCARGADO_TURNO`; además se compara la sucursal asignada al encargado contra la sucursal de la mesa o pedido. Esa validación está centralizada en `BranchAuthorizationService`.

Ejemplo: si el encargado pertenece a `Sucursal Centro`, no puede confirmar, modificar o cancelar pedidos de `Sucursal Escalón`, aunque tenga el rol correcto.

Adicionalmente, cuando un usuario cambia su contraseña se incrementa `tokenVersion` y se revocan sus refresh tokens, dejando inválidos los access tokens anteriores.

## 4. Levantar con Docker

```bash
docker-compose up --build
```

La API queda en:

```bash
http://localhost:8080
```

Health check:

```bash
curl http://localhost:8080/api/health
```

## 5. Usuarios semilla para pruebas

Cuando `APP_SEED_ENABLED=true`, se crean usuarios demo:

| Rol | Username | Password |
| --- | --- | --- |
| Administrador | `admin` | `Admin123` |
| Encargado Centro | `turno.centro` | `Turno123` |
| Cliente | `cliente` | `Cliente123` |

## 6. Flujo básico de prueba

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin123"}'
```

Copia el `accessToken` y úsalo así:

```bash
export TOKEN="PEGAR_ACCESS_TOKEN"
```

### Ver productos

```bash
curl http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN"
```

### Crear pedido como cliente

Primero inicia sesión como `cliente`. Luego:

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "tableId": 1,
    "items": [
      {"productId": 1, "quantity": 1},
      {"productId": 2, "quantity": 2}
    ]
  }'
```

### Confirmar pedido como encargado

Inicia sesión como `turno.centro` y confirma un pedido de su sucursal:

```bash
curl -X PATCH http://localhost:8080/api/orders/1/confirm \
  -H "Authorization: Bearer $TOKEN"
```

## 7. Refresh token

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"PEGAR_REFRESH_TOKEN"}'
```

## 8. CI/CD

El workflow `.github/workflows/ci.yml` se ejecuta en cada push o pull request hacia `main`.

Realiza:

1. Build del proyecto.
2. Ejecución de pruebas.
3. Análisis OWASP Dependency Check con fallo en CVSS >= 9.0.
4. Escaneo de secretos con TruffleHog.
