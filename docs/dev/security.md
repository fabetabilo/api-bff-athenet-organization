## Seguridad y roles en `bff_organization`

### Flujo del token

El frontend (Angular + MSAL) llama solo al BFF, y el BFF reenvía el mismo token a los microservicios que lo exigen.

```plaintext
Angular (MSAL) ──Bearer──▶ BFF ──mismo Bearer──▶ ms-events / ms-competitions
                           valida el token          vuelven a validar el token
                           autoriza el caso de uso  autorizan la operación sobre su recurso
                            │
                            └──sin token─────────▶ ms-institutions (sus GET son públicos)
```

- Todos los servicios comparten el mismo App Registration de Entra ID, y por lo tanto el mismo issuer y la misma audience.
- El BFF verifica; si el token no es válido rechaza la petición antes de llamar a cualquier microservicio.
- El reenvío del token lo hace un interceptor de los `RestClient` en `config`. Los `XxxClient` no saben nada de tokens. En las rutas públicas no se adjunta nada: por eso `institutionsRestClient` no lleva el interceptor. Todos los GET de ms-institutions son públicos, y si un GET público recibe un token vencido o inválido, responde 401 igual.
- Reenviar el token del usuario, en vez de usar una identidad propia del BFF, permite que cada microservicio sepa quién llama (`oid`, `roles`).

### Roles

| Rol        | Qué hace                                                                    |
|------------|-----------------------------------------------------------------------------|
| `ADMIN`    | Administra Athenet: instituciones, sedes, equipos y deportes.               |
| `DIRECTOR` | Crea, gestiona y ve eventos usando los datos que cargó el admin.            |
| `DELEGATE` | Certifica e inicia eventos y registra resultados (depende de ms-competitions). |

Los roles llegan en el claim `roles` del token. Su valor se escribe exactamente igual en Entra, en el BFF y en cada microservicio.

### Dos niveles de autorización

| Dónde        | Pregunta que responde                                  | Ejemplo                                              |
|--------------|--------------------------------------------------------|------------------------------------------------------|
| BFF          | ¿Este rol puede usar este caso de uso?                 | `DIRECTOR` puede usar "crear evento".                |
| Microservicio | ¿Este rol puede hacer esta operación sobre mi recurso? | ms-institutions: los GET son públicos, escribe solo `ADMIN`. |

**Regla clave:** en un microservicio, un rol autoriza operaciones; no es dueño del microservicio. Que el director lea lo que creó el admin no es un caso especial, es un GET permitido a `DIRECTOR`.

Las reglas que dependen de los datos (por ejemplo, "solo el delegado asignado certifica su evento") van en el microservicio dueño del dato, que compara con el `oid` del token. El BFF puede revisar lo mismo antes, pero solo para mejorar la experiencia en pantalla.

### Cómo se ordena el código

Cada capa se organiza por un eje distinto:

| Capa      | Se organiza por       | Ejemplo                                                     |
|-----------|-----------------------|-------------------------------------------------------------|
| `web`     | Rol que consume       | `web.director.DirectorEventController`                      |
| `service` | Rol y caso de uso     | `service.director.DirectorEventService`                     |
| `client`  | Microservicio         | `EventsClient`, `InstitutionsClient` (no conocen roles)     |

```plaintext
admin    ──▶ AdminInstitutionController ─▶ AdminInstitutionService ──┐
director ──▶ DirectorEventController ───▶ DirectorEventService ─────┼─▶ InstitutionsClient ─▶ ms-institutions
                                                                  └─▶ EventsClient ───────▶ ms-events
```

Los cruces entre roles se resuelven en `service`. Por ejemplo, para crear un evento, `DirectorEventService` primero valida contra ms-institutions (la sede pertenece a la institución, el equipo a la sede) y después crea en ms-events (validación pendiente, ver [roadmap](roadmap.md)).

### Rutas del BFF

Cada prefijo se asigna a un rol en un único `SecurityFilterChain`. `@PreAuthorize` se usa solo para excepciones.

| Prefijo            | Acceso                                                   |
|--------------------|----------------------------------------------------------|
| `/api/common/**`   | `ADMIN`, `DIRECTOR` o `DELEGATE` (lo que comparten varios roles) |
| `/api/admin/**`    | `ADMIN`                                                  |
| `/api/director/**` | `DIRECTOR`                                               |
| `/api/delegate/**` | `DELEGATE`                                               |

> **Ojo con los nombres:** en el BFF el prefijo es el **rol**. En ms-events, `/api/admin/**` significa "API de administración" y la usa `DIRECTOR`. El BFF hace la traducción: `/api/director/events` llama a `ms-events /api/admin/events`.

### Pendientes

- CORS se configura en el BFF. A futuro, los microservicios solo deberían ser accesibles desde el BFF.
- Si el BFF hace llamadas en paralelo (async), el contexto de seguridad no viaja solo a otros hilos: hay que propagarlo para que el interceptor encuentre el token.
- Cuando se integre el CRUD de admin de ms-institutions (POST, PUT, DELETE), hará falta un `RestClient` que sí reenvíe el token: el actual no lo lleva.
- Si algún microservicio llega a tener su propio App Registration, el reenvío directo se reemplaza por On-Behalf-Of dentro del mismo interceptor, sin tocar `service` ni `web`.
