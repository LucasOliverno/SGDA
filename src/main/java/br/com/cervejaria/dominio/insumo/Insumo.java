package br.com.cervejaria.dominio.insumo;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.comum.UnidadeMedida;

import java.util.Objects;
import java.util.UUID;

/**
 * Entidade que representa um insumo/ingrediente usado na produção de cerveja.
 * 
 * <p>
 * Exemplos: Malte Pilsen, Lúpulo Cascade, Levedura US-05.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class Insumo {

    private final String id;
    private String nome;
    private CategoriaInsumo categoria;
    private String fabricante;
    private UnidadeMedida unidadePadrao;
    private String caracteristicas;

    /**
     * Cria um novo insumo.
     * 
     * @param nome          nome do insumo
     * @param categoria     categoria do insumo
     * @param unidadePadrao unidade de medida padrão
     */
    public Insumo(String nome, CategoriaInsumo categoria, UnidadeMedida unidadePadrao) {
        this.id = UUID.randomUUID().toString();
        setNome(nome);
        setCategoria(categoria);
        setUnidadePadrao(unidadePadrao);
    }

    /**
     * Construtor para reconstituição (ex: do repositório).
     */
    public Insumo(String id, String nome, CategoriaInsumo categoria, UnidadeMedida unidadePadrao) {
        if (id == null || id.isBlank()) {
            throw new DominioException("ID do insumo não pode ser nulo ou vazio");
        }
        this.id = id;
        setNome(nome);
        setCategoria(categoria);
        setUnidadePadrao(unidadePadrao);
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DominioException("Nome do insumo não pode ser nulo ou vazio");
        }
        this.nome = nome.trim();
    }

    public CategoriaInsumo getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaInsumo categoria) {
        if (categoria == null) {
            throw new DominioException("Categoria do insumo não pode ser nula");
        }
        this.categoria = categoria;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante != null ? fabricante.trim() : null;
    }

    public UnidadeMedida getUnidadePadrao() {
        return unidadePadrao;
    }

    public void setUnidadePadrao(UnidadeMedida unidadePadrao) {
        if (unidadePadrao == null) {
            throw new DominioException("Unidade padrão do insumo não pode ser nula");
        }
        this.unidadePadrao = unidadePadrao;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    /**
     * Verifica se este insumo é de uma categoria obrigatória em receitas.
     */
    public boolean isCategoriaObrigatoria() {
        return categoria.isObrigatorioEmReceita();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Insumo insumo = (Insumo) o;
        return Objects.equals(id, insumo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", nome, categoria);
    }
}
