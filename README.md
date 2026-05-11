# uNotificacionesGyP
Backend para almacenar modulo de Notificaciones

# uAlertasGyP
Modulo de alertas para Gestion y Prevension de incendios para la Municipalidad Valle del Sol

Microservicio de [uNotificaciones]

Este microservicio forma parte del ecosistema de Gestión y Planificación (GyP). Está desarrollado con Spring Boot 3 y utiliza Docker para su despliegue, asegurando un entorno consistente.

🛠️ Tecnologías Utilizadas

1. Java 21 (Eclipse Temurin)

2. Spring Boot 3.x (Spring Data JPA)

3. PostgreSQL (Neon.tech) como base de datos en la nube.

4. Maven para la gestión de dependencias.

5. Docker para la contenedorización.

### Requisitos Previos

- Docker Desktop instalado.

- Java 21 (si se desea ejecutar de forma local sin Docker).

- Maven 3.9+.

### Ejecución local
``
mvn clean package -DskipTests
java -jar target/*.jar
``

### Pruebas de Postman o Thunder Client

1. 
    - URL: GET /api/[nombre] o GET /api/[nombre]/ping
    - Respuesta esperada: 200 ok

### Despliegue en Render

El microservicio está configurado para despliegue continuo en Render mediante el Dockerfile multi-stage:

1. Etapa de Construcción: Compila con Maven 3.9.9.

2. Etapa de Ejecución: Corre sobre un JRE ligero de Eclipse Temurin 21.