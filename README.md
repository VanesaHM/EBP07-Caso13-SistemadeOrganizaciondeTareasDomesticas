# EP07 - Sistema de Organizacion de Tareas Domesticas

## Descripcion del proyecto
Este proyecto consiste en el desarrollo de una aplicacion que permite organizar y distribuir tareas domesticas dentro de un hogar compartido.

## Funcionalidades principales

* Registro de usuarios y grupos familiares
* Creacion y asignacion de tareas domesticas
* Definicion de fechas limite y prioridades
* Registro del estado de cada tarea
* Historial de cumplimiento por usuario
* Reportes sobre la distribucion de responsabilidades en el hogar

## Base de datos local vs Docker

El backend ahora usa perfiles distintos para evitar el problema de conectividad entre el JAR local y PostgreSQL en Docker:

* `local`: usa `localhost:5432`
* `docker`: usa `db:5432`
* `render`: usa `DATABASE_URL` si se define

### Caso 1: PostgreSQL en Docker y JAR local

Levanta solo la base de datos:

```bash
docker compose up -d db
```

Luego ejecuta el backend localmente:

```bash
cd Soyla
./mvnw spring-boot:run
```

En este caso Spring usa automaticamente el perfil `local`.

### Caso 2: backend y PostgreSQL dentro de Docker

```bash
docker compose up --build
```

En este caso `docker-compose.yml` activa el perfil `docker`.

## Variables locales sugeridas

```env
DATABASE_USER=postgres
DATABASE_PASSWORD=postgres
DATABASE_NAME=soyla
DATABASE_HOST=localhost
DATABASE_PORT=5432
PORT=8080
JWT_SECRET=change-me-local-jwt-secret-32-bytes-min
```
