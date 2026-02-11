package br.com.cervejaria.dominio.lote;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.receita.EtapaProducao;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Registro de execução real de uma etapa em um lote.
 * 
 * <p>
 * Permite comparar "o que deveria acontecer" (EtapaProducao)
 * vs "o que aconteceu" (EtapaProducaoExecutada).
 * </p>
 * 
 * <p>
 * Esta é uma entidade de composição - pertence a um Lote.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class EtapaProducaoExecutada {

    private final int ordem;
    private final EtapaProducao modelo;

    private StatusEtapaExecutada status;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Double temperaturaReal;
    private String observacoes;

    /**
     * Cria uma nova etapa executada baseada em um modelo.
     * 
     * @param modelo etapa modelo da receita
     */
    public EtapaProducaoExecutada(EtapaProducao modelo) {
        if (modelo == null) {
            throw new DominioException("Modelo da etapa não pode ser nulo");
        }
        this.ordem = modelo.getOrdem();
        this.modelo = modelo;
        this.status = StatusEtapaExecutada.PENDENTE;
    }

    // ==================== GETTERS ====================

    public int getOrdem() {
        return ordem;
    }

    public EtapaProducao getModelo() {
        return modelo;
    }

    public StatusEtapaExecutada getStatus() {
        return status;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public Double getTemperaturaReal() {
        return temperaturaReal;
    }

    public String getObservacoes() {
        return observacoes;
    }

    // ==================== AÇÕES ====================

    /**
     * Inicia a execução desta etapa.
     */
    public void iniciar() {
        if (status != StatusEtapaExecutada.PENDENTE) {
            throw new DominioException(
                    String.format("Etapa '%s' não pode ser iniciada: status atual é %s",
                            modelo.getTipo(), status));
        }
        this.status = StatusEtapaExecutada.EM_ANDAMENTO;
        this.dataInicio = LocalDateTime.now();
    }

    /**
     * Conclui a execução desta etapa.
     * 
     * @param temperaturaReal temperatura real observada (opcional)
     * @param observacoes     observações do operador (opcional)
     */
    public void concluir(Double temperaturaReal, String observacoes) {
        if (status != StatusEtapaExecutada.EM_ANDAMENTO) {
            throw new DominioException(
                    String.format("Etapa '%s' não pode ser concluída: status atual é %s",
                            modelo.getTipo(), status));
        }
        this.status = StatusEtapaExecutada.CONCLUIDA;
        this.dataFim = LocalDateTime.now();
        this.temperaturaReal = temperaturaReal;
        this.observacoes = observacoes;
    }

    /**
     * Conclui a etapa indicando que houve problemas.
     */
    public void concluirComProblemas(String observacoes) {
        if (status != StatusEtapaExecutada.EM_ANDAMENTO) {
            throw new DominioException(
                    String.format("Etapa '%s' não pode ser concluída: status atual é %s",
                            modelo.getTipo(), status));
        }
        if (observacoes == null || observacoes.isBlank()) {
            throw new DominioException("Observações são obrigatórias ao concluir com problemas");
        }
        this.status = StatusEtapaExecutada.COM_PROBLEMAS;
        this.dataFim = LocalDateTime.now();
        this.observacoes = observacoes;
    }

    // ==================== CONSULTAS ====================

    /**
     * Verifica se a etapa está pendente.
     */
    public boolean isPendente() {
        return status == StatusEtapaExecutada.PENDENTE;
    }

    /**
     * Verifica se a etapa está em andamento.
     */
    public boolean isEmAndamento() {
        return status == StatusEtapaExecutada.EM_ANDAMENTO;
    }

    /**
     * Verifica se a etapa está finalizada (concluída ou com problemas).
     */
    public boolean isFinalizada() {
        return status.isFinalizada();
    }

    /**
     * Retorna a duração real da etapa, ou null se não finalizada.
     */
    public Duration getDuracaoReal() {
        if (dataInicio == null || dataFim == null) {
            return null;
        }
        return Duration.between(dataInicio, dataFim);
    }

    /**
     * Retorna a diferença entre a duração real e a esperada.
     * Positivo = demorou mais, Negativo = terminou antes.
     */
    public Duration getDiferencaDuracao() {
        Duration real = getDuracaoReal();
        Duration esperada = modelo.getDuracaoEsperada();
        if (real == null || esperada == null) {
            return null;
        }
        return real.minus(esperada);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EtapaProducaoExecutada that = (EtapaProducaoExecutada) o;
        return ordem == that.ordem;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordem);
    }

    @Override
    public String toString() {
        return String.format("%d. %s [%s]", ordem, modelo.getTipo(), status);
    }
}
