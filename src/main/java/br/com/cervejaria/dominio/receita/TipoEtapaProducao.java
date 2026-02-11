package br.com.cervejaria.dominio.receita;

/**
 * Tipos de etapas no processo de produção de cerveja.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public enum TipoEtapaProducao {

    MOSTURA(1, "Mostura", "Mistura do malte com água quente"),
    FILTRAGEM(2, "Filtragem", "Separação do mosto dos grãos"),
    FERVURA(3, "Fervura", "Fervura do mosto com lúpulo"),
    RESFRIAMENTO(4, "Resfriamento", "Resfriamento do mosto"),
    FERMENTACAO_PRIMARIA(5, "Fermentação Primária", "Primeira fase da fermentação"),
    FERMENTACAO_SECUNDARIA(6, "Fermentação Secundária", "Segunda fase (dry hopping, etc)"),
    MATURACAO(7, "Maturação", "Maturação/condicionamento da cerveja"),
    CARBONATACAO(8, "Carbonatação", "Carbonatação natural ou forçada"),
    ENVASE(9, "Envase", "Envase em garrafas ou barris");

    private final int ordemPadrao;
    private final String nome;
    private final String descricao;

    TipoEtapaProducao(int ordemPadrao, String nome, String descricao) {
        this.ordemPadrao = ordemPadrao;
        this.nome = nome;
        this.descricao = descricao;
    }

    /**
     * Retorna a ordem padrão desta etapa no processo cervejeiro.
     */
    public int getOrdemPadrao() {
        return ordemPadrao;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return nome;
    }
}
