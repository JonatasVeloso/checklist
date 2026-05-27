package br.com.checklist;

public class Tarefa {

    private String descricao;
    private StatusTarefa status;

    public Tarefa(String descricao, StatusTarefa status) {
        this.descricao = descricao;
        this.status = status == null ? StatusTarefa.PENDING : status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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