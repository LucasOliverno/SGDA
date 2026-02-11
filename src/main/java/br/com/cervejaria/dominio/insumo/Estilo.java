package br.com.cervejaria.dominio.insumo;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.comum.FaixaParametro;

import java.util.Objects;
import java.util.UUID;

/**
 * Entidade que representa um estilo de cerveja (ex: IPA, Pilsen, Stout).
 * 
 * <p>
 * Define as características esperadas para cervejas deste estilo,
 * como faixas de IBU, ABV e cor.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class Estilo {

    private final String id;
    private String nome;
    private String categoria;
    private String descricao;
    private FaixaParametro faixaIBU;
    private FaixaParametro faixaABV;
    private FaixaParametro faixaCor;

    /**
     * Cria um novo estilo de cerveja.
     * 
     * @param nome      nome do estilo (ex: "American IPA")
     * @param categoria categoria geral (ex: "Ale", "Lager")
     */
    public Estilo(String nome, String categoria) {
        this.id = UUID.randomUUID().toString();
        setNome(nome);
        setCategoria(categoria);
    }

    /**
     * Construtor para reconstituição.
     */
    public Estilo(String id, String nome, String categoria) {
        if (id == null || id.isBlank()) {
            throw new DominioException("ID do estilo não pode ser nulo ou vazio");
        }
        this.id = id;
        setNome(nome);
        setCategoria(categoria);
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DominioException("Nome do estilo não pode ser nulo ou vazio");
        }
        this.nome = nome.trim();
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            throw new DominioException("Categoria do estilo não pode ser nula ou vazia");
        }
        this.categoria = categoria.trim();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public FaixaParametro getFaixaIBU() {
        return faixaIBU;
    }

    public void setFaixaIBU(FaixaParametro faixaIBU) {
        this.faixaIBU = faixaIBU;
    }

    public FaixaParametro getFaixaABV() {
        return faixaABV;
    }

    public void setFaixaABV(FaixaParametro faixaABV) {
        this.faixaABV = faixaABV;
    }

    public FaixaParametro getFaixaCor() {
        return faixaCor;
    }

    public void setFaixaCor(FaixaParametro faixaCor) {
        this.faixaCor = faixaCor;
    }

    /**
     * Define as faixas de parâmetros do estilo.
     */
    public Estilo comFaixas(FaixaParametro ibu, FaixaParametro abv, FaixaParametro cor) {
        this.faixaIBU = ibu;
        this.faixaABV = abv;
        this.faixaCor = cor;
        return this;
    }

    /**
     * Verifica se um valor de IBU está dentro da faixa do estilo.
     */
    public boolean ibuDentroDoEstilo(double ibu) {
        return faixaIBU == null || faixaIBU.contem(ibu);
    }

    /**
     * Verifica se um valor de ABV está dentro da faixa do estilo.
     */
    public boolean abvDentroDoEstilo(double abv) {
        return faixaABV == null || faixaABV.contem(abv);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Estilo estilo = (Estilo) o;
        return Objects.equals(id, estilo.id);
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
