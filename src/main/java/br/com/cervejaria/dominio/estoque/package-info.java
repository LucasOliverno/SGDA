/**
 * Subdomínio de Estoque.
 * 
 * <p>
 * Contém as classes relacionadas ao controle de estoque de insumos,
 * incluindo movimentações de entrada e saída.
 * </p>
 * 
 * <h2>Classes Planejadas</h2>
 * <ul>
 * <li>{@code Estoque} - Entidade: quantidade disponível de um insumo</li>
 * <li>{@code MovimentoEstoque} - Entidade: registro de entrada/saída</li>
 * <li>{@code TipoMovimentoEstoque} - Enum: ENTRADA, SAIDA</li>
 * <li>{@code MotivoMovimento} - Enum: COMPRA, USO_PRODUCAO, DESCARTE,
 * AJUSTE</li>
 * </ul>
 * 
 * <h2>Invariantes</h2>
 * <ul>
 * <li>Quantidade em estoque nunca pode ser negativa</li>
 * <li>Todo movimento deve estar vinculado a um estoque</li>
 * <li>Movimentos de saída requerem verificação prévia de disponibilidade</li>
 * </ul>
 */
package br.com.cervejaria.dominio.estoque;
