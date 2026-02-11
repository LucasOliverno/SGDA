/**
 * Subdomínio de Receitas.
 * 
 * <p>Contém as classes relacionadas à definição de receitas de cerveja,
 * incluindo ingredientes e etapas de produção planejadas.</p>
 * 
 * <h2>Classes Planejadas</h2>
 * <ul>
 *   <li>{@code Receita} - Entidade principal: fórmula de uma cerveja</li>
 *   <li>{@code ItemReceita} - Insumo + quantidade na receita</li>
 *   <li>{@code EtapaProducao} - Template de etapa (mostura, fervura, etc.)</li>
 *   <li>{@code StatusReceita} - Enum: RASCUNHO, ATIVA, ARQUIVADA</li>
 * </ul>
 * 
 * <h2>Relacionamentos</h2>
 * <ul>
 *   <li>Receita CONTÉM ItemReceita (composição 1:N)</li>
 *   <li>Receita CONTÉM EtapaProducao (composição 1:N)</li>
 *   <li>Receita PERTENCE A Estilo (agregação N:1)</li>
 * </ul>
 */
package br.com.cervejaria.dominio.receita;
