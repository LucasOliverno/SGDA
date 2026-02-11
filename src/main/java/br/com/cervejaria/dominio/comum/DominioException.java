package br.com.cervejaria.dominio.comum;

/**
 * Exceção base para violações de regras de domínio.
 * 
 * <p>
 * Utilizada quando uma operação viola um invariante ou regra de negócio
 * do sistema de gestão de cervejaria.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class DominioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Cria uma nova exceção de domínio com a mensagem especificada.
     * 
     * @param mensagem descrição da violação de domínio
     */
    public DominioException(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria uma nova exceção de domínio com mensagem e causa.
     * 
     * @param mensagem descrição da violação de domínio
     * @param causa    exceção que originou esta
     */
    public DominioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
