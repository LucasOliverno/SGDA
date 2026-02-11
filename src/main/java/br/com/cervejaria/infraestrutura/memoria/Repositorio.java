package br.com.cervejaria.infraestrutura.memoria;

import java.util.List;
import java.util.Optional;

/**
 * Interface base para repositórios.
 * 
 * @param <T>  tipo da entidade
 * @param <ID> tipo do identificador
 */
public interface Repositorio<T, ID> {

    /**
     * Salva uma entidade.
     */
    T salvar(T entidade);

    /**
     * Busca uma entidade por ID.
     */
    Optional<T> buscarPorId(ID id);

    /**
     * Retorna todas as entidades.
     */
    List<T> buscarTodos();

    /**
     * Remove uma entidade por ID.
     */
    void remover(ID id);

    /**
     * Verifica se existe uma entidade com o ID.
     */
    boolean existe(ID id);

    /**
     * Conta o total de entidades.
     */
    long contar();
}
