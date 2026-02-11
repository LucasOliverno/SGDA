package br.com.cervejaria.dominio.comum;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa informações de contato.
 * 
 * <p>
 * Utilizado para armazenar dados de contato de fornecedores.
 * </p>
 * 
 * <p>
 * Este objeto é imutável. Ao menos um dos campos (telefone, email, endereço)
 * deve ser preenchido.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public final class Contato {

    // Padrão simples para validação de e-mail
    private static final Pattern PADRAO_EMAIL = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Padrão para telefone brasileiro (aceita formatos variados)
    private static final Pattern PADRAO_TELEFONE = Pattern.compile(
            "^[\\d\\s()+-]{8,20}$");

    private final String telefone;
    private final String email;
    private final String endereco;

    /**
     * Cria um novo contato.
     * 
     * <p>
     * Ao menos um dos campos deve ser preenchido (não nulo e não vazio).
     * </p>
     * 
     * @param telefone número de telefone (opcional)
     * @param email    endereço de e-mail (opcional, validado se fornecido)
     * @param endereco endereço físico (opcional)
     * @throws DominioException se todos os campos forem vazios ou se o e-mail for
     *                          inválido
     */
    public Contato(String telefone, String email, String endereco) {
        this.telefone = normalizarTexto(telefone);
        this.email = normalizarTexto(email);
        this.endereco = normalizarTexto(endereco);
        validar();
    }

    private String normalizarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim();
    }

    private void validar() {
        boolean temTelefone = telefone != null && !telefone.isEmpty();
        boolean temEmail = email != null && !email.isEmpty();
        boolean temEndereco = endereco != null && !endereco.isEmpty();

        if (!temTelefone && !temEmail && !temEndereco) {
            throw new DominioException(
                    "Contato deve ter ao menos um campo preenchido (telefone, e-mail ou endereço)");
        }

        if (temTelefone && !PADRAO_TELEFONE.matcher(telefone).matches()) {
            throw new DominioException(
                    String.format("Formato de telefone inválido: %s", telefone));
        }

        if (temEmail && !PADRAO_EMAIL.matcher(email).matches()) {
            throw new DominioException(
                    String.format("Formato de e-mail inválido: %s", email));
        }

        if (temEndereco && endereco.length() < 5) {
            throw new DominioException("Endereço deve ter ao menos 5 caracteres");
        }
    }

    /**
     * Retorna o número de telefone.
     * 
     * @return telefone ou null se não informado
     */
    public String getTelefone() {
        return telefone;
    }

    /**
     * Retorna o endereço de e-mail.
     * 
     * @return e-mail ou null se não informado
     */
    public String getEmail() {
        return email;
    }

    /**
     * Retorna o endereço físico.
     * 
     * @return endereço ou null se não informado
     */
    public String getEndereco() {
        return endereco;
    }

    /**
     * Verifica se o telefone foi informado.
     */
    public boolean temTelefone() {
        return telefone != null;
    }

    /**
     * Verifica se o e-mail foi informado.
     */
    public boolean temEmail() {
        return email != null;
    }

    /**
     * Verifica se o endereço foi informado.
     */
    public boolean temEndereco() {
        return endereco != null;
    }

    /**
     * Retorna uma cópia deste contato com telefone atualizado.
     * 
     * @param novoTelefone novo número de telefone
     * @return nova instância de Contato
     */
    public Contato comTelefone(String novoTelefone) {
        return new Contato(novoTelefone, this.email, this.endereco);
    }

    /**
     * Retorna uma cópia deste contato com e-mail atualizado.
     * 
     * @param novoEmail novo endereço de e-mail
     * @return nova instância de Contato
     */
    public Contato comEmail(String novoEmail) {
        return new Contato(this.telefone, novoEmail, this.endereco);
    }

    /**
     * Retorna uma cópia deste contato com endereço atualizado.
     * 
     * @param novoEndereco novo endereço físico
     * @return nova instância de Contato
     */
    public Contato comEndereco(String novoEndereco) {
        return new Contato(this.telefone, this.email, novoEndereco);
    }

    // Factory methods

    /**
     * Cria um contato apenas com telefone.
     */
    public static Contato apenasTelefone(String telefone) {
        return new Contato(telefone, null, null);
    }

    /**
     * Cria um contato apenas com e-mail.
     */
    public static Contato apenasEmail(String email) {
        return new Contato(null, email, null);
    }

    /**
     * Cria um contato apenas com endereço.
     */
    public static Contato apenasEndereco(String endereco) {
        return new Contato(null, null, endereco);
    }

    /**
     * Cria um contato completo.
     */
    public static Contato completo(String telefone, String email, String endereco) {
        return new Contato(telefone, email, endereco);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Contato contato = (Contato) o;
        return Objects.equals(telefone, contato.telefone)
                && Objects.equals(email, contato.email)
                && Objects.equals(endereco, contato.endereco);
    }

    @Override
    public int hashCode() {
        return Objects.hash(telefone, email, endereco);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Contato{");
        boolean primeiro = true;

        if (telefone != null) {
            sb.append("tel=").append(telefone);
            primeiro = false;
        }
        if (email != null) {
            if (!primeiro)
                sb.append(", ");
            sb.append("email=").append(email);
            primeiro = false;
        }
        if (endereco != null) {
            if (!primeiro)
                sb.append(", ");
            sb.append("end=").append(endereco);
        }

        sb.append("}");
        return sb.toString();
    }
}
