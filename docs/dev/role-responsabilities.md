# Responsabilidades por rol

## Admin

Gestiona los datos base de la plataforma Athenet:

- (CRUD) Deportes de la plataforma.
- (CRUD) Instituciones afiliadas.
- (CRUD) Sedes de cada institución.
- (CRUD) Equipos de cada sede.

## Director

Gestiona (CRUD) los eventos:

- (CRUD) Gestiona y coordina los eventos.
- Asignar un delegado de Athenet a un evento concreto.
- Gestiona las copas: crea la competencia, inscribe sus 16 equipos, sortea los octavos y programa cada encuentro (fecha, hora, lugar y delegado). Los partidos nacen en la competencia, no como eventos.
- Nunca registra ni corrige resultados.

## Delegado

Está a cargo de SOLO los partidos que tiene asignados ("Mis eventos"):

- Iniciar el partido, incluso antes de la hora definida.
- Terminar el partido cargando el resultado final (sin empates), y con eso se guarda el resultado y el ganador avanza en la llave.
- Corregir el resultado, mientras el partido siguiente no haya comenzado.
