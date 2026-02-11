package br.com.cervejaria.dominio.comum;

/**
 * Escala utilizada para medição de densidade.
 * 
 * <p>
 * As duas escalas mais comuns são:
 * </p>
 * <ul>
 * <li>SG (Specific Gravity): gravidade específica, tipicamente 1.000-1.150</li>
 * <li>Plato: graus Plato, tipicamente 0°-35°</li>
 * </ul>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public enum EscalaDensidade {

    /**
     * Gravidade Específica (Specific Gravity).
     * Faixa típica: 1.000 a 1.150
     */
    SG("Specific Gravity", 1.000, 1.200),

    /**
     * Graus Plato.
     * Faixa típica: 0° a 35°
     */
    PLATO("°Plato", 0.0, 40.0);

    private final String simbolo;
    private final double valorMinimo;
    private final double valorMaximo;

    EscalaDensidade(String simbolo, double valorMinimo, double valorMaximo) {
        this.simbolo = simbolo;
        this.valorMinimo = valorMinimo;
        this.valorMaximo = valorMaximo;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public double getValorMinimo() {
        return valorMinimo;
    }

    public double getValorMaximo() {
        return valorMaximo;
    }

    /**
     * Verifica se um valor está dentro da faixa válida para esta escala.
     * 
     * @param valor valor a verificar
     * @return true se o valor está na faixa válida
     */
    public boolean isValorValido(double valor) {
        return valor >= valorMinimo && valor <= valorMaximo;
    }

    /**
     * Converte um valor desta escala para outra.
     * 
     * @param valor   valor a converter
     * @param destino escala de destino
     * @return valor convertido
     */
    public double converterPara(double valor, EscalaDensidade destino) {
        if (this == destino) {
            return valor;
        }

        // Conversão SG -> Plato: Plato = -616.868 + 1111.14 * SG - 630.272 * SG² +
        // 135.997 * SG³
        // Conversão Plato -> SG: SG = 1 + (Plato / (258.6 - (Plato/258.2) * 227.1))

        if (this == SG && destino == PLATO) {
            // Fórmula simplificada: Plato ≈ (SG - 1) * 1000 / 4
            return (valor - 1.0) * 1000.0 / 4.0;
        } else {
            // Plato para SG
            return 1.0 + (valor / (258.6 - (valor / 258.2) * 227.1));
        }
    }

    @Override
    public String toString() {
        return simbolo;
    }
}
