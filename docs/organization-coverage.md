# Cobertura del BFF de Organización

Contrato backend implementado. No presupone que Angular ya consuma las operaciones nuevas.

| Rol | Operación | Ruta BFF | Llamada upstream |
| --- | --- | --- | --- |
| ADMIN | Listar/detalle/crear/editar/eliminar deportes | `/api/admin/sports[/{id}]` | `/api/v1/deportes[/{id}]` |
| ADMIN | Listar/detalle/crear/editar/eliminar instituciones | `/api/admin/institutions[/{id}]` | `/api/v1/instituciones[/{id}]` |
| ADMIN | Listar/detalle/crear/editar/eliminar sedes | `/api/admin/campuses[/{id}]` | `/api/v1/sedes[/{id}]` |
| ADMIN | Listar/detalle/crear/editar/eliminar equipos | `/api/admin/teams[/{id}]` | `/api/v1/equipos[/{id}]` |
| Roles autorizados | Catálogos para formularios | `/api/common/**` | GET públicos de Instituciones |
| DIRECTOR | CRUD competencias | `/api/director/competitions[/{id}]` | `/api/admin/competitions[/{id}]` |
| DIRECTOR | Iniciar copa | `POST /api/director/competitions/{id}/start` | `PATCH /api/admin/competitions/{id}/status` |
| DIRECTOR | Nómina: listar/inscribir/desinscribir | `/api/director/competitions/{id}/teams[/{teamId}]` | `/api/admin/competitions/{id}/teams[/{teamId}]` |
| DIRECTOR | Listar/programar encuentros | GET `.../{id}/matches`, PUT `.../{id}/matches/{matchId}` | Mismas operaciones bajo `/api/admin/competitions` |
| DIRECTOR | Confirmar agenda de un encuentro | `POST .../{id}/matches/{matchId}/confirm` | `POST /api/admin/competitions/{id}/matches/{matchId}/confirm` |
| DIRECTOR | CRUD eventos independientes | `/api/director/events[/{internalId}]` | `/api/admin/events[/{internalId}]` |
| DIRECTOR | Tipos de evento | `GET /api/director/event-types` | Tipos públicos de Eventos, excluyendo enfrentamientos |
| DELEGATE | Mis encuentros | `GET /api/delegate/matches` | `GET /api/delegate/matches` |
| DELEGATE | Iniciar | `POST /api/delegate/matches/{id}/start` | Misma ruta en Competencia |
| DELEGATE | Finalizar/corregir | `PUT /api/delegate/matches/{id}/result` | Misma ruta en Competencia |

CRUD: GET colección, GET detalle, POST colección, PUT detalle, DELETE detalle. La confirmación no inicia la copa ni el encuentro. El director proporciona fecha/hora de inicio y `endDate`/`endTime`; no se estima una duración. El delegado solo inicia cuando la programación está confirmada.

## Mapeo administrativo

Los DTOs del BFF usan inglés; Instituciones mantiene su contrato en español.

| Recurso | BFF → Instituciones |
| --- | --- |
| Deporte | `name → nombre`, `description → descripcion` |
| Institución | `name → nombre`, `acronym → sigla`, `imageUrl → imagenUrl`, `active → activo` |
| Sede | `name → nombre`, `city → ciudad`, `address → direccion`, `institutionId → institucionId` |
| Equipo | `name → nombre`, `division → categoria`, `coach → entrenador`, `logoUrl → logoUrl`, `campusId → sedeId`, `sportId → deporteId` |

Listado de sedes: `institutionId` se traduce a `institucionId`; listado de equipos: `campusId` se traduce a `sedeId`. Los listados administrativos incluyen inactivos; los catálogos comunes conservan sus filtros existentes. La categoría usa los valores del microservicio: MASCULINO, FEMENINO o MIXTO. Equipo no admite cambiar `active` en escritura porque el contrato upstream actual no lo contempla.

Las escrituras de Instituciones reenvían el access token y el microservicio vuelve a exigir ADMIN. Los clientes públicos siguen sin relay. Las restricciones de integridad/borrado las decide Instituciones; el BFF no encadena eliminaciones para eludirlas.

## Validaciones y fallos

- Eventos independientes: el BFF comprueba existencia y relación institución/sede/equipo, y deporte cuando se indica. Las reglas de horario, estado y tipo siguen en Eventos.
- Copias de encuentros: comprobación de origen en BFF y Eventos antes de actualizar/borrar, incluso si el request intenta cambiar el tipo. Respuesta 409 `COMPETITION_EVENT_READ_ONLY`.
- La autorización del delegado sigue en Competencia: rol DELEGATE y claim oid igual al asignado. El BFF nunca acepta un oid del body para autorizar.
- El enriquecimiento de respuestas de escritura se prepara antes de guardar. Una consulta secundaria no falla después de que la escritura ya se confirmó. Si falla la escritura por timeout, su resultado puede ser incierto: consultar estado antes de reintentar.
- 201 con Location del BFF en creación, 200 en lectura/edición/confirmación, 204 en borrado. Se conservan errores útiles 400/403/404/409/422; conexión no disponible devuelve 503. La traducción existente conserva 502 para errores internos del upstream, incluido su rechazo de autenticación.

No hay catálogo de usuarios del tenant en el BFF. La asignación recibe `delegateOid` y `delegateDisplayName`; incorporar Microsoft Graph requiere un caso de uso y permisos definidos, no se infiere de estos campos.

El BFF registra la orden del delegado; los efectos sobre notificaciones, ranking y agenda se publican desde los microservicios mediante RabbitMQ. No se agregan llamadas HTTP de envío de correo después de guardar un resultado.
