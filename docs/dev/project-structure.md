## Estructura del proyecto `bff_organization`

### Regla de dependencias

web ──▶ service ──▶ client        - (config puede conocer todo: arma los beans)

- `web` conoce `service` y `web.dto`. Nunca importa nada de `client`.
- `service` conoce `client` y `web.dto`. No conoce `web.controller` ni tipos HTTP (`ResponseEntity`, `HttpServletRequest`).
- `client` no conoce `service` ni `web`.
- `service` orquesta: qué microservicios llamar, en qué orden y qué hacer si fallan.
- Los `*Mapper` de `service` traducen campo a campo entre `web.dto` y `client.<ms>.dto`. No llama clientes ni toma decisiones.
- Los errores de los microservicios se traducen a `ServiceException` (`service.exception`) en un solo lugar: el `RestClientCustomizer` de `config.RestClientConfig`, que se aplica a todos los `RestClient`. Los services también la lanzan para sus propias reglas.
- `web.error` solo conoce `ServiceException`. La excepción es `ResourceAccessException` (microservicio caído o timeout → 503), porque ocurre antes de que haya respuesta que traducir.
- El formato de error hacia el front es `ApiError` (`timestamp, status, error, code, message, fieldErrors`), el mismo de ms-events: su `code`, `message` y `fieldErrors` se reenvían tal cual.

| Responde el microservicio | El BFF responde |
|---|---|
| 400, 403, 404, 409, 422 | Mismo status y mismo `ApiError` |
| 401, otro 4xx, 5xx | 502 `UPSTREAM_ERROR` (un 401 haría que el front crea que expiró la sesión) |
| No responde (caído o timeout) | 503 `SERVICE_UNAVAILABLE` |

```plaintext
com.athenet.bff_organization
├── client/                   ← PAQUETE de SALIDA: un subpaquete por microservicio
│   ├── competitions/
│   │   ├── CompetitionsClient
│   │   └── dto/
│   │       ├── request/
│   │       └── response/
│   ├── events/
│   └── institutions/
├── config/                   ← CORS, seguridad, beans de RestClient
├── service/                  ← orquestación (un service por pantalla o caso de uso)
│   ├── director/             ← un subpaquete por rol: services + mappers (web.dto ↔ client.dto)
│   ├── common/
│   └── mock/                 ← datos simulados de un microservicio aún no integrado
└── web/                      ← PAQUETE de ENTRADA: el contrato con el frontend
    ├── director/             ← un subpaquete por rol: controllers + dto/request, dto/response
    ├── common/
    └── error/                ← GlobalExceptionHandler + formato de error hacia el front
```

### Request y Response

Los nombres se leen desde la conversación HTTP a la que pertenece el DTO, no desde el BFF.
El BFF participa en dos conversaciones y en cada una cumple un rol distinto:

| Paquete           | Rol del BFF                      | `request/`                           | `response/`                             |
|-------------------|----------------------------------|--------------------------------------|-----------------------------------------|
| `web.dto`         | Servidor (el frontend lo llama)  | Lo que el frontend envía al BFF      | Lo que el BFF devuelve al frontend      |
| `client.<ms>.dto` | Cliente (llama al microservicio) | Lo que el BFF envía al microservicio | Lo que el microservicio devuelve al BFF |

```plaintext
Frontend ──web.dto.request───▶ controller ─▶ service ─(mapper)─▶ client.<ms>.dto.request ───▶ Microservicio
Frontend ◀──web.dto.response── controller ◀─ service ◀─(mapper)── client.<ms>.dto.response ◀── Microservicio
```

**Reglas**

- `web.dto` le pertenece al frontend: se diseña según lo que necesita la pantalla.
- `client.<ms>.dto` es una copia fiel del contrato del microservicio: refleja su API tal cual, sin "mejorarla".
- Nunca se reutiliza un DTO de un lado en el otro, aunque sean idénticos.
- Solo los `*Mapper` importan ambos lados.
- `request/` existe solo si hay body. Un GET con path o query params no lleva DTO de request.

**Nombres**

- En `client`, los DTOs se nombran por el recurso del microservicio: `EventResponse`, `CompetitionResponse`.
- En `web`, los DTOs se nombran por lo que muestra la pantalla: `EventCardResponse`, `CalendarioEventosResponse`.

Así nunca coinciden nombres entre ambos lados y el nombre indica a qué lado pertenece cada clase.