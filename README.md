# Hamay Wasi - Portal Web de Reservas

Proyecto web desarrollado con **Spring Boot 3.5.7**, **Thymeleaf**, **Spring Data JPA** y **PostgreSQL** para la gestión y cotización de reservas en línea del Hotel Hamay Wasi.

---

## 1. Requisitos

- **Java**: 17 o superior
- **Maven**: 3.9 o superior (o el wrapper `./mvnw` / `mvnw.cmd`)
- **PostgreSQL**: 14 o superior

---

## 2. Configuración de Base de Datos

1. Crear la base de datos en PostgreSQL:
   ```sql
   CREATE DATABASE hamay_wasi_avanzada;
   ```

2. Cargar el esquema SQL y datos iniciales:
   ```bash
   psql -U postgres -d hamay_wasi_avanzada -f docs/hamay_wasi_avanzada.sql
   ```

El esquema contiene:
- 11 tablas (catálogos de habitaciones y servicios, huéspedes, reservas, pagos, contacto y auditoría).
- Funciones y procedimientos para cálculo de disponibilidad en tiempo real.
- Vistas de consulta para detalles de reservas.

---

## 3. Ejecución del Proyecto

1. Verificar que las credenciales en `src/main/resources/application.properties` correspondan a tu PostgreSQL local:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/hamay_wasi_avanzada
   spring.datasource.username=postgres
   spring.datasource.password=123456
   ```

2. Iniciar la aplicación:
   ```bash
   mvn spring-boot:run
   ```
   *(O en Windows con el wrapper: `mvnw.cmd spring-boot:run`)*

3. Abrir en el navegador:
   **http://localhost:8080**

---

## 4. Rutas del Portal Web

| Ruta | Descripción |
|------|-------------|
| `/` | Portada principal: presentación, servicios, atractivos y catálogo |
| `/habitaciones` | Catálogo de habitaciones con fotos, comodidades y precios |
| `/reservas` | Formulario de reserva: cotización en tiempo real y selección de fechas |
| `/reservas/confirmacion/{codigo}` | Confirmación de reserva con datos y enlace directo a WhatsApp |
| `/contacto` | Formulario de contacto y consultas |
| `/api/notifications/send` | Endpoint REST para el envío de notificaciones automáticas |

---

## 5. Estructura del Proyecto

```
src/main/java/com/hamaywasi/
├── HamayWasiApplication.java       Clase principal de inicio
├── catalogo/                       Textos y recursos multimedia del sitio
├── dto/                            DTOs para solicitudes y respuestas REST
├── evento/                         Eventos de dominio (confirmación de reserva)
├── form/                           Validaciones de formularios web
├── modelo/                         Entidades JPA y enumeradores
├── repositorio/                    Interfaces Spring Data JPA
├── servicio/                       Lógica de negocio y servicios
└── web/                            Controladores Spring MVC y REST
src/main/resources/
├── application.properties          Configuración principal del sistema
├── static/                         Archivos estáticos (CSS, JS, imágenes)
└── templates/                      Vistas renderizadas con Thymeleaf
```
