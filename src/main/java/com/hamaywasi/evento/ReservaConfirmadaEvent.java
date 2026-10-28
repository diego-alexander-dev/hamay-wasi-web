package com.hamaywasi.evento;

import com.hamaywasi.modelo.Reserva;

// evento que se lanza cuando se crea la reserva
public record ReservaConfirmadaEvent(Reserva reserva) {
}
