package br.com.cervejaria.dominio.receita;

import br.com.cervejaria.dominio.comum.DominioException;

import java.time.Duration;
import java.util.Objects;

/**
 * Modelo de etapa de produção em uma receita.
 * 
 * <p>
 * Define o que deve acontecer em cada fase da produção:
 * tipo, duração esperada, temperatura alvo, instruções.
 * </p>
 * 
 * <p>
 * Esta é uma entidade de composição - não existe sem uma Receita.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class EtapaProducao {

    private final int ordem;
    private final TipoEtapaProducao tipo;
    private Duration duracaoEsperada;
    private Double temperaturaAlvo;
    private String instrucoes;

    /**
     * Cria uma nova etapa de produção.
     * 
     * @param ordem posição da etapa na sequência (1-based)
     * @param tipo  tipo da etapa
     */
    public EtapaProducao(int ordem, TipoEtapaProducao tipo) {
        if (ordem < 1) {
            throw new DominioException("Ordem da etapa deve ser >= 1");
        }
        if (tipo == null) {
            throw new DominioException("Tipo da etapa não pode ser nulo");
        }
        this.ordem = ordem;
        this.tipo = tipo;
    }

    /**
     * Cria uma etapa completa.
     */
    public EtapaProducao(int ordem, TipoEtapaProducao tipo, Duration duracaoEsperada,
            Double temperaturaAlvo, String instrucoes) {
        this(ordem, tipo);
        this.duracaoEsperada = duracaoEsperada;
        this.temperaturaAlvo = temperaturaAlvo;
        this.instrucoes = instrucoes;
    }

    public int getOrdem() {
        return ordem;
    }

    public TipoEtapaProducao getTipo() {
        return tipo;
    }

    public Duration getDuracaoEsperada() {
        return duracaoEsperada;
    }

    public void setDuracaoEsperada(Duration duracaoEsperada) {
        if (duracaoEsperada != null && duracaoEsperada.isNegative()) {
            throw new DominioException("Duração esperada não pode ser negativa");
        }
        this.duracaoEsperada = duracaoEsperada;
    }

    /**
     * Define a duração esperada em horas.
     */
    public void setDuracaoEsperadaEmHoras(long horas) {
        setDuracaoEsperada(Duration.ofHours(horas));
    }

    /**
     * Define a duração esperada em dias.
     */
    public void setDuracaoEsperadaEmDias(long dias) {
        setDuracaoEsperada(Duration.ofDays(dias));
    }

    public Double getTemperaturaAlvo() {
        return temperaturaAlvo;
    }

    public void setTemperaturaAlvo(Double temperaturaAlvo) {
        this.temperaturaAlvo = temperaturaAlvo;
    }

    public String getInstrucoes() {
        return instrucoes;
    }

    public void setInstrucoes(String instrucoes) {
        this.instrucoes = instrucoes;
    }

    /**
     * Retorna a duração esperada em horas, ou 0 se não definida.
     */
    public long getDuracaoEmHoras() {
        return duracaoEsperada != null ? duracaoEsperada.toHours() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EtapaProducao that = (EtapaProducao) o;
        return ordem == that.ordem && tipo == that.tipo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordem, tipo);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ordem).append(". ").append(tipo.getNome());
        if (duracaoEsperada != null) {
            long horas = duracaoEsperada.toHours();
            if (horas >= 24) {
                sb.append(" (").append(horas / 24).append("d)");
            } else {
                sb.append(" (").append(horas).append("h)");
            }
        }
        if (temperaturaAlvo != null) {
            sb.append(" @").append(temperaturaAlvo).append("°C");
        }
        return sb.toString();
    }
}
