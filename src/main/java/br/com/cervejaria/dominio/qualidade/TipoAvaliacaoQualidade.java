package br.com.cervejaria.dominio.qualidade;

/**
 * Tipo de avaliação de qualidade.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public enum TipoAvaliacaoQualidade {

    SENSORIAL("Sensorial", "Avaliação baseada nos sentidos"),
    TECNICA("Técnica", "Avaliação baseada em medições");

    private final String nome;
    private final String descricao;

    TipoAvaliacaoQualidade(String nome, String descricao) {
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
