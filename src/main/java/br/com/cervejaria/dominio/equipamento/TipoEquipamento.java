package br.com.cervejaria.dominio.equipamento;

/**
 * Tipos de equipamentos da cervejaria.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public enum TipoEquipamento {

    PANELA_MOSTURA("Panela de Mostura"),
    PANELA_FERVURA("Panela de Fervura"),
    FERMENTADOR("Fermentador"),
    MATURADOR("Maturador"),
    BARRIL("Barril"),
    ENVASADORA("Envasadora"),
    MOEDOR("Moedor de Malte"),
    TROCADOR_CALOR("Trocador de Calor");

    private final String descricao;

    TipoEquipamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
