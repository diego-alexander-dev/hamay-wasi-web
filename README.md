# Hamay Wasi - Portal Web de Reservas

Proyecto web desarrollado con **Spring Boot 3.5.7**, **Thymeleaf**, **Spring Data JPA** y **PostgreSQL** para la gestión y cotización de reservas en línea del Hotel Hamay Wasi.

---

## 1. Requisitos

- **Java**: 17 o superior
- **Maven**: 3.9 o superior (o el wrapper incluido `./mvnw` / `mvnw.cmd`)

---

## 2. Ejecución Inmediata (Sin Configuración Extra)

El proyecto está preconfigurado con conexión directa y segura a la base de datos PostgreSQL en la nube (Render). **No requiere instalación ni configuración previa de base de datos** para correr y evaluar el sistema en cualquier ordenador.

1. Iniciar la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```
   *(En Windows con terminal o PowerShell: `mvnw.cmd spring-boot:run`)*  
   *(O desde VS Code / IntelliJ / Eclipse ejecutando la clase `HamayWasiWebApplication.java`)*

2. Abrir en el navegador:
   - **Entorno local:** [http://localhost:9090](http://localhost:9090)
   - **Entorno en producción:** [https://hamay-wasi-web.onrender.com](https://hamay-wasi-web.onrender.com)

---

## 3. Configuración con Base de Datos Local (Opcional)

Si se prefiere utilizar una instancia local de PostgreSQL:

1. Crear la base de datos:
   ```sql
   CREATE DATABASE hamay_wasi_avanzada;
   ```
2. Cargar el esquema y datos iniciales:
   ```bash
   psql -U postgres -d hamay_wasi_avanzada -f docs/hamay_wasi_avanzada.sql
   ```
3. Modificar las credenciales en `src/main/resources/application.properties` o definir las variables de entorno `DB_URL`, `DB_USER`, `DB_PASSWORD`.

---

## 4. Rutas del Portal Web

| Ruta | Descripción |
| :--- | :--- |
| `/` | Portada principal: presentación, servicios, atractivos y catálogo |
| `/habitaciones` | Catálogo de habitaciones con comodidades, fotos y tarifas |
| `/reservas` | Formulario de reserva: cotización en tiempo real y disponibilidad |
| `/reservas/confirmacion/{codigo}` | Comprobante de reserva y enlace directo a WhatsApp |
| `/contacto` | Formulario de contacto y consultas |
| `/api/notifications/send` | Endpoint REST para envío de notificaciones automáticas |

---

## 5. Estructura del Proyecto

```
src/main/java/com/hamaywasi/
├── HamayWasiWebApplication.java   Clase principal de inicio
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
docs/
└── hamay_wasi_avanzada.sql         Esquema y datos del hotel en PostgreSQL
```
