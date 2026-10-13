# ROADMAP

Lo que falte en el proyecto, se define aqui. Marcar con una `[x]` cuando un punto este finalizado e implementado.

> Ideas tambien se aceptan aqui. Si falta algo se agrega.

## 0. Sobre el proyecto

- [x] Uso e implementacion de Circuit Breakers (Resilience4j 2.4.0, compatible con Spring Boot 4): uno por microservicio
- [x] Manejo de excepciones: traduccion de errores de clients a 400/404/409/422/503 (Si aplica)
- [ ] Test unitarios para CADA modulo
- [ ] CI: Configuracion de despliegue

## 1. Integracion con microservicios

- [x] Integracion de api-athenet-events
- [x] Integracion de api-athenet-institutions: lectura (catalogos comunes y referencias de eventos de director)
- [ ] Integracion de api-athenet-institutions: CRUD de admin
- [ ] Integracion de api-athenet-competitions

## Ideas:

- Implementar Swagger para endpoints