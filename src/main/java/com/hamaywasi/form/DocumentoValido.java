package com.hamaywasi.form;

import com.hamaywasi.modelo.TipoDocumento;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

// valida el documento segun su tipo
@Documented
@Constraint(validatedBy = DocumentoValido.Validador.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentoValido {

    String message() default "Revisa el número de documento";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validador implements ConstraintValidator<DocumentoValido, ReservaForm> {

        private static final Pattern DIGITOS = Pattern.compile("^\\d+$");
        private static final Pattern ALFANUMERICOS = Pattern.compile("^[A-Za-z0-9]+$");

        @Override
        public boolean isValid(ReservaForm form, ConstraintValidatorContext ctx) {
            if (form == null) {
                return true;
            }
            TipoDocumento tipo = form.getTipoDocumento();
            String numero = form.getNumeroDocumento();
            if (tipo == null || numero == null || numero.isBlank()) {
                return true; // campo obligatorio validado aparte
            }

            String limpio = numero.trim();
            boolean ok;
            String mensaje;
            switch (tipo) {
                case DNI -> {
                    ok = DIGITOS.matcher(limpio).matches() && limpio.length() == 8;
                    mensaje = "El DNI tiene 8 dígitos";
                }
                case CE -> {
                    ok = ALFANUMERICOS.matcher(limpio).matches()
                            && limpio.length() >= 9 && limpio.length() <= 12;
                    mensaje = "El carné de extranjería admite de 9 a 12 letras o números";
                }
                case PASAPORTE -> {
                    ok = ALFANUMERICOS.matcher(limpio).matches()
                            && limpio.length() >= 6 && limpio.length() <= 12;
                    mensaje = "El pasaporte admite de 6 a 12 letras o números";
                }
                default -> {
                    ok = true;
                    mensaje = "Revisa el número de documento";
                }
            }

            if (ok) {
                return true;
            }
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(mensaje)
                    .addPropertyNode("numeroDocumento")
                    .addConstraintViolation();
            return false;
        }
    }
}
