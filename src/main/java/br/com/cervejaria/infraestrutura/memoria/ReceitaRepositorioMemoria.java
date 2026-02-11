package br.com.cervejaria.infraestrutura.memoria;

import br.com.cervejaria.dominio.receita.Receita;
import br.com.cervejaria.dominio.receita.StatusReceita;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositório de Receitas em memória.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class ReceitaRepositorioMemoria implements Repositorio<Receita, String> {

    private final Map<String, Receita> receitas = new ConcurrentHashMap<>();

    @Override
    public Receita salvar(Receita receita) {
        if (receita == null) {
            throw new IllegalArgumentException("Receita não pode ser nula");
        }
        receitas.put(receita.getId(), receita);
        return receita;
    }

    @Override
    public Optional<Receita> buscarPorId(String id) {
        return Optional.ofNullable(receitas.get(id));
    }

    @Override
    public List<Receita> buscarTodos() {
        return new ArrayList<>(receitas.values());
    }

    @Override
    public void remover(String id) {
        receitas.remove(id);
    }

    @Override
    public boolean existe(String id) {
        return receitas.containsKey(id);
    }

    @Override
    public long contar() {
        return receitas.size();
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * Busca receitas por nome (contém, case insensitive).
     */
    public List<Receita> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return buscarTodos();
        }
        String nomeLower = nome.toLowerCase();
        return receitas.values().stream()
                .filter(r -> r.getNome().toLowerCase().contains(nomeLower))
                .toList();
    }

    /**
     * Busca receitas por status.
     */
    public List<Receita> buscarPorStatus(StatusReceita status) {
        return receitas.values().stream()
                .filter(r -> r.getStatus() == status)
                .toList();
    }

    /**
     * Retorna apenas receitas ativas.
     */
    public List<Receita> buscarAtivas() {
        return buscarPorStatus(StatusReceita.ATIVA);
    }

    /**
     * Verifica se existe receita com o nome especificado.
     */
    public boolean existePorNome(String nome) {
        if (nome == null)
            return false;
        return receitas.values().stream()
                .anyMatch(r -> r.getNome().equalsIgnoreCase(nome));
    }
}
