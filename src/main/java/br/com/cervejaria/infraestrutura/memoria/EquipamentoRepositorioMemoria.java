package br.com.cervejaria.infraestrutura.memoria;

import br.com.cervejaria.dominio.equipamento.Equipamento;
import br.com.cervejaria.dominio.equipamento.StatusEquipamento;
import br.com.cervejaria.dominio.equipamento.TipoEquipamento;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositório de Equipamentos em memória.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class EquipamentoRepositorioMemoria implements Repositorio<Equipamento, String> {

    private final Map<String, Equipamento> equipamentos = new ConcurrentHashMap<>();

    @Override
    public Equipamento salvar(Equipamento equipamento) {
        if (equipamento == null) {
            throw new IllegalArgumentException("Equipamento não pode ser nulo");
        }
        equipamentos.put(equipamento.getId(), equipamento);
        return equipamento;
    }

    @Override
    public Optional<Equipamento> buscarPorId(String id) {
        return Optional.ofNullable(equipamentos.get(id));
    }

    @Override
    public List<Equipamento> buscarTodos() {
        return new ArrayList<>(equipamentos.values());
    }

    @Override
    public void remover(String id) {
        equipamentos.remove(id);
    }

    @Override
    public boolean existe(String id) {
        return equipamentos.containsKey(id);
    }

    @Override
    public long contar() {
        return equipamentos.size();
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * Busca equipamentos por tipo.
     */
    public List<Equipamento> buscarPorTipo(TipoEquipamento tipo) {
        return equipamentos.values().stream()
                .filter(e -> e.getTipo() == tipo)
                .toList();
    }

    /**
     * Busca equipamentos por status.
     */
    public List<Equipamento> buscarPorStatus(StatusEquipamento status) {
        return equipamentos.values().stream()
                .filter(e -> e.getStatus() == status)
                .toList();
    }

    /**
     * Retorna equipamentos disponíveis para alocação.
     */
    public List<Equipamento> buscarDisponiveis() {
        return equipamentos.values().stream()
                .filter(Equipamento::podeSerAlocado)
                .toList();
    }

    /**
     * Retorna equipamentos disponíveis por tipo.
     */
    public List<Equipamento> buscarDisponiveisPorTipo(TipoEquipamento tipo) {
        return equipamentos.values().stream()
                .filter(e -> e.getTipo() == tipo && e.podeSerAlocado())
                .toList();
    }

    /**
     * Busca equipamento alocado a um lote específico.
     */
    public List<Equipamento> buscarPorLote(String loteId) {
        if (loteId == null)
            return List.of();
        return equipamentos.values().stream()
                .filter(e -> e.isAlocadoAoLote(loteId))
                .toList();
    }
}
