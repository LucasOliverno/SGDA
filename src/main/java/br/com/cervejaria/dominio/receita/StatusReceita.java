package br.com.cervejaria.dominio.receita;

/**
 * Status de uma receita no sistema.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public enum StatusReceita {

    /**
     * Receita em elaboração, ainda não pode gerar lotes.
     */
    RASCUNHO("Rascunho", false),

    /**
     * Receita pronta para produção.
     */
    ATIVA("Ativa", true),

    /**
     * Receita desativada/histórica.
     */
    ARQUIVADA("Arquivada", false);

    private final String descricao;
    private final boolean podeCriarLote;

    StatusReceita(String descricao, boolean podeCriarLote) {
        this.descricao = descricao;
        this.podeCriarLote = podeCriarLote;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Indica se uma receita neste status pode originar novos lotes.
     */
    public boolean podeCriarLote() {
        return podeCriarLote;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
