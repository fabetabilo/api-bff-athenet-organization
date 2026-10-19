# Cambios de integración — BFF de Organización

Fecha: 18 de octubre de 2026. Documento de entrega para el responsable de este repositorio.

## Objetivo

Completar las operaciones backend que necesita Organización y permitir la programación y confirmación explícita de encuentros. Los microservicios conservan sus reglas de negocio; Competencia y Eventos publican los efectos asíncronos para Notificaciones y Ranking.

## Qué cambió y por qué

### Administración de catálogos

Se añadieron cuatro controladores bajo `/api/admin`: `sports`, `institutions`, `campuses` y `teams`. Cada recurso ofrece listado, detalle, creación, edición y eliminación: 20 endpoints en total, protegidos por el rol ADMIN.

`AdminInstitutionsClient`, `AdminCatalogService`, `AdminCatalogMapper` y sus DTOs conectan esas operaciones con Instituciones. El BFF mantiene nombres de campos en inglés y traduce al contrato en español del microservicio. Por ejemplo, `name` pasa a `nombre`, `campusId` a `sedeId` y `division` a `categoria`. Los filtros de sede por institución y equipo por sede también se traducen. Los listados administrativos preservan registros inactivos.

Esto permite administrar el catálogo desde Organización a través del BFF. Las restricciones de integridad y borrado siguen siendo responsabilidad de Instituciones; no se agregaron borrados en cascada desde el BFF.

### Clientes autenticados y errores

Se separó el cliente administrativo de Instituciones del cliente de consulta pública. Se extrajeron `BearerTokenRelay` y `UpstreamErrorConfig` desde la configuración de clientes para compartir el reenvío del JWT y la traducción de errores sin duplicación.

Las escrituras administrativas reenvían el access token del usuario y el microservicio vuelve a autorizarlo. Los clientes públicos conservan sus consultas sin relay. Se mantiene la traducción de errores: 400/403/404/409/422 conservan información útil; un error interno del upstream, incluido su 401, se traduce a 502; indisponibilidad de conexión, a 503.

### Programación y confirmación de encuentros

Se incorporó:

```http
POST /api/director/competitions/{id}/matches/{matchId}/confirm
```

`CompetitionsClient`, controlador, servicio y mappers propagan esta operación hacia Competencia. Se añadieron `endDate` y `endTime` a la programación y `scheduleConfirmed` y `scheduleVersion` a las respuestas. La respuesta del delegado expone además `version`, correspondiente al resultado.

La confirmación es una acción separada de iniciar la copa y de iniciar el encuentro, conforme a la decisión del usuario. El director ingresa el término explícitamente. Los campos nuevos de confirmación del DTO del cliente usan tipos que toleran valores ausentes; el mapper los representa como false/0 cuando corresponde.

### Integridad de eventos y respuestas de escritura

Se añadió `EventReferenceValidator` para comprobar existencia y relación de institución, sede, equipo y deporte antes de escribir un evento independiente. Las reglas de horarios y estados siguen en Eventos.

Antes de editar o borrar, se consulta el origen persistido del evento. Si es una copia de Competencia, se rechaza con 409 `COMPETITION_EVENT_READ_ONLY`, incluso si el cuerpo intenta cambiar su tipo.

Los servicios de escritura preparan el catálogo/enriquecimiento antes de persistir en el upstream. Así se evita que una consulta secundaria posterior convierta una escritura exitosa en una respuesta fallida. El servicio del delegado también tolera indisponibilidad del catálogo de competencias para el enriquecimiento cuando corresponde.

## Archivos y pruebas

- `client/institutions/`, `service/admin/` y `web/admin/`: catálogo administrativo y mapeos.
- `config/BearerTokenRelay.java`, `config/UpstreamErrorConfig.java` y `RestClientConfig.java`: clientes y errores.
- `service/director/`, `service/delegate/`, DTOs y cliente de Competencia: agenda, protección de origen y enriquecimiento.
- `pom.xml`: dependencias para pruebas HTTP y de seguridad.
- `OrganizationFlowTest` y `AdminCatalogHttpTest`: pruebas de flujos, contratos, errores y permisos.
- [Matriz de operaciones](docs/organization-coverage.md) y README: documentación de las rutas.

Se ejecutaron inicialmente 17 pruebas con una configuración temporal del sandbox. Posteriormente el usuario ejecutó `clean verify` con el POM real y compartió `BUILD SUCCESS`; no compartió el total de esa ejecución.

## Validación local conectada

El usuario verificó con JWT reales: rechazo sin token; consulta y creación de catálogos como ADMIN; creación de copa, inscripción de 16 equipos, programación, confirmación e inicio como DIRECTOR; inicio, resultado 2–1 y corrección 1–3 como DELEGATE asignado. Los efectos llegaron a los microservicios y se comprobaron entregas SENT y ranking sin duplicar partidos.

## Configuración y alcance de entrega

El BFF no requiere migraciones de base de datos. Configurar tenant, audiencia de la API y `API_EVENTS_URL`, `API_INSTITUTIONS_URL`, `API_COMPETITIONS_URL` según el entorno. No incluir tokens ni archivos privados de configuración en la entrega.

El BFF no envía correos ni recalcula rankings por HTTP: esos efectos parten de los mensajes de los microservicios. No se añadió un catálogo de usuarios de Microsoft Graph. No se leyó ni modificó Angular; su consumo de los campos y rutas nuevos requiere revisión independiente. Los casos negativos completos de permisos y el despliegue AWS quedaron fuera del cierre mínimo acordado.
