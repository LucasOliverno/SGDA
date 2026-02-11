/**
 * Camada de Aplicação - Casos de Uso.
 * 
 * <p>
 * Orquestra a execução de casos de uso completos, coordenando
 * entidades, serviços e repositórios.
 * </p>
 * 
 * <h2>Subpacotes</h2>
 * <ul>
 * <li>{@code receita} - Casos de uso de receitas</li>
 * <li>{@code lote} - Casos de uso de lotes</li>
 * <li>{@code estoque} - Casos de uso de estoque</li>
 * </ul>
 * 
 * <h2>Características</h2>
 * <ul>
 * <li>Cada classe representa um caso de uso</li>
 * <li>Recebe entrada, orquestra, retorna resultado</li>
 * <li>Pode usar DTOs para entrada e saída</li>
 * </ul>
 * 
 * <h2>O que NÃO deve existir aqui</h2>
 * <ul>
 * <li>Lógica de domínio (delegar para entidades/serviços)</li>
 * <li>Lógica de interface gráfica</li>
 * <li>Detalhes de persistência</li>
 * </ul>
 */
package br.com.cervejaria.aplicacao;
