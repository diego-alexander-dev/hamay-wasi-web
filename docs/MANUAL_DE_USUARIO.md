# Manual de Usuario - Portal Web de Reservas Hotel Hamay Wasi

## 1. Introducción y Propósito del Sistema

El Portal Web de Reservas del Hotel Hamay Wasi es una plataforma tecnológica orientada al comercio electrónico y a la gestión de reservas hoteleras en línea. Diseñado para operar en la ciudad de Huancayo, el sistema brinda a turistas nacionales, internacionales y viajeros corporativos una experiencia ágil, transparente y segura para planificar su estadía en el Valle del Mantaro.

El presente manual describe de forma estructurada el funcionamiento del portal web, sus módulos de consulta, cotización en tiempo real, validación de disponibilidad y emisión de comprobantes de reserva.

---

## 2. Requisitos de Acceso y Compatibilidad

### 2.1. Navegadores Web Compatibles
La plataforma está construida con estándares modernos de diseño web responsivo (HTML5, CSS3, JavaScript modular y Bootstrap 5), siendo compatible con:
- Google Chrome (versión 90 o superior)
- Mozilla Firefox (versión 88 o superior)
- Apple Safari (versión 14 o superior en iOS y macOS)
- Microsoft Edge (versión 90 o superior)
- Navegadores integrados de dispositivos móviles Android y iOS

### 2.2. Direcciones de Acceso
- Entorno de producción (en la nube): `https://hamay-wasi-web.onrender.com`
- Entorno local de desarrollo y evaluación: `http://localhost:9090`

No se requiere la instalación de complementos ni extensiones de navegador por parte del usuario final.

---

## 3. Estructura y Navegación del Portal

El portal cuenta con una interfaz organizada en secciones temáticas y una barra de navegación superior que permanece accesible durante el desplazamiento de la página.

### 3.1. Barra de Navegación Principal
Permite desplazarse a las distintas áreas del portal con un solo clic:
- Inicio: Presentación institucional y carrusel fotográfico de bienvenida.
- Habitaciones & Suites: Catálogo detallado de habitaciones, servicios y tarifas.
- Las Terrazas: Información de ambientes y áreas comunes del hotel.
- Destino Huancayo: Reseña de los principales atractivos turísticos de la región.
- Servicios: Relación de prestaciones complementarias del hotel.
- Fotos: Galería visual de las instalaciones.
- Reservar Ahora: Botón destacado de llamado a la acción que conduce al formulario de cotización.

---

## 4. Consulta del Catálogo de Habitaciones

En la sección de habitaciones (`/habitaciones`), el usuario puede revisar las cuatro categorías que componen el hotel:

### 4.1. Standard Individual con Vista a la Calle
- Capacidad: 1 huésped
- Distribución: Cama king-size, vista panorámica al exterior, baño privado, conexión Wi-Fi de alta velocidad y escritorio de trabajo.
- Tarifa referencial: S/ 120.00 por noche.

### 4.2. Standard Individual con Vista Interior
- Capacidad: 1 huésped
- Distribución: Cama king-size, diseño orientado al descanso y silencio absoluto, baño privado y Wi-Fi de alta velocidad.
- Tarifa referencial: S/ 110.00 por noche.

### 4.3. Standard Matrimonial con Vista a la Calle
- Capacidad: 2 huéspedes
- Distribución: Cama king-size, amplitud de 22 m², iluminación natural, baño completo y amenidades para parejas.
- Tarifa referencial: S/ 160.00 por noche.

### 4.4. Standard Doble Dos Camas
- Capacidad: Hasta 4 huéspedes (familias o grupos)
- Distribución: Dos camas queen-size en 25 m², ropero empotrado, espacio para equipaje y baño privado.
- Tarifa referencial: S/ 200.00 por noche.

---

## 5. Proceso Paso a Paso para Realizar una Reserva

El módulo de reservas se encuentra ubicado en la ruta `/reservas` y opera con validación de disponibilidad y control de sobreventa en tiempo real.

### Paso 1: Selección de Fechas de Estadía
1. En el campo "Fecha de llegada (Check-in)", seleccione el día en que ingresará al hotel. El horario de entrada rige a partir de las 14:00 horas.
2. En el campo "Fecha de salida (Check-out)", elija el día de culminación de su hospedaje. La entrega de habitación se realiza hasta las 12:00 horas.
3. El sistema valida automáticamente que la fecha de salida sea estrictamente posterior a la de entrada y calcula la cantidad de noches de estancia.

### Paso 2: Elección del Tipo de Habitación
1. Despliegue el selector de categoría y elija el tipo de habitación deseado.
2. Al seleccionar la categoría, la tarjeta interactiva a la derecha actualizará de inmediato la fotografía representativa, el metraje, las características principales y la tarifa base.

