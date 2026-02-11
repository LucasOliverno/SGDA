package br.com.cervejaria.infraestrutura.memoria;

import br.com.cervejaria.dominio.insumo.CategoriaInsumo;
import br.com.cervejaria.dominio.insumo.Insumo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositório de Insumos em memória.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class InsumoRepositorioMemoria implements Repositorio<Insumo, String> {

    private final Map<String, Insumo> insumos = new ConcurrentHashMap<>();

    @Override
    public Insumo salvar(Insumo insumo) {
        if (insumo == null) {
            throw new IllegalArgumentException("Insumo não pode ser nulo");
        }
        insumos.put(insumo.getId(), insumo);
        return insumo;
    }

    @Override
    public Optional<Insumo> buscarPorId(String id) {
        return Optional.ofNullable(insumos.get(id));
    }

    @Override
    public List<Insumo> buscarTodos() {
        return new ArrayList<>(insumos.values());
    }

    @Override
    public void remover(String id) {
        insumos.remove(id);
    }

    @Override
    public boolean existe(String id) {
        return insumos.containsKey(id);
    }

    @Override
    public long contar() {
        return insumos.size();
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * Busca insumos por categoria.
     */
    public List<Insumo> buscarPorCategoria(CategoriaInsumo categoria) {
        return insumos.values().stream()
                .filter(i -> i.getCategoria() == categoria)
                .toList();
    }

    /**
     * Busca insumos por nome (contém, case insensitive).
     */
    public List<Insumo> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return buscarTodos();
        }
        String nomeLower = nome.toLowerCase();
        return insumos.values().stream()
                .filter(i -> i.getNome().toLowerCase().contains(nomeLower))
                .toList();
    }

    /**
     * Busca insumos por fabricante.
     */
    public List<Insumo> buscarPorFabricante(String fabricante) {
        if (fabricante == null)
            return List.of();
        return insumos.values().stream()
                .filter(i -> fabricante.equals(i.getFabricante()))
                .toList();
    }
}
