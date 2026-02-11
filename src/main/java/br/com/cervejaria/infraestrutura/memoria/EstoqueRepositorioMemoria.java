package br.com.cervejaria.infraestrutura.memoria;

import br.com.cervejaria.dominio.estoque.Estoque;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositório de Estoque em memória.
 * 
 * <p>
 * A chave é o ID do Insumo.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class EstoqueRepositorioMemoria implements Repositorio<Estoque, String> {

    private final Map<String, Estoque> estoques = new ConcurrentHashMap<>();

    @Override
    public Estoque salvar(Estoque estoque) {
        if (estoque == null) {
            throw new IllegalArgumentException("Estoque não pode ser nulo");
        }
        // Usa o ID do Insumo como chave
        estoques.put(estoque.getInsumo().getId(), estoque);
        return estoque;
    }

    @Override
    public Optional<Estoque> buscarPorId(String insumoId) {
        return Optional.ofNullable(estoques.get(insumoId));
    }

    /**
     * Busca estoque por ID do insumo.
     */
    public Optional<Estoque> buscarPorInsumoId(String insumoId) {
        return buscarPorId(insumoId);
    }

    @Override
    public List<Estoque> buscarTodos() {
        return new ArrayList<>(estoques.values());
    }

    @Override
    public void remover(String insumoId) {
        estoques.remove(insumoId);
    }

    @Override
    public boolean existe(String insumoId) {
        return estoques.containsKey(insumoId);
    }

    @Override
    public long contar() {
        return estoques.size();
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * Retorna estoques abaixo do mínimo.
     */
    public List<Estoque> buscarAbaixoDoMinimo() {
        return estoques.values().stream()
                .filter(Estoque::isAbaixoDoMinimo)
                .toList();
    }

    /**
     * Retorna mapa de insumoId -> Estoque para uso em serviços.
     */
    public Map<String, Estoque> getMapaEstoques() {
        return new HashMap<>(estoques);
    }
}
