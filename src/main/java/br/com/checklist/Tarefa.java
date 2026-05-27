package br.com.checklist;

public class Tarefa {

    private String descricao;
    private String observacao;
    private StatusTarefa status;

    public Tarefa(String descricao, StatusTarefa status) {
        this(descricao, "", status);
    }

    public Tarefa(String descricao, String observacao, StatusTarefa status) {
        this.descricao = descricao;
        this.observacao = observacao == null ? "" : observacao;
        this.status = status == null ? StatusTarefa.PENDING : status;
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

    public StatusTarefa getStatus() {
        return status;
    }

    public void setStatus(StatusTarefa status) {
        this.status = status == null ? StatusTarefa.PENDING : status;
    }

    public boolean isConcluida() {
        return StatusTarefa.DONE.equals(status);
    }

    @Override
    public String toString() {
        return descricao;
    }
}