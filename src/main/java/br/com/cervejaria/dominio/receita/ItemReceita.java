package br.com.cervejaria.dominio.receita;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.comum.Medida;
import br.com.cervejaria.dominio.insumo.Insumo;

import java.util.Objects;

/**
 * Item de uma receita: insumo + quantidade.
 * 
 * <p>
 * Representa a quantidade de um insumo específico necessária
 * para produzir a receita.
 * </p>
 * 
 * <p>
 * Esta é uma entidade de composição - não existe sem uma Receita.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class ItemReceita {

    private final Insumo insumo;
    private Medida quantidade;
    private String observacao;
    private Integer tempoAdicaoMinutos; // Para lúpulo: tempo de fervura

    /**
     * Cria um novo item de receita.
     * 
     * @param insumo     insumo a ser usado
     * @param quantidade quantidade do insumo
     */
    public ItemReceita(Insumo insumo, Medida quantidade) {
        if (insumo == null) {
            throw new DominioException("Insumo do item de receita não pode ser nulo");
        }
        if (quantidade == null) {
            throw new DominioException("Quantidade do item de receita não pode ser nula");
        }
        if (quantidade.isZero()) {
            throw new DominioException("Quantidade do item de receita não pode ser zero");
        }
        this.insumo = insumo;
        this.quantidade = quantidade;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public Medida getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Medida quantidade) {
        if (quantidade == null || quantidade.isZero()) {
            throw new DominioException("Quantidade do item não pode ser nula ou zero");
        }
        this.quantidade = quantidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    /**
     * Tempo de adição durante a fervura (para lúpulo).
     * Ex: 60 min = início da fervura, 0 min = flameout
     */
    public Integer getTempoAdicaoMinutos() {
        return tempoAdicaoMinutos;
    }

    public void setTempoAdicaoMinutos(Integer tempoAdicaoMinutos) {
        if (tempoAdicaoMinutos != null && tempoAdicaoMinutos < 0) {
            throw new DominioException("Tempo de adição não pode ser negativo");
        }
        this.tempoAdicaoMinutos = tempoAdicaoMinutos;
    }

    /**
     * Escala a quantidade deste item por um fator.
     * 
     * @param fator multiplicador
     * @return nova instância com quantidade escalada
     */
    public ItemReceita escalar(double fator) {
        ItemReceita novo = new ItemReceita(this.insumo, this.quantidade.multiplicarPor(fator));
        novo.setObservacao(this.observacao);
        novo.setTempoAdicaoMinutos(this.tempoAdicaoMinutos);
        return novo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ItemReceita that = (ItemReceita) o;
        return Objects.equals(insumo.getId(), that.insumo.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(insumo.getId());
    }

    @Override
    public String toString() {
        return String.format("%s: %s", insumo.getNome(), quantidade);
    }
}
