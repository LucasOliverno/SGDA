package br.com.cervejaria.dominio.qualidade;

/**
 * Parecer final de uma avaliação de qualidade.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public enum ParecerQualidade {

    APROVADO("Aprovado", true),
    APROVADO_COM_RESSALVAS("Aprovado com Ressalvas", true),
    REPROVADO("Reprovado", false);

    private final String descricao;
    private final boolean permiteEnvase;

    ParecerQualidade(String descricao, boolean permiteEnvase) {
        this.descricao = descricao;
        this.permiteEnvase = permiteEnvase;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Indica se este parecer permite o envase do lote.
     */
    public boolean permiteEnvase() {
        return permiteEnvase;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
