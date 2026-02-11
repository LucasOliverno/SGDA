package br.com.cervejaria.dominio.estoque;

/**
 * Tipo de movimento de estoque.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public enum TipoMovimentoEstoque {

    ENTRADA("Entrada", 1),
    SAIDA("Saída", -1);

    private final String descricao;
    private final int fator;

    TipoMovimentoEstoque(String descricao, int fator) {
        this.descricao = descricao;
        this.fator = fator;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Fator para aplicar na quantidade: +1 para entrada, -1 para saída.
     */
    public int getFator() {
        return fator;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
