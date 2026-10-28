package com.hamaywasi.catalogo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// contenido estatico y textos del sitio
public final class Contenido {

    private Contenido() {
    }

    public record Categoria(String titulo, String etiqueta, String descripcion, String imagen) {
    }

    public record Atractivo(String nombre, String intro, String imagen) {
    }

    public record Servicio(String titulo, String descripcion, String imagen) {
    }

    // datos descriptivos de la habitacion
    public record Habitacion(String nombre, String imagen, String desc, List<String> features) {
    }

    public static final List<String> HERO_IMAGENES = List.of(
            "https://images.unsplash.com/photo-1551882547-ff40c0d5e9af?q=80&w=1920",
            "https://images.unsplash.com/photo-1562790351-d273a961e0e9?q=80&w=1920",
            "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?q=80&w=1920");

    public static final List<Categoria> CATEGORIAS = List.of(
            new Categoria("Simple Ventana", "Vista exterior",
                    "Habitación individual con excelente iluminación natural y una agradable vista hacia el exterior.",
                    "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?q=80&w=800"),
            new Categoria("Simple Pasillo", "Cómodo",
                    "Espacio individual práctico y privado, con acceso directo y cómodo para estancias rápidas.",
                    "https://images.unsplash.com/photo-1591088398332-8a7791972843?q=80&w=800"),
            new Categoria("Matrimonial", "Matrimonial",
                    "Diseñada para parejas, con una hermosa ventana exterior y un ambiente cálido y relajante.",
                    "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?q=80&w=800"),
            new Categoria("Habitación Doble", "Familiar",
                    "Espacios amplios equipados con dos camas confortables, perfectas para colegas o amigos.",
                    "https://images.unsplash.com/photo-1566665797739-1674de7a421a?q=80&w=800"));

    public static final List<Atractivo> ATRACTIVOS = List.of(
            new Atractivo("Catedral de Huancayo",
                    "Ubicada en la Plaza Constitución, es una joya arquitectónica de estilo neoclásico construida en el siglo XIX.",
                    "https://images.unsplash.com/photo-1544644181-1484b3fdfc62?q=80&w=1000"),
            new Atractivo("Cerrito de la Libertad",
                    "Mirador natural privilegiado que ofrece una vista panorámica impresionante de todo el Valle del Mantaro.",
                    "https://images.unsplash.com/photo-1506744038136-46273834b3fb?q=80&w=1000"),
            new Atractivo("Parque de la Identidad Wanka",
                    "Homenaje a la cultura regional construido íntegramente en piedra canto rodado, flores y arte wanka.",
                    "https://images.unsplash.com/photo-1519681393784-d120267933ba?q=80&w=1000"),
            new Atractivo("Feria Dominical de Huancayo",
                    "Una de las ferias artesanales, comerciales y culturales más grandes y tradicionales de todo el Perú.",
                    "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?q=80&w=1000"),
            new Atractivo("Cochas Chico y Grande",
                    "Cuna de los mejores artesanos del mate burilado, donde el arte cobra vida sobre las semillas de calabaza.",
                    "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?q=80&w=1000"));

    public static final List<Servicio> SERVICIOS = List.of(
            new Servicio("Recepción 24 horas",
                    "Asistencia personalizada y seguridad garantizada en cualquier momento de tu estadía.",
                    "https://images.unsplash.com/photo-1562790351-d273a961e0e9?q=80&w=800"),
            new Servicio("WiFi de alta velocidad",
                    "Conexión estable y de alta velocidad en todas las instalaciones para tus dispositivos.",
                    "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?q=80&w=800"),
            new Servicio("Bebidas y snacks",
                    "Variedad de piqueos, refrescos y aperitivos disponibles para disfrutar en el momento.",
                    "https://images.unsplash.com/photo-1551024709-8f23befc6f87?q=80&w=800"),
            new Servicio("Smart TV y streaming",
                    "Disfruta de Netflix, YouTube, Disney+ y entretenimiento total desde la comodidad de tu habitación.",
                    "https://images.unsplash.com/photo-1593784991095-a205069470b6?q=80&w=800"));

    public static final List<Habitacion> HABITACIONES = List.of(
            new Habitacion("Standard Individual con Vista a la Calle",
                    "https://images.unsplash.com/photo-1611892440504-42a792e24d32?q=80&w=1000",
                    "Descansa en un ambiente clásico y cálido, con vista a la ciudad y la comodidad ideal para explorar a tu ritmo.",
                    List.of("Aire acondicionado", "TV Smart", "Baño propio", "No fumadores", "Vistas de la calle")),
            new Habitacion("Standard Individual con Vista Interior",
                    "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?q=80&w=1000",
                    "Silencio y tranquilidad en el corazón del hotel. Perfecta para un descanso profundo tras un día de aventuras.",
                    List.of("Aire acondicionado", "TV Smart", "Baño propio", "No fumadores", "Silencio total")),
            new Habitacion("Standard Matrimonial con Vista a la Calle",
                    "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?q=80&w=1000",
                    "Amplitud y confort para parejas, con hermosas vistas a la actividad de la ciudad.",
                    List.of("Aire acondicionado", "TV Smart", "Baño propio", "No fumadores", "Vistas de la calle")),
            new Habitacion("Standard Doble Dos Camas",
                    "https://images.unsplash.com/photo-1590490360182-c33d57733427?q=80&w=1000",
                    "Ideal para amigos o familias pequeñas. Espacio, comodidad y un diseño pensado para el descanso compartido.",
                    List.of("Aire acondicionado", "Frigobar", "Baño propio", "Escritorio", "Vista panorámica")));

    public static List<String> nombresHabitaciones() {
        return HABITACIONES.stream().map(Habitacion::nombre).toList();
    }

    // mapa de habitaciones por nombre
    public static Map<String, Habitacion> porNombre() {
        return HABITACIONES.stream()
                .collect(Collectors.toMap(h -> h.nombre().trim(), h -> h, (a, b) -> a, LinkedHashMap::new));
    }

    public static Optional<Habitacion> buscarHabitacion(String nombre) {
        if (nombre == null) {
            return Optional.empty();
        }
        return HABITACIONES.stream()
                .filter(h -> h.nombre().trim().equalsIgnoreCase(nombre.trim()))
                .findFirst();
    }
}
