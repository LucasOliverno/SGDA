package br.com.cervejaria.dominio.estoque;

/**
 * Motivo de um movimento de estoque.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public enum MotivoMovimento {

    // Entradas
    COMPRA(TipoMovimentoEstoque.ENTRADA, "Compra de fornecedor"),
    DEVOLUCAO_FORNECEDOR(TipoMovimentoEstoque.ENTRADA, "Devolução recebida de fornecedor"),
    AJUSTE_POSITIVO(TipoMovimentoEstoque.ENTRADA, "Ajuste de inventário (sobra)"),

    // Saídas
    USO_PRODUCAO(TipoMovimentoEstoque.SAIDA, "Uso em lote de produção"),
    DESCARTE_VALIDADE(TipoMovimentoEstoque.SAIDA, "Descarte por vencimento"),
    DESCARTE_QUALIDADE(TipoMovimentoEstoque.SAIDA, "Descarte por problema de qualidade"),
    AJUSTE_NEGATIVO(TipoMovimentoEstoque.SAIDA, "Ajuste de inventário (falta)"),
    PERDA(TipoMovimentoEstoque.SAIDA, "Perda acidental");

    private final TipoMovimentoEstoque tipo;
    private final String descricao;

    MotivoMovimento(TipoMovimentoEstoque tipo, String descricao) {
        this.tipo = tipo;
        this.descricao = descricao;
    }

    public TipoMovimentoEstoque getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isEntrada() {
        return tipo == TipoMovimentoEstoque.ENTRADA;
    }

    public boolean isSaida() {
        return tipo == TipoMovimentoEstoque.SAIDA;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
