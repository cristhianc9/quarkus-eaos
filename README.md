# Backend Quarkus - Reto Técnico

Este proyecto implementa un backend reactivo, orientado a eventos, con Quarkus (última versión), siguiendo buenas prácticas, principios SOLID y Clean Code. Incluye endpoints para concatenar strings, consumir la PokeAPI, tareas programadas, logging avanzado, CORS, y configuración avanzada.

## Estructura
- **controller/**: Exposición de endpoints REST
- **service/**: Lógica de negocio
- **dto/**: Objetos de transferencia de datos
- **event/**: Definición y manejo de eventos
- **client/**: Clientes REST externos
- **config/**: Configuración de Quarkus y utilidades
- **interceptor/**: Interceptores de entrada/salida y logging
- **util/**: Utilidades generales
- **diagram/**: Diagramas de clases (.puml)

## Despliegue
- Puerto HTTP: 15050 (prod), 15055 (test)
- CORS habilitado
- Logs asincrónicos, rotación 10MB x 3 archivos
- Swagger solo en dev
- start.sh para despliegue local

## Endpoints

- **POST /api/v1/test/{p1}/{p2}/{p3}/{p4}/{p5}**
	- Recibe 5 strings como path params (text/plain)
	- Retorna la concatenación de los 5 strings
	- Valida caracteres inseguros y patrones de inyección SQL
	- Usa EventBus bloqueante para publicar el evento
	- Responde 400 si algún parámetro es inválido

- **GET /api/v2/move**
	- Proxy reactivo a https://pokeapi.co/api/v2/move
	- Propaga cabecera Authorization si está presente
	- Tolerancia a fallos: Retry y CircuitBreaker
	- Usa EventBus no bloqueante para publicar el evento
	- Responde 503 si el servicio externo falla

- **Tarea programada**
	- Llama a /api/v2/move cada 5 minutos (cron configurable)
	- Publica evento MoveFetchedEvent en el bus de eventos

### Swagger / OpenAPI
- Documentación interactiva disponible en `/q/swagger-ui` (solo en perfil dev)

### Perfiles y Puertos
- **dev/prod:** Puerto 15050
- **test:** Puerto 15055 (forzado en tests)

## Diagrama de Clases
El diagrama de clases PlantUML se encuentra en `diagram/backend-class-diagram.puml`.
Puedes visualizarlo con cualquier visor PlantUML o desde VS Code con la extensión adecuada.

## Requisitos
- Java 17+
- Maven 3.9+
- Bash

## Ejecución
Ver `start.sh` para despliegue local y uso de configuración externa.

---

> Para detalles de endpoints, configuración y arquitectura, ver comentarios en el código y swagger (solo en dev).
