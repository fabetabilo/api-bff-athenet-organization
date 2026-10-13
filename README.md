# Athenet BFF Organización

<p align="center">
BFF (Backend For Frontend) de <strong>Athenet</strong> para el panel de organización: punto de entrada único del frontend Angular, que valida el token de cada usuario, autoriza por rol y orquesta los microservicios de Athenet (eventos, instituciones y competencias).
</p>

<p align="center">Versión v0.1</p>

<h4 align="center"><strong>
<a href="https://github.com/fabetabilo/web-athenet-org">Athenet ORG</a> - <a href="https://github.com/fabetabilo/web-athenet">Athenet Público</a>
</br>
<a href="docs/dev/security.md">Seguridad</a> - <a href="docs/dev/role-responsabilities.md">Responsabilidades por Rol</a> - <a href="#desarrollo-local">Desarrollo Local</a> - <a href="#estructura-del-proyecto">Estructura del Proyecto</a>
</strong></h4>

### Stack

<div align="center">

[![Java](https://img.shields.io/badge/Java_21-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot_4-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Microsoft Azure](https://img.shields.io/badge/Microsoft_Entra_ID-%230078D4.svg?style=for-the-badge&logo=microsoftazure&logoColor=white)](https://learn.microsoft.com/entra/identity/)

</div>

* **Framework backend:** [Spring Boot 4.0](https://spring.io/projects/spring-boot) (Spring Web MVC).
* **Lenguaje:** Java 21.
* **Comunicación con microservicios:** `RestClient` (`spring-boot-starter-restclient`), reenviando el token del usuario (salvo a ms-institutions, cuyos GET son públicos).
* **Autenticación:** `spring-boot-starter-oauth2-resource-server`, validando JWT emitidos por Microsoft Entra ID.
* **Build:** Maven (vía wrapper `./mvnw`, no requiere instalación global).

### bff-organization

El frontend de organización llama solo a este BFF. El BFF valida el token, decide por rol qué puede hacer cada usuario (`ADMIN`, `DIRECTOR`, `DELEGATE`), llama a los microservicios necesarios con el token y devuelve los datos armados para cada pantalla.

Más detalle en [docs/dev/security.md](docs/dev/security.md) y [docs/dev/role-responsabilities.md](docs/dev/role-responsabilities.md).

### Desarrollo Local

#### 1. Verificación de dependencias del entorno

* **Java:** JDK 21 (el `pom.xml` fija `java.version=21`).
* **Maven:** no hace falta instalarlo, el repo incluye el wrapper (`./mvnw`).
* **Microservicios:** ms-events (por defecto en `http://localhost:8081`) y ms-institutions (por defecto en `http://localhost:8080`) corriendo.

Verifica la versión instalada:

```bash
java -version
```

#### 2. Instalar dependencias

```bash
./mvnw dependency:go-offline
```

#### 3. Configuración del entorno (`.env`)

Copia la plantilla en la raíz del proyecto:

```bash
cp .env.example .env
```

**Nunca subas `.env` al repositorio** (ya está en `.gitignore`).

| Variable | Descripción |
| :--- | :--- |
| `SERVER_PORT` | Puerto del BFF (por defecto `8087`). |
| `TENANT_ID` | Tenant de Microsoft Entra ID. |
| `CLIENT_ID` | Application (Client) ID del App Registration de Athenet (el mismo que usan los microservicios). |
| `CORS_ALLOWED_ORIGINS` | Orígenes del frontend, separados por coma (por defecto `http://localhost:4200`). |
| `API_EVENTS_URL` | URL de ms-events (por defecto `http://localhost:8081`). |
| `API_INSTITUTIONS_URL` | URL de ms-institutions (por defecto `http://localhost:8080`). |

#### 4. Servidor de desarrollo

```bash
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8087/`.

#### 5. Compilación

```bash
./mvnw clean package -DskipTests
```

El artefacto se genera en `target/bff-organization-1.0.0-SNAPSHOT.jar`.

### Estructura del Proyecto

Resumen. El detalle y las reglas de dependencias están en [docs/dev/project-structure.md](docs/dev/project-structure.md).

```text
src/main/java/com/athenet/bff_organization/
├── client/            # Un subpaquete por microservicio: XxxClient + DTOs de su contrato
├── config/            # SecurityConfig (JWT, roles, CORS), RestClientConfig
├── service/           # Orquestación por rol (director/, common/) y mappers
└── web/               # Controllers y DTOs por rol (director/, common/): el contrato con el frontend
```
