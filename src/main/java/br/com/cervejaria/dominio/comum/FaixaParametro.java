package br.com.cervejaria.dominio.comum;

import java.util.Objects;

/**
 * Value Object que representa uma faixa válida de valores para um parâmetro.
 * 
 * <p>
 * Utilizado para definir limites aceitáveis em estilos de cerveja, como:
 * </p>
 * <ul>
 * <li>IBU: 40-70</li>
 * <li>ABV: 5.5%-7.5%</li>
 * <li>Temperatura de fermentação: 18-22°C</li>
 * </ul>
 * 
 * <p>
 * Este objeto é imutável.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public final class FaixaParametro {

    private final double minimo;
    private final double maximo;
    private final String unidade;

    /**
     * Cria uma nova faixa de parâmetro.
     * 
     * @param minimo  valor mínimo da faixa
     * @param maximo  valor máximo da faixa (deve ser >= mínimo)
     * @param unidade descrição da unidade (ex: "IBU", "%", "°C")
     * @throws DominioException se os valores forem inválidos
     */
    public FaixaParametro(double minimo, double maximo, String unidade) {
        validar(minimo, maximo, unidade);
        this.minimo = minimo;
        this.maximo = maximo;
        this.unidade = unidade;
    }

    private void validar(double minimo, double maximo, String unidade) {
        if (Double.isNaN(minimo) || Double.isInfinite(minimo)) {
            throw new DominioException("Valor mínimo deve ser um número válido");
        }
        if (Double.isNaN(maximo) || Double.isInfinite(maximo)) {
            throw new DominioException("Valor máximo deve ser um número válido");
        }
        if (minimo > maximo) {
            throw new DominioException(
                    String.format("Valor mínimo (%.2f) não pode ser maior que o máximo (%.2f)", minimo, maximo));
        }
        if (unidade == null || unidade.isBlank()) {
            throw new DominioException("Unidade da faixa de parâmetro não pode ser nula ou vazia");
        }
    }

    /**
     * Retorna o valor mínimo da faixa.
     */
    public double getMinimo() {
        return minimo;
    }

    /**
     * Retorna o valor máximo da faixa.
     */
    public double getMaximo() {
        return maximo;
    }

    /**
     * Retorna a descrição da unidade.
     */
    public String getUnidade() {
        return unidade;
    }

    /**
     * Retorna a amplitude da faixa (máximo - mínimo).
     */
    public double getAmplitude() {
        return maximo - minimo;
    }

    /**
     * Retorna o ponto médio da faixa.
     */
    public double getPontoMedio() {
        return (minimo + maximo) / 2.0;
    }

    /**
     * Verifica se um valor está dentro da faixa (inclusive).
     * 
     * @param valor valor a verificar
     * @return true se o valor está entre mínimo e máximo (inclusive)
     */
    public boolean contem(double valor) {
        return valor >= minimo && valor <= maximo;
    }

    /**
     * Verifica se um valor está abaixo da faixa.
     * 
     * @param valor valor a verificar
     * @return true se o valor é menor que o mínimo
     */
    public boolean isAbaixoDaFaixa(double valor) {
        return valor < minimo;
    }

    /**
     * Verifica se um valor está acima da faixa.
     * 
     * @param valor valor a verificar
     * @return true se o valor é maior que o máximo
     */
    public boolean isAcimaDaFaixa(double valor) {
        return valor > maximo;
    }

    /**
     * Retorna a distância de um valor até a faixa.
     * Se o valor está dentro da faixa, retorna 0.
     * Se está abaixo, retorna a diferença para o mínimo (negativo).
     * Se está acima, retorna a diferença para o máximo (positivo).
     * 
     * @param valor valor a verificar
     * @return distância até a faixa
     */
    public double distanciaAte(double valor) {
        if (contem(valor)) {
            return 0.0;
        }
        if (valor < minimo) {
            return valor - minimo; // negativo
        }
        return valor - maximo; // positivo
    }

    /**
     * Verifica se esta faixa tem interseção com outra.
     * 
     * @param outra faixa para verificar interseção
     * @return true se as faixas têm alguma sobreposição
     */
    public boolean interseciona(FaixaParametro outra) {
        if (outra == null) {
            return false;
        }
        return this.minimo <= outra.maximo && this.maximo >= outra.minimo;
    }

    // Factory methods para faixas comuns

    /**
     * Cria uma faixa para IBU (International Bitterness Units).
     */
    public static FaixaParametro ibu(double minimo, double maximo) {
        return new FaixaParametro(minimo, maximo, "IBU");
    }

    /**
     * Cria uma faixa para ABV (Alcohol By Volume).
     */
    public static FaixaParametro abv(double minimo, double maximo) {
        return new FaixaParametro(minimo, maximo, "%");
    }

    /**
     * Cria uma faixa para temperatura em Celsius.
     */
    public static FaixaParametro temperaturaCelsius(double minimo, double maximo) {
        return new FaixaParametro(minimo, maximo, "°C");
    }

    /**
     * Cria uma faixa para cor em SRM.
     */
    public static FaixaParametro corSRM(double minimo, double maximo) {
        return new FaixaParametro(minimo, maximo, "SRM");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FaixaParametro that = (FaixaParametro) o;
        return Double.compare(that.minimo, minimo) == 0
                && Double.compare(that.maximo, maximo) == 0
                && Objects.equals(unidade, that.unidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimo, maximo, unidade);
    }

    @Override
    public String toString() {
        String formatoMinimo = minimo == (long) minimo ? "%.0f" : "%.1f";
        String formatoMaximo = maximo == (long) maximo ? "%.0f" : "%.1f";
        return String.format(formatoMinimo + " - " + formatoMaximo + " %s", minimo, maximo, unidade);
    }
}
