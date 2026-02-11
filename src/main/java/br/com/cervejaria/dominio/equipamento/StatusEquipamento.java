package br.com.cervejaria.dominio.equipamento;

/**
 * Status de um equipamento.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public enum StatusEquipamento {

    DISPONIVEL("Disponível", true),
    EM_USO("Em Uso", false),
    EM_MANUTENCAO("Em Manutenção", false),
    EM_LIMPEZA("Em Limpeza", false),
    INDISPONIVEL("Indisponível", false);

    private final String descricao;
    private final boolean podeAlocar;

    StatusEquipamento(String descricao, boolean podeAlocar) {
        this.descricao = descricao;
        this.podeAlocar = podeAlocar;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Indica se o equipamento pode ser alocado a um lote.
     */
    public boolean podeAlocar() {
        return podeAlocar;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
