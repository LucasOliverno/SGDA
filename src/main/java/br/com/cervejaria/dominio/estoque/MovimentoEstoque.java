package br.com.cervejaria.dominio.estoque;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.comum.Medida;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Registro de movimento (entrada ou saída) de estoque.
 * 
 * <p>
 * Permite rastreabilidade completa das movimentações.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class MovimentoEstoque {

    private final String id;
    private final LocalDateTime dataHora;
    private final MotivoMovimento motivo;
    private final Medida quantidade;

    private String referencia; // Número NF, etc
    private String loteId; // ID do lote se for uso em produção
    private String observacao;

    /**
     * Cria um novo movimento de estoque.
     * 
     * @param motivo     motivo do movimento
     * @param quantidade quantidade movimentada
     */
    public MovimentoEstoque(MotivoMovimento motivo, Medida quantidade) {
        if (motivo == null) {
            throw new DominioException("Motivo do movimento não pode ser nulo");
        }
        if (quantidade == null || quantidade.isZero()) {
            throw new DominioException("Quantidade do movimento deve ser maior que zero");
        }

        this.id = UUID.randomUUID().toString();
        this.dataHora = LocalDateTime.now();
        this.motivo = motivo;
        this.quantidade = quantidade;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public MotivoMovimento getMotivo() {
        return motivo;
    }

    public TipoMovimentoEstoque getTipo() {
        return motivo.getTipo();
    }

    public Medida getQuantidade() {
        return quantidade;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getLoteId() {
        return loteId;
    }

    public void setLoteId(String loteId) {
        this.loteId = loteId;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean isEntrada() {
        return motivo.isEntrada();
    }

    public boolean isSaida() {
        return motivo.isSaida();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MovimentoEstoque that = (MovimentoEstoque) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s %s: %s em %s",
                motivo.getTipo(), quantidade, motivo, dataHora.toLocalDate());
    }
}
