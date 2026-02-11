package br.com.cervejaria.dominio.comum;

/**
 * Unidades de medida utilizadas no sistema de cervejaria.
 * 
 * <p>
 * Define as unidades para quantificação de insumos, volumes e capacidades.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public enum UnidadeMedida {

    // Massa
    QUILOGRAMA("kg", "Quilograma", TipoMedida.MASSA, 1.0),
    GRAMA("g", "Grama", TipoMedida.MASSA, 0.001),

    // Volume
    LITRO("L", "Litro", TipoMedida.VOLUME, 1.0),
    MILILITRO("mL", "Mililitro", TipoMedida.VOLUME, 0.001),

    // Contagem
    UNIDADE("un", "Unidade", TipoMedida.CONTAGEM, 1.0);

    private final String simbolo;
    private final String descricao;
    private final TipoMedida tipo;
    private final double fatorConversaoParaBase;

    UnidadeMedida(String simbolo, String descricao, TipoMedida tipo, double fatorConversaoParaBase) {
        this.simbolo = simbolo;
        this.descricao = descricao;
        this.tipo = tipo;
        this.fatorConversaoParaBase = fatorConversaoParaBase;
    }

    /**
     * Retorna o símbolo da unidade para exibição.
     * 
     * @return símbolo (ex: "kg", "L", "g")
     */
    public String getSimbolo() {
        return simbolo;
    }

    /**
     * Retorna a descrição completa da unidade.
     * 
     * @return descrição (ex: "Quilograma", "Litro")
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Retorna o tipo de medida (MASSA, VOLUME, CONTAGEM).
     * 
     * @return tipo da medida
     */
    public TipoMedida getTipo() {
        return tipo;
    }

    /**
     * Retorna a unidade base para este tipo de medida.
     * 
     * @return unidade base (QUILOGRAMA para massa, LITRO para volume)
     */
    public UnidadeMedida getUnidadeBase() {
        return switch (tipo) {
            case MASSA -> QUILOGRAMA;
            case VOLUME -> LITRO;
            case CONTAGEM -> UNIDADE;
        };
    }

    /**
     * Verifica se esta unidade é compatível com outra para conversão.
     * 
     * @param outra unidade para comparação
     * @return true se são do mesmo tipo de medida
     */
    public boolean isCompativelCom(UnidadeMedida outra) {
        return this.tipo == outra.tipo;
    }

    /**
     * Converte um valor desta unidade para outra unidade compatível.
     * 
     * @param valor   valor a converter
     * @param destino unidade de destino
     * @return valor convertido
     * @throws DominioException se as unidades não forem compatíveis
     */
    public double converterPara(double valor, UnidadeMedida destino) {
        if (!isCompativelCom(destino)) {
            throw new DominioException(
                    String.format("Não é possível converter de %s para %s: tipos incompatíveis (%s vs %s)",
                            this.descricao, destino.descricao, this.tipo, destino.tipo));
        }

        // Converte para unidade base e depois para destino
        double valorEmBase = valor * this.fatorConversaoParaBase;
        return valorEmBase / destino.fatorConversaoParaBase;
    }

    @Override
    public String toString() {
        return simbolo;
    }

    /**
     * Tipos de medida para agrupamento de unidades compatíveis.
     */
    public enum TipoMedida {
        MASSA,
        VOLUME,
        CONTAGEM
    }
}
