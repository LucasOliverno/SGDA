package br.com.cervejaria.dominio.comum;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object que representa um período de tempo com início e fim.
 * 
 * <p>
 * Utilizado para:
 * </p>
 * <ul>
 * <li>Duração de etapas de produção</li>
 * <li>Período de fermentação</li>
 * <li>Período de maturação</li>
 * </ul>
 * 
 * <p>
 * Este objeto é imutável.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public final class Periodo {

    private final LocalDateTime inicio;
    private final LocalDateTime fim;

    /**
     * Cria um novo período com início e fim especificados.
     * 
     * @param inicio data/hora de início (não pode ser nulo)
     * @param fim    data/hora de fim (não pode ser nulo, deve ser >= início)
     * @throws DominioException se os valores forem inválidos
     */
    public Periodo(LocalDateTime inicio, LocalDateTime fim) {
        validar(inicio, fim);
        this.inicio = inicio;
        this.fim = fim;
    }

    private void validar(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null) {
            throw new DominioException("Data/hora de início do período não pode ser nula");
        }
        if (fim == null) {
            throw new DominioException("Data/hora de fim do período não pode ser nula");
        }
        if (fim.isBefore(inicio)) {
            throw new DominioException(
                    String.format("Data de fim (%s) não pode ser anterior à data de início (%s)", fim, inicio));
        }
    }

    /**
     * Retorna a data/hora de início.
     */
    public LocalDateTime getInicio() {
        return inicio;
    }

    /**
     * Retorna a data/hora de fim.
     */
    public LocalDateTime getFim() {
        return fim;
    }

    /**
     * Retorna a duração do período.
     */
    public Duration getDuracao() {
        return Duration.between(inicio, fim);
    }

    /**
     * Retorna a duração em horas.
     */
    public long getDuracaoEmHoras() {
        return getDuracao().toHours();
    }

    /**
     * Retorna a duração em dias.
     */
    public long getDuracaoEmDias() {
        return getDuracao().toDays();
    }

    /**
     * Verifica se uma data/hora está contida neste período (inclusive).
     * 
     * @param dataHora data/hora a verificar
     * @return true se está no período
     */
    public boolean contem(LocalDateTime dataHora) {
        if (dataHora == null) {
            return false;
        }
        return !dataHora.isBefore(inicio) && !dataHora.isAfter(fim);
    }

    /**
     * Verifica se este período tem interseção com outro.
     * 
     * @param outro período para verificar
     * @return true se há sobreposição
     */
    public boolean interseciona(Periodo outro) {
        if (outro == null) {
            return false;
        }
        return !this.fim.isBefore(outro.inicio) && !this.inicio.isAfter(outro.fim);
    }

    /**
     * Verifica se este período está completamente contido em outro.
     * 
     * @param outro período container
     * @return true se este período está dentro do outro
     */
    public boolean estaDentroDe(Periodo outro) {
        if (outro == null) {
            return false;
        }
        return !this.inicio.isBefore(outro.inicio) && !this.fim.isAfter(outro.fim);
    }

    /**
     * Verifica se este período já terminou (fim está no passado).
     * 
     * @return true se o período já terminou
     */
    public boolean jaTerminou() {
        return fim.isBefore(LocalDateTime.now());
    }

    /**
     * Verifica se este período ainda não começou (início está no futuro).
     * 
     * @return true se o período ainda não começou
     */
    public boolean aindaNaoComecou() {
        return inicio.isAfter(LocalDateTime.now());
    }

    /**
     * Verifica se estamos atualmente dentro deste período.
     * 
     * @return true se o momento atual está dentro do período
     */
    public boolean emAndamento() {
        return contem(LocalDateTime.now());
    }

    // Factory methods

    /**
     * Cria um período que começa agora e dura o número especificado de horas.
     */
    public static Periodo aPartirDeAgora(Duration duracao) {
        if (duracao == null || duracao.isNegative() || duracao.isZero()) {
            throw new DominioException("Duração deve ser positiva");
        }
        LocalDateTime agora = LocalDateTime.now();
        return new Periodo(agora, agora.plus(duracao));
    }

    /**
     * Cria um período que começa em uma data e dura o número especificado de dias.
     */
    public static Periodo deDias(LocalDateTime inicio, int dias) {
        if (dias < 0) {
            throw new DominioException("Número de dias não pode ser negativo");
        }
        return new Periodo(inicio, inicio.plusDays(dias));
    }

    /**
     * Cria um período que começa em uma data e dura o número especificado de horas.
     */
    public static Periodo deHoras(LocalDateTime inicio, int horas) {
        if (horas < 0) {
            throw new DominioException("Número de horas não pode ser negativo");
        }
        return new Periodo(inicio, inicio.plusHours(horas));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Periodo periodo = (Periodo) o;
        return Objects.equals(inicio, periodo.inicio) && Objects.equals(fim, periodo.fim);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inicio, fim);
    }

    @Override
    public String toString() {
        long dias = getDuracaoEmDias();
        long horas = getDuracaoEmHoras() % 24;

        String duracao;
        if (dias > 0 && horas > 0) {
            duracao = String.format("%dd %dh", dias, horas);
        } else if (dias > 0) {
            duracao = String.format("%dd", dias);
        } else {
            duracao = String.format("%dh", getDuracaoEmHoras());
        }

        return String.format("%s até %s (%s)", inicio, fim, duracao);
    }
}
