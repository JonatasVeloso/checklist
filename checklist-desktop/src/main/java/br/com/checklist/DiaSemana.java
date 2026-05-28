package br.com.checklist;

import java.text.Normalizer;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum DiaSemana {

    SEGUNDA,
    TERCA,
    QUARTA,
    QUINTA,
    SEXTA,
    SABADO,
    DOMINGO;

    public static boolean existe(String texto) {
        return fromTexto(texto) != null;
    }

    public static DiaSemana fromTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        String valorNormalizado = normalizar(texto);

        for (DiaSemana dia : values()) {
            if (dia.name().equals(valorNormalizado)) {
                return dia;
            }
        }

        return null;
    }

    public static DiaSemana fromDayOfWeek(DayOfWeek dayOfWeek) {
        if (dayOfWeek == null) {
            return null;
        }

        return switch (dayOfWeek) {
            case MONDAY -> SEGUNDA;
            case TUESDAY -> TERCA;
            case WEDNESDAY -> QUARTA;
            case THURSDAY -> QUINTA;
            case FRIDAY -> SEXTA;
            case SATURDAY -> SABADO;
            case SUNDAY -> DOMINGO;
        };
    }

    public static String valoresValidos() {
        return Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    private static String normalizar(String texto) {
        String semAcento = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return semAcento
                .toUpperCase()
                .replace("Ç", "C");
    }
}