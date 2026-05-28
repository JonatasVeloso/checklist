package br.com.checklist;

public enum TipoTarefa {

    DIARIA("Diária"),
    SEMANAL("Semanal"),
    MENSAL("Mensal"),
    PROGRAMADA("Programada");

    private final String descricao;

    TipoTarefa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return name();
    }

    public static TipoTarefa fromTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return DIARIA;
        }

        try {
            return TipoTarefa.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return DIARIA;
        }
    }
}