### Paso 3: Definición de Habitaciones y Huéspedes
1. Indique la cantidad de habitaciones requeridas utilizando los selectores de incremento y decremento.
2. Indique el número total de huéspedes que se alojarán.
3. El sistema cuenta con validación de aforo: si el número de huéspedes excede la capacidad de la categoría seleccionada, la plataforma le alertará para aumentar la cantidad de habitaciones o seleccionar una categoría de mayor aforo.

### Paso 4: Selección de Servicios Adicionales (Opcional)
El usuario puede incorporar servicios complementarios marcando la casilla correspondiente e indicando la cantidad deseada:
- Desayuno buffet: S/ 25.00 por persona / servicio.
- Traslado desde el terminal: S/ 30.00 por trayecto.
- Servicio de lavandería: S/ 15.00.
- Tour guiado por el Valle del Mantaro: S/ 80.00 por persona.

El panel de cotización acumula de manera automática el subtotal de habitación y servicios adicionales, mostrando el total estimado en tiempo real.

### Paso 5: Ingreso de Datos del Huésped
Haga clic en el botón "Continuar con la reserva" para abrir la ventana de datos personales:
1. Nombres y Apellidos: Ingrese su nombre completo de manera legible.
2. Tipo de Documento: Seleccione DNI, Carné de Extranjería (CE) o Pasaporte.
3. Número de Documento: Digite su número de identificación. Para DNI, el sistema valida que contenga exactamente 8 dígitos numéricos.
4. Correo Electrónico: Ingrese una dirección de correo válida para el registro de su reserva.
5. Teléfono / Móvil: Digite su número telefónico con o sin prefijo de país.
6. País de Origen: Especifique su país de procedencia.
7. Observaciones: Indique pedidos especiales (hora estimada de llegada, requerimientos de accesibilidad, etc.).

### Paso 6: Confirmación Final
Revise los datos y presione "Confirmar y reservar". El motor transaccional asignará la habitación libre en la base de datos de manera atómica, garantizando que ninguna otra persona pueda reservar la misma habitación en las mismas fechas.

---

## 6. Comprobante de Reserva y Notificaciones

Tras confirmarse la reserva, el sistema redirige automáticamente a la pantalla de confirmación (`/reservas/confirmacion/{codigo}`):

### 6.1. Código Único de Reserva
Se genera un código alfanumérico irrepetible con formato `HW-XXXXXX` (por ejemplo, `HW-L5SW3T`), el cual sirve como identificador para recepción, consultas o pagos directos.

### 6.2. Resumen de la Estancia
La página presenta la información consolidada:
- Habitación y piso asignado.
- Fechas de entrada y salida con indicación de número de noches.
- Relación de servicios complementarios seleccionados.
- Monto total de la reserva expresado en Soles (S/).
- Datos personales enmascarados por políticas de privacidad (ejemplo: `ju***@gmail.com` y `60****37`).

### 6.3. Opciones Disponibles en Pantalla
- Botón Abrir en WhatsApp: Genera un enlace directo a la cuenta oficial de recepción del hotel con el mensaje formateado conteniendo los detalles exactos de su reserva.
- Botón Imprimir Comprobante: Abre el diálogo nativo de impresión del navegador para archivar o imprimir una copia física en formato ticket/hoja de confirmación.
- Botón Volver al Inicio: Redirige a la página principal del hotel.

---

## 7. Módulo de Contacto y Libro de Reclamaciones

Ubicado al pie de página y en la pestaña de contacto (`/reservas?tab=contacto`):
- Los huéspedes pueden enviar solicitudes especiales, consultas de tarifas corporativas o peticiones de eventos.
- El formulario cuenta con validación de campos obligatorios y protección antispam invisible (técnica honeypot).
- La información de contacto institucional incluye dirección fija (Jr. Huancas 245 / Real 1250, Huancayo), teléfonos de atención 24 horas (`+51 969 922 828` y `938 560 388`) y correo oficial (`reservas@hamaywasi.com`).

---

## 8. Preguntas Frecuentes y Solución de Incidencias

### ¿Qué ocurre si el sistema muestra el mensaje de aforo completo?
Significa que para el rango de fechas elegido ya se han ocupado todas las habitaciones de esa categoría. La solución es seleccionar otro rango de fechas o elegir otra categoría de habitación disponible.

### ¿Se cobra la reserva inmediatamente al registrarse en la web?
No. La reserva se registra en estado confirmada con pago pendiente. El huésped puede abonar a su llegada en recepción (efectivo, tarjeta, Yape, Plin o transferencia bancaria), o coordinar el abono previo a través de WhatsApp.

### ¿Cuál es la política de cancelación?
La cancelación es gratuita hasta el día fijado para el arribo, con horario máximo de aviso hasta las 18:00 horas. Para cancelaciones, se debe contactar a recepción indicando el código `HW-XXXXXX`.
