package br.com.cervejaria.dominio.equipamento;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.comum.Medida;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade que representa um equipamento da cervejaria.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class Equipamento {

    private final String id;
    private String nome;
    private TipoEquipamento tipo;
    private Medida capacidade;
    private StatusEquipamento status;
    private LocalDateTime ultimaLimpeza;
    private String loteAlocadoId; // ID do lote que está usando

    /**
     * Cria um novo equipamento.
     * 
     * @param nome       identificação do equipamento
     * @param tipo       tipo do equipamento
     * @param capacidade capacidade em litros
     */
    public Equipamento(String nome, TipoEquipamento tipo, Medida capacidade) {
        this.id = UUID.randomUUID().toString();
        this.status = StatusEquipamento.DISPONIVEL;

        setNome(nome);
        setTipo(tipo);
        setCapacidade(capacidade);
    }

    /**
     * Construtor para reconstituição.
     */
    public Equipamento(String id, String nome, TipoEquipamento tipo, Medida capacidade) {
        if (id == null || id.isBlank()) {
            throw new DominioException("ID do equipamento não pode ser nulo ou vazio");
        }
        this.id = id;
        this.status = StatusEquipamento.DISPONIVEL;

        setNome(nome);
        setTipo(tipo);
        setCapacidade(capacidade);
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DominioException("Nome do equipamento não pode ser nulo ou vazio");
        }
        this.nome = nome.trim();
    }

    public TipoEquipamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoEquipamento tipo) {
        if (tipo == null) {
            throw new DominioException("Tipo do equipamento não pode ser nulo");
        }
        this.tipo = tipo;
    }

    public Medida getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Medida capacidade) {
        this.capacidade = capacidade;
    }

    public StatusEquipamento getStatus() {
        return status;
    }

    public LocalDateTime getUltimaLimpeza() {
        return ultimaLimpeza;
    }

    public String getLoteAlocadoId() {
        return loteAlocadoId;
    }

    // ==================== OPERAÇÕES ====================

    /**
     * Aloca o equipamento para um lote.
     * 
     * @param loteId ID do lote
     */
    public void alocar(String loteId) {
        if (!status.podeAlocar()) {
            throw new DominioException(
                    String.format("Equipamento '%s' não pode ser alocado: status = %s", nome, status));
        }
        if (loteId == null || loteId.isBlank()) {
            throw new DominioException("ID do lote é obrigatório para alocação");
        }

        this.status = StatusEquipamento.EM_USO;
        this.loteAlocadoId = loteId;
    }

    /**
     * Libera o equipamento do lote atual.
     */
    public void liberar() {
        if (status != StatusEquipamento.EM_USO) {
            throw new DominioException(
                    String.format("Equipamento '%s' não está em uso para ser liberado", nome));
        }
        this.status = StatusEquipamento.DISPONIVEL;
        this.loteAlocadoId = null;
    }

    /**
     * Coloca o equipamento em manutenção.
     */
    public void iniciarManutencao() {
        if (status == StatusEquipamento.EM_USO) {
            throw new DominioException(
                    String.format("Equipamento '%s' está em uso e não pode entrar em manutenção", nome));
        }
        this.status = StatusEquipamento.EM_MANUTENCAO;
    }

    /**
     * Finaliza a manutenção.
     */
    public void finalizarManutencao() {
        if (status != StatusEquipamento.EM_MANUTENCAO) {
            throw new DominioException("Equipamento não está em manutenção");
        }
        this.status = StatusEquipamento.DISPONIVEL;
    }

    /**
     * Registra limpeza do equipamento.
     */
    public void registrarLimpeza() {
        this.ultimaLimpeza = LocalDateTime.now();
        if (status == StatusEquipamento.EM_LIMPEZA) {
            this.status = StatusEquipamento.DISPONIVEL;
        }
    }

    /**
     * Verifica se o equipamento pode ser alocado.
     */
    public boolean podeSerAlocado() {
        return status.podeAlocar();
    }

    /**
     * Verifica se o equipamento está alocado a um lote específico.
     */
    public boolean isAlocadoAoLote(String loteId) {
        return loteAlocadoId != null && loteAlocadoId.equals(loteId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Equipamento that = (Equipamento) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) [%s]", nome, tipo, status);
    }
}
