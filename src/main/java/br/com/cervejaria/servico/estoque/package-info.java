/**
 * Serviços de Gestão de Estoque.
 * 
 * <p>
 * Contém os serviços responsáveis pela coordenação de
 * operações de estoque.
 * </p>
 * 
 * <h2>Classes Planejadas</h2>
 * <ul>
 * <li>{@code GestaoEstoqueService} - Serviço principal de estoque</li>
 * </ul>
 * 
 * <h2>Operações</h2>
 * <ul>
 * <li>Reservar insumos para um lote</li>
 * <li>Confirmar consumo de insumos (baixa)</li>
 * <li>Verificar disponibilidade para uma receita</li>
 * <li>Gerar alertas de reposição</li>
 * <li>Registrar entrada de compra</li>
 * </ul>
 * 
 * <h2>Regras de Negócio</h2>
 * <ul>
 * <li>Verificar estoque antes de iniciar produção</li>
 * <li>Alertar quando atingir ponto de reposição</li>
 * <li>Insumos vencidos não são considerados disponíveis</li>
 * </ul>
 */
package br.com.cervejaria.servico.estoque;
