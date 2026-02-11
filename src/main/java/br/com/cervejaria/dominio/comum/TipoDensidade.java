package br.com.cervejaria.dominio.comum;

/**
 * Tipo de medição de densidade do mosto/cerveja.
 * 
 * <p>
 * OG (Original Gravity) é medida antes da fermentação.
 * FG (Final Gravity) é medida após a fermentação.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public enum TipoDensidade {

    /**
     * Densidade Original (Original Gravity).
     * Medida antes do início da fermentação.
     */
    OG("Original Gravity", "Densidade antes da fermentação"),

    /**
     * Densidade Final (Final Gravity).
     * Medida após o término da fermentação.
     */
    FG("Final Gravity", "Densidade após fermentação");

    private final String nome;
    private final String descricao;

    TipoDensidade(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
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
