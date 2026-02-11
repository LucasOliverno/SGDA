package br.com.cervejaria.dominio.comum;

import java.util.Objects;

/**
 * Value Object que representa uma medição de densidade do mosto ou cerveja.
 * 
 * <p>
 * A densidade é fundamental para:
 * </p>
 * <ul>
 * <li>Calcular o teor alcoólico (ABV)</li>
 * <li>Monitorar o progresso da fermentação</li>
 * <li>Verificar eficiência da brassagem</li>
 * </ul>
 * 
 * <p>
 * Este objeto é imutável.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public final class Densidade {

    private final double valor;
    private final TipoDensidade tipo;
    private final EscalaDensidade escala;

    /**
     * Cria uma nova densidade.
     * 
     * @param valor  valor da densidade
     * @param tipo   tipo de medição (OG ou FG)
     * @param escala escala utilizada (SG ou Plato)
     * @throws DominioException se os valores forem inválidos
     */
    public Densidade(double valor, TipoDensidade tipo, EscalaDensidade escala) {
        validar(valor, tipo, escala);
        this.valor = valor;
        this.tipo = tipo;
        this.escala = escala;
    }

    private void validar(double valor, TipoDensidade tipo, EscalaDensidade escala) {
        if (tipo == null) {
            throw new DominioException("Tipo de densidade não pode ser nulo");
        }
        if (escala == null) {
            throw new DominioException("Escala de densidade não pode ser nula");
        }
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new DominioException("Valor da densidade deve ser um número válido");
        }
        if (!escala.isValorValido(valor)) {
            throw new DominioException(
                    String.format("Valor de densidade %.4f está fora da faixa válida para %s (%.3f - %.3f)",
                            valor, escala.getSimbolo(), escala.getValorMinimo(), escala.getValorMaximo()));
        }
    }

    /**
     * Retorna o valor numérico da densidade.
     */
    public double getValor() {
        return valor;
    }

    /**
     * Retorna o tipo de medição (OG ou FG).
     */
    public TipoDensidade getTipo() {
        return tipo;
    }

    /**
     * Retorna a escala utilizada (SG ou Plato).
     */
    public EscalaDensidade getEscala() {
        return escala;
    }

    /**
     * Verifica se esta é uma densidade original (OG).
     */
    public boolean isOG() {
        return tipo == TipoDensidade.OG;
    }

    /**
     * Verifica se esta é uma densidade final (FG).
     */
    public boolean isFG() {
        return tipo == TipoDensidade.FG;
    }

    /**
     * Converte esta densidade para outra escala.
     * 
     * @param novaEscala escala de destino
     * @return nova instância com o valor convertido
     */
    public Densidade converterPara(EscalaDensidade novaEscala) {
        if (novaEscala == null) {
            throw new DominioException("Escala de destino não pode ser nula");
        }
        if (novaEscala == this.escala) {
            return this;
        }
        double valorConvertido = escala.converterPara(valor, novaEscala);
        return new Densidade(valorConvertido, tipo, novaEscala);
    }

    /**
     * Retorna o valor em SG, convertendo se necessário.
     */
    public double getValorEmSG() {
        if (escala == EscalaDensidade.SG) {
            return valor;
        }
        return escala.converterPara(valor, EscalaDensidade.SG);
    }

    /**
     * Retorna o valor em Plato, convertendo se necessário.
     */
    public double getValorEmPlato() {
        if (escala == EscalaDensidade.PLATO) {
            return valor;
        }
        return escala.converterPara(valor, EscalaDensidade.PLATO);
    }

    /**
     * Verifica se esta densidade é maior que outra.
     * Compara convertendo para a mesma escala (SG).
     * 
     * @param outra densidade para comparação
     * @return true se esta for maior
     */
    public boolean isMaiorQue(Densidade outra) {
        if (outra == null) {
            return true;
        }
        return this.getValorEmSG() > outra.getValorEmSG();
    }

    /**
     * Verifica se esta densidade é menor que outra.
     * Compara convertendo para a mesma escala (SG).
     * 
     * @param outra densidade para comparação
     * @return true se esta for menor
     */
    public boolean isMenorQue(Densidade outra) {
        if (outra == null) {
            return false;
        }
        return this.getValorEmSG() < outra.getValorEmSG();
    }

    // Factory methods para conveniência

    /**
     * Cria uma OG em escala SG.
     */
    public static Densidade ogEmSG(double valor) {
        return new Densidade(valor, TipoDensidade.OG, EscalaDensidade.SG);
    }

    /**
     * Cria uma FG em escala SG.
     */
    public static Densidade fgEmSG(double valor) {
        return new Densidade(valor, TipoDensidade.FG, EscalaDensidade.SG);
    }

    /**
     * Cria uma OG em escala Plato.
     */
    public static Densidade ogEmPlato(double valor) {
        return new Densidade(valor, TipoDensidade.OG, EscalaDensidade.PLATO);
    }

    /**
     * Cria uma FG em escala Plato.
     */
    public static Densidade fgEmPlato(double valor) {
        return new Densidade(valor, TipoDensidade.FG, EscalaDensidade.PLATO);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Densidade densidade = (Densidade) o;
        // Compara em SG para consistência entre escalas diferentes
        double valorSG1 = this.getValorEmSG();
        double valorSG2 = densidade.getValorEmSG();
        return tipo == densidade.tipo && Math.abs(valorSG1 - valorSG2) < 0.0001;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipo, Math.round(getValorEmSG() * 10000));
    }

    @Override
    public String toString() {
        String valorFormatado = escala == EscalaDensidade.SG
                ? String.format("%.3f", valor)
                : String.format("%.1f", valor);
        return String.format("%s: %s %s", tipo.name(), valorFormatado, escala.getSimbolo());
    }
}
