package br.com.checklist;

public class Tarefa {

    private String descricao;
    private String observacao;
    private String dataReferencia;
    private StatusTarefa status;
    private TipoTarefa tipo;

    public Tarefa(String descricao, StatusTarefa status) {
        this(descricao, "", "", status, TipoTarefa.DIARIA);
    }

    public Tarefa(String descricao, String observacao, StatusTarefa status) {
        this(descricao, observacao, "", status, TipoTarefa.DIARIA);
    }

    public Tarefa(
            String descricao,
            String observacao,
            String dataReferencia,
            StatusTarefa status,
            TipoTarefa tipo
    ) {
        this.descricao = descricao;
        this.observacao = observacao == null ? "" : observacao;
        this.dataReferencia = dataReferencia == null ? "" : dataReferencia;
        this.status = status == null ? StatusTarefa.PENDING : status;
        this.tipo = tipo == null ? TipoTarefa.DIARIA : tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao == null ? "" : observacao;
    }

    public String getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(String dataReferencia) {
        this.dataReferencia = dataReferencia == null ? "" : dataReferencia;
    }

    public StatusTarefa getStatus() {
        return status;
    }

    public void setStatus(StatusTarefa status) {
        this.status = status == null ? StatusTarefa.PENDING : status;
    }

    public TipoTarefa getTipo() {
        return tipo;
    }

    public void setTipo(TipoTarefa tipo) {
        this.tipo = tipo == null ? TipoTarefa.DIARIA : tipo;
    }

    public boolean isConcluida() {
        return StatusTarefa.DONE.equals(status);
    }

    @Override
    public String toString() {
        return descricao;
    }
}