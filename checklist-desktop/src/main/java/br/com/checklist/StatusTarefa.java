package br.com.checklist;

import java.awt.Color;

public enum StatusTarefa {

    PENDING("Pendente", new Color(245, 245, 245)),
    DOING("Fazendo", new Color(173, 216, 230)),
    WAITING("Aguardando", new Color(255, 245, 157)),
    DONE("Concluída", new Color(200, 255, 200));

    private final String descricao;
    private final Color cor;

    StatusTarefa(String descricao, Color cor) {
        this.descricao = descricao;
        this.cor = cor;
    }

    public String getDescricao() {
        return descricao;
    }

    public Color getCor() {
        return cor;
    }

    @Override
    public String toString() {
        return name();
    }

    public static StatusTarefa fromTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return PENDING;
        }

        String valor = texto.trim();

        // Compatibilidade com arquivo antigo: true/false
        if ("true".equalsIgnoreCase(valor)) {
            return DONE;
        }

        if ("false".equalsIgnoreCase(valor)) {
            return PENDING;
        }

        try {
            return StatusTarefa.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }
}