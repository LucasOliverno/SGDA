/**
 * Camada de Infraestrutura.
 * 
 * <p>
 * Implementa a persistência de dados e acesso a recursos externos.
 * </p>
 * 
 * <h2>Subpacotes</h2>
 * <ul>
 * <li>{@code memoria} - Implementações de repositório em memória</li>
 * </ul>
 * 
 * <h2>Características</h2>
 * <ul>
 * <li>Implementa interfaces de repositório definidas no domínio</li>
 * <li>Esconde detalhes de persistência</li>
 * <li>Permite trocar implementação (memória → banco de dados)</li>
 * </ul>
 * 
 * <h2>O que NÃO deve existir aqui</h2>
 * <ul>
 * <li>Lógica de negócio</li>
 * <li>Validações de domínio</li>
 * <li>Conhecimento da interface gráfica</li>
 * </ul>
 */
package br.com.cervejaria.infraestrutura;
