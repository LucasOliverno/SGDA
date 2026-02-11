package br.com.cervejaria.infraestrutura.memoria;

import br.com.cervejaria.dominio.lote.Lote;
import br.com.cervejaria.dominio.lote.StatusLote;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositório de Lotes em memória.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class LoteRepositorioMemoria implements Repositorio<Lote, String> {

    private final Map<String, Lote> lotes = new ConcurrentHashMap<>();

    @Override
    public Lote salvar(Lote lote) {
        if (lote == null) {
            throw new IllegalArgumentException("Lote não pode ser nulo");
        }
        lotes.put(lote.getId(), lote);
        return lote;
    }

    @Override
    public Optional<Lote> buscarPorId(String id) {
        return Optional.ofNullable(lotes.get(id));
    }

    @Override
    public List<Lote> buscarTodos() {
        return new ArrayList<>(lotes.values());
    }

    @Override
    public void remover(String id) {
        lotes.remove(id);
    }

    @Override
    public boolean existe(String id) {
        return lotes.containsKey(id);
    }

    @Override
    public long contar() {
        return lotes.size();
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    /**
     * Busca lote por código.
     */
    public Optional<Lote> buscarPorCodigo(String codigo) {
        if (codigo == null)
            return Optional.empty();
        return lotes.values().stream()
                .filter(l -> l.getCodigo().equals(codigo))
                .findFirst();
    }

    /**
     * Busca lotes por status.
     */
    public List<Lote> buscarPorStatus(StatusLote status) {
        return lotes.values().stream()
                .filter(l -> l.getStatus() == status)
                .toList();
    }

    /**
     * Retorna lotes em produção (não finalizados).
     */
    public List<Lote> buscarEmProducao() {
        return lotes.values().stream()
                .filter(l -> !l.isEstadoFinal())
                .toList();
    }

    /**
     * Retorna lotes finalizados.
     */
    public List<Lote> buscarFinalizados() {
        return lotes.values().stream()
                .filter(Lote::isEstadoFinal)
                .toList();
    }

    /**
     * Busca lotes por receita.
     */
    public List<Lote> buscarPorReceita(String receitaId) {
        if (receitaId == null)
            return List.of();
        return lotes.values().stream()
                .filter(l -> l.getReceita().getId().equals(receitaId))
                .toList();
    }
}
