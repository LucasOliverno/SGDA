package br.com.cervejaria.dominio.lote;

/**
 * Status de uma etapa de produção em execução.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public enum StatusEtapaExecutada {

    PENDENTE("Pendente", false),
    EM_ANDAMENTO("Em Andamento", false),
    CONCLUIDA("Concluída", true),
    COM_PROBLEMAS("Concluída com Problemas", true);

    private final String descricao;
    private final boolean finalizada;

    StatusEtapaExecutada(String descricao, boolean finalizada) {
        this.descricao = descricao;
        this.finalizada = finalizada;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Indica se a etapa está finalizada (não está mais em andamento).
     */
    public boolean isFinalizada() {
        return finalizada;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
