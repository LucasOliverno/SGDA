package br.com.cervejaria.dominio.comum;

import java.util.Objects;

/**
 * Value Object que representa uma quantidade associada a uma unidade de medida.
 * 
 * <p>
 * Exemplos de uso:
 * </p>
 * <ul>
 * <li>5.0 kg de malte</li>
 * <li>50 g de lúpulo</li>
 * <li>20 L de água</li>
 * </ul>
 * 
 * <p>
 * Este objeto é imutável. Operações de conversão retornam novas instâncias.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public final class Medida {

    private final double valor;
    private final UnidadeMedida unidade;

    /**
     * Cria uma nova medida com valor e unidade especificados.
     * 
     * @param valor   valor numérico da medida (deve ser >= 0)
     * @param unidade unidade de medida (não pode ser nula)
     * @throws DominioException se o valor for negativo ou a unidade for nula
     */
    public Medida(double valor, UnidadeMedida unidade) {
        validar(valor, unidade);
        this.valor = valor;
        this.unidade = unidade;
    }

    private void validar(double valor, UnidadeMedida unidade) {
        if (unidade == null) {
            throw new DominioException("Unidade de medida não pode ser nula");
        }
        if (valor < 0) {
            throw new DominioException(
                    String.format("Valor da medida não pode ser negativo: %.4f %s", valor, unidade.getSimbolo()));
        }
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new DominioException("Valor da medida deve ser um número válido");
        }
    }

    /**
     * Retorna o valor numérico da medida.
     * 
     * @return valor da medida
     */
    public double getValor() {
        return valor;
    }

    /**
     * Retorna a unidade de medida.
     * 
     * @return unidade
     */
    public UnidadeMedida getUnidade() {
        return unidade;
    }

    /**
     * Verifica se esta medida representa zero.
     * 
     * @return true se o valor for zero
     */
    public boolean isZero() {
        return valor == 0.0;
    }

    /**
     * Converte esta medida para outra unidade compatível.
     * 
     * @param novaUnidade unidade de destino
     * @return nova instância de Medida com o valor convertido
     * @throws DominioException se as unidades não forem compatíveis
     */
    public Medida converterPara(UnidadeMedida novaUnidade) {
        if (novaUnidade == null) {
            throw new DominioException("Unidade de destino não pode ser nula");
        }
        double valorConvertido = unidade.converterPara(valor, novaUnidade);
        return new Medida(valorConvertido, novaUnidade);
    }

    /**
     * Soma esta medida com outra, retornando uma nova instância.
     * As medidas devem ter unidades compatíveis.
     * O resultado terá a unidade desta medida.
     * 
     * @param outra medida a somar
     * @return nova medida com a soma
     * @throws DominioException se as unidades não forem compatíveis
     */
    public Medida somar(Medida outra) {
        if (outra == null) {
            throw new DominioException("Medida a somar não pode ser nula");
        }
        Medida outraConvertida = outra.converterPara(this.unidade);
        return new Medida(this.valor + outraConvertida.valor, this.unidade);
    }

    /**
     * Subtrai outra medida desta, retornando uma nova instância.
     * As medidas devem ter unidades compatíveis.
     * O resultado terá a unidade desta medida.
     * 
     * @param outra medida a subtrair
     * @return nova medida com a diferença
     * @throws DominioException se as unidades não forem compatíveis ou resultado
     *                          negativo
     */
    public Medida subtrair(Medida outra) {
        if (outra == null) {
            throw new DominioException("Medida a subtrair não pode ser nula");
        }
        Medida outraConvertida = outra.converterPara(this.unidade);
        double diferenca = this.valor - outraConvertida.valor;
        if (diferenca < 0) {
            throw new DominioException(
                    String.format("Subtração resultaria em valor negativo: %.4f - %.4f = %.4f %s",
                            this.valor, outraConvertida.valor, diferenca, this.unidade.getSimbolo()));
        }
        return new Medida(diferenca, this.unidade);
    }

    /**
     * Multiplica esta medida por um fator, retornando uma nova instância.
     * 
     * @param fator multiplicador (deve ser >= 0)
     * @return nova medida multiplicada
     * @throws DominioException se o fator for negativo
     */
    public Medida multiplicarPor(double fator) {
        if (fator < 0) {
            throw new DominioException("Fator de multiplicação não pode ser negativo: " + fator);
        }
        return new Medida(this.valor * fator, this.unidade);
    }

    /**
     * Verifica se esta medida é maior que outra.
     * As medidas devem ter unidades compatíveis.
     * 
     * @param outra medida para comparação
     * @return true se esta medida for maior
     * @throws DominioException se as unidades não forem compatíveis
     */
    public boolean isMaiorQue(Medida outra) {
        if (outra == null) {
            return true;
        }
        Medida outraConvertida = outra.converterPara(this.unidade);
        return this.valor > outraConvertida.valor;
    }

    /**
     * Verifica se esta medida é maior ou igual a outra.
     * As medidas devem ter unidades compatíveis.
     * 
     * @param outra medida para comparação
     * @return true se esta medida for maior ou igual
     * @throws DominioException se as unidades não forem compatíveis
     */
    public boolean isMaiorOuIgualA(Medida outra) {
        if (outra == null) {
            return true;
        }
        Medida outraConvertida = outra.converterPara(this.unidade);
        return this.valor >= outraConvertida.valor;
    }

    // Factory methods para conveniência

    /**
     * Cria uma medida em quilogramas.
     */
    public static Medida quilogramas(double valor) {
        return new Medida(valor, UnidadeMedida.QUILOGRAMA);
    }

    /**
     * Cria uma medida em gramas.
     */
    public static Medida gramas(double valor) {
        return new Medida(valor, UnidadeMedida.GRAMA);
    }

    /**
     * Cria uma medida em litros.
     */
    public static Medida litros(double valor) {
        return new Medida(valor, UnidadeMedida.LITRO);
    }

    /**
     * Cria uma medida em mililitros.
     */
    public static Medida mililitros(double valor) {
        return new Medida(valor, UnidadeMedida.MILILITRO);
    }

    /**
     * Cria uma medida em unidades.
     */
    public static Medida unidades(double valor) {
        return new Medida(valor, UnidadeMedida.UNIDADE);
    }

    /**
     * Cria uma medida zero na unidade especificada.
     */
    public static Medida zero(UnidadeMedida unidade) {
        return new Medida(0.0, unidade);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Medida medida = (Medida) o;
        // Compara convertendo para a mesma unidade base
        if (!this.unidade.isCompativelCom(medida.unidade)) {
            return false;
        }
        UnidadeMedida unidadeBase = this.unidade.getUnidadeBase();
        double valorBase1 = this.unidade.converterPara(this.valor, unidadeBase);
        double valorBase2 = medida.unidade.converterPara(medida.valor, unidadeBase);
        // Tolerância para comparação de doubles
        return Math.abs(valorBase1 - valorBase2) < 0.0001;
    }

    @Override
    public int hashCode() {
        // Usa a unidade base e valor convertido para hash consistente
        UnidadeMedida unidadeBase = unidade.getUnidadeBase();
        double valorBase = unidade.converterPara(valor, unidadeBase);
        return Objects.hash(Math.round(valorBase * 10000), unidadeBase);
    }

    @Override
    public String toString() {
        if (valor == (long) valor) {
            return String.format("%.0f %s", valor, unidade.getSimbolo());
        }
        return String.format("%.2f %s", valor, unidade.getSimbolo());
    }
}
