package br.com.cervejaria.dominio.estoque;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.comum.Medida;
import br.com.cervejaria.dominio.comum.UnidadeMedida;
import br.com.cervejaria.dominio.insumo.Insumo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidade que representa o estoque de um insumo.
 * 
 * <p>
 * Controla a quantidade disponível e mantém histórico de movimentações.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class Estoque {

    private final Insumo insumo;
    private Medida quantidadeAtual;
    private Medida quantidadeMinima; // Ponto de reposição
    private LocalDateTime ultimaAtualizacao;
    private String localizacao;

    private final List<MovimentoEstoque> movimentos;

    /**
     * Cria um novo registro de estoque para um insumo.
     * 
     * @param insumo insumo a controlar
     */
    public Estoque(Insumo insumo) {
        if (insumo == null) {
            throw new DominioException("Insumo não pode ser nulo");
        }
        this.insumo = insumo;
        this.quantidadeAtual = Medida.zero(insumo.getUnidadePadrao());
        this.movimentos = new ArrayList<>();
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public Medida getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public Medida getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public void setQuantidadeMinima(Medida quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public LocalDateTime getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public List<MovimentoEstoque> getMovimentos() {
        return Collections.unmodifiableList(movimentos);
    }

    // ==================== OPERAÇÕES ====================

    /**
     * Registra uma entrada de estoque.
     * 
     * @param quantidade quantidade a adicionar
     * @param motivo     motivo da entrada
     * @param referencia referência externa (ex: número NF)
     * @return movimento criado
     */
    public MovimentoEstoque registrarEntrada(Medida quantidade, MotivoMovimento motivo, String referencia) {
        if (!motivo.isEntrada()) {
            throw new DominioException("Motivo " + motivo + " não é de entrada");
        }
        return registrarMovimento(quantidade, motivo, referencia, null);
    }

    /**
     * Registra uma saída de estoque.
     * 
     * @param quantidade quantidade a retirar
     * @param motivo     motivo da saída
     * @param loteId     ID do lote (se for uso em produção)
     * @return movimento criado
     */
    public MovimentoEstoque registrarSaida(Medida quantidade, MotivoMovimento motivo, String loteId) {
        if (!motivo.isSaida()) {
            throw new DominioException("Motivo " + motivo + " não é de saída");
        }

        // Verifica se há estoque suficiente
        if (quantidade.isMaiorQue(quantidadeAtual)) {
            throw new DominioException(
                    String.format("Estoque insuficiente de '%s'. Disponível: %s, Solicitado: %s",
                            insumo.getNome(), quantidadeAtual, quantidade));
        }

        return registrarMovimento(quantidade, motivo, null, loteId);
    }

    private MovimentoEstoque registrarMovimento(Medida quantidade, MotivoMovimento motivo,
            String referencia, String loteId) {
        if (quantidade == null || quantidade.isZero()) {
            throw new DominioException("Quantidade do movimento deve ser maior que zero");
        }

        // Atualiza quantidade
        if (motivo.isEntrada()) {
            quantidadeAtual = quantidadeAtual.somar(quantidade);
        } else {
            quantidadeAtual = quantidadeAtual.subtrair(quantidade);
        }

        // Cria movimento
        MovimentoEstoque movimento = new MovimentoEstoque(motivo, quantidade);
        movimento.setReferencia(referencia);
        movimento.setLoteId(loteId);
        movimentos.add(movimento);

        ultimaAtualizacao = LocalDateTime.now();

        return movimento;
    }

    // ==================== CONSULTAS ====================

    /**
     * Verifica se o estoque está abaixo do mínimo.
     */
    public boolean isAbaixoDoMinimo() {
        if (quantidadeMinima == null) {
            return false;
        }
        return !quantidadeAtual.isMaiorOuIgualA(quantidadeMinima);
    }

    /**
     * Verifica se há quantidade suficiente disponível.
     */
    public boolean temDisponivel(Medida quantidadeNecessaria) {
        if (quantidadeNecessaria == null) {
            return true;
        }
        return quantidadeAtual.isMaiorOuIgualA(quantidadeNecessaria);
    }

    /**
     * Retorna a quantidade que falta para atingir o mínimo.
     */
    public Medida getQuantidadeFaltante() {
        if (quantidadeMinima == null || !isAbaixoDoMinimo()) {
            return Medida.zero(insumo.getUnidadePadrao());
        }
        return quantidadeMinima.subtrair(quantidadeAtual);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Estoque estoque = (Estoque) o;
        return Objects.equals(insumo.getId(), estoque.insumo.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(insumo.getId());
    }

    @Override
    public String toString() {
        return String.format("Estoque{%s: %s}", insumo.getNome(), quantidadeAtual);
    }
}
