package br.com.cervejaria.dominio.insumo;

/**
 * Categorias de insumos utilizados na produção de cerveja.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public enum CategoriaInsumo {

    MALTE("Malte", "Grãos maltados", true),
    LUPULO("Lúpulo", "Flores de lúpulo para amargor e aroma", false),
    LEVEDURA("Levedura", "Fermento para fermentação", true),
    AGUA("Água", "Base líquida da cerveja", true),
    ADJUNTO("Adjunto", "Ingredientes extras (açúcar, frutas, especiarias)", false),
    CLARIFICANTE("Clarificante", "Agentes de clarificação", false);

    private final String nome;
    private final String descricao;
    private final boolean obrigatorioEmReceita;

    CategoriaInsumo(String nome, String descricao, boolean obrigatorioEmReceita) {
        this.nome = nome;
        this.descricao = descricao;
        this.obrigatorioEmReceita = obrigatorioEmReceita;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Indica se esta categoria é obrigatória em uma receita válida.
     */
    public boolean isObrigatorioEmReceita() {
        return obrigatorioEmReceita;
    }

    @Override
    public String toString() {
        return nome;
    }
}
