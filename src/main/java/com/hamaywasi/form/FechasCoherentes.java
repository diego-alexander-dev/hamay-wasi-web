package com.hamaywasi.form;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// valida orden y coherencia de las fechas
@Documented
@Constraint(validatedBy = FechasCoherentes.Validador.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FechasCoherentes {

    String message() default "Revisa las fechas";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // bandera para exigir fecha futura
    boolean exigirFuturo() default true;

    class Validador implements ConstraintValidator<FechasCoherentes, ReservaForm> {

        private boolean exigirFuturo;

        @Override
        public void initialize(FechasCoherentes constraintAnnotation) {
            this.exigirFuturo = constraintAnnotation.exigirFuturo();
        }

        @Override
        public boolean isValid(ReservaForm form, ConstraintValidatorContext ctx) {
            if (form == null) {
                return true;
            }
            LocalDate entrada = form.getFechaEntrada();
            LocalDate salida = form.getFechaSalida();
            if (entrada == null || salida == null) {
                return true; // campos obligatorios validados aparte
            }

            if (!salida.isAfter(entrada)) {
                return rechazar(ctx, "fechaSalida", "La salida tiene que ser posterior a la entrada");
            }
            if (ChronoUnit.DAYS.between(entrada, salida) > 30) {
                return rechazar(ctx, "fechaSalida", "La estadía máxima por reserva es de 30 noches");
            }
            if (exigirFuturo && entrada.isBefore(LocalDate.now())) {
                return rechazar(ctx, "fechaEntrada", "La entrada no puede ser anterior a hoy");
            }
            return true;
        }

        private boolean rechazar(ConstraintValidatorContext ctx, String campo, String mensaje) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(mensaje)
                    .addPropertyNode(campo)
                    .addConstraintViolation();
            return false;
        }
    }
}
