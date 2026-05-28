package br.com.checklist;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public final class DataTarefaValidator {

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter
            .ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private DataTarefaValidator() {
    }

    public static ResultadoValidacao normalizar(TipoTarefa tipo, String dataDigitada) {
        TipoTarefa tipoSeguro = tipo == null ? TipoTarefa.DIARIA : tipo;
        String data = dataDigitada == null ? "" : dataDigitada.trim();

        return switch (tipoSeguro) {
            case DIARIA -> normalizarDiaria(data);
            case SEMANAL -> normalizarSemanal(data);
            case MENSAL -> normalizarMensal(data);
            case PROGRAMADA -> normalizarProgramada(data);
        };
    }

    public static String ajustarAoTrocarTipo(TipoTarefa novoTipo, String dataAtual) {
        ResultadoValidacao resultado = normalizar(novoTipo, dataAtual);

        if (resultado.valida()) {
            return resultado.valorNormalizado();
        }

        return "";
    }

    private static ResultadoValidacao normalizarDiaria(String data) {
        if (data.isBlank()) {
            return ResultadoValidacao.valido("");
        }

        return ResultadoValidacao.invalido(
                "Tarefas DIARIA não precisam de data. Deixe o campo Data vazio."
        );
    }

    private static ResultadoValidacao normalizarSemanal(String data) {
        if (data.isBlank()) {
            return ResultadoValidacao.invalido(
                    "Para tarefas SEMANAL, informe um dia da semana: " + DiaSemana.valoresValidos()
            );
        }

        DiaSemana diaSemana = DiaSemana.fromTexto(data);

        if (diaSemana == null) {
            return ResultadoValidacao.invalido(
                    "Dia da semana inválido. Use um destes valores: " + DiaSemana.valoresValidos()
            );
        }

        return ResultadoValidacao.valido(diaSemana.name());
    }

    private static ResultadoValidacao normalizarMensal(String data) {
        if (data.isBlank()) {
            return ResultadoValidacao.invalido(
                    "Para tarefas MENSAL, informe o dia do mês, de 1 a 31."
            );
        }

        try {
            int dia = Integer.parseInt(data);

            if (dia < 1 || dia > 31) {
                return ResultadoValidacao.invalido(
                        "Dia mensal inválido. Use um número de 1 a 31."
                );
            }

            return ResultadoValidacao.valido(String.valueOf(dia));
        } catch (NumberFormatException e) {
            return ResultadoValidacao.invalido(
                    "Dia mensal inválido. Use apenas número, por exemplo: 10."
            );
        }
    }

    private static ResultadoValidacao normalizarProgramada(String data) {
        if (data.isBlank()) {
            return ResultadoValidacao.invalido(
                    "Para tarefas PROGRAMADA, informe uma data no formato dd/MM/yyyy."
            );
        }

        try {
            LocalDate dataConvertida = LocalDate.parse(data, FORMATADOR_DATA);
            return ResultadoValidacao.valido(dataConvertida.format(FORMATADOR_DATA));
        } catch (DateTimeParseException e) {
            return ResultadoValidacao.invalido(
                    "Data inválida. Use o formato dd/MM/yyyy, por exemplo: 28/05/2026."
            );
        }
    }

    public record ResultadoValidacao(boolean valida, String valorNormalizado, String mensagemErro) {

        public static ResultadoValidacao valido(String valorNormalizado) {
            return new ResultadoValidacao(true, valorNormalizado, "");
        }

        public static ResultadoValidacao invalido(String mensagemErro) {
            return new ResultadoValidacao(false, "", mensagemErro);
        }
    }
}