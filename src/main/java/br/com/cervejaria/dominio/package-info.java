/**
 * Camada de Domínio - Coração do Sistema.
 * 
 * <p>Contém as entidades e value objects que representam os conceitos
 * do negócio cervejeiro. Classes deste pacote são POJOs puros.</p>
 * 
 * <h2>Subpacotes</h2>
 * <ul>
 *   <li>{@code receita} - Receitas e seus componentes</li>
 *   <li>{@code lote} - Lotes de produção e etapas executadas</li>
 *   <li>{@code insumo} - Insumos/ingredientes</li>
 *   <li>{@code estoque} - Controle de estoque e movimentações</li>
 *   <li>{@code equipamento} - Equipamentos da cervejaria</li>
 *   <li>{@code qualidade} - Avaliações de qualidade</li>
 *   <li>{@code fornecedor} - Fornecedores de insumos</li>
 *   <li>{@code comum} - Value objects compartilhados</li>
 * </ul>
 * 
 * <h2>O que NÃO deve existir aqui</h2>
 * <ul>
 *   <li>Lógica de persistência</li>
 *   <li>Referências a frameworks externos</li>
 *   <li>Lógica de interface gráfica</li>
 *   <li>Dependências de infraestrutura</li>
 * </ul>
 */
package br.com.cervejaria.dominio;
