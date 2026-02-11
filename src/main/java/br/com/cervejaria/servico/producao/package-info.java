/**
 * Serviços de Produção.
 * 
 * <p>
 * Contém os serviços responsáveis pela validação e
 * planejamento de produções.
 * </p>
 * 
 * <h2>Classes Planejadas</h2>
 * <ul>
 * <li>{@code ValidacaoLoteService} - Validação de transições de lote</li>
 * <li>{@code PlanejamentoProducaoService} - Viabilidade de produção</li>
 * </ul>
 * 
 * <h2>Operações de Validação</h2>
 * <ul>
 * <li>Validar se lote pode iniciar fermentação</li>
 * <li>Verificar parâmetros dentro do esperado</li>
 * <li>Autorizar envase</li>
 * </ul>
 * 
 * <h2>Operações de Planejamento</h2>
 * <ul>
 * <li>Verificar disponibilidade de insumos</li>
 * <li>Verificar disponibilidade de equipamentos</li>
 * <li>Sugerir data de início viável</li>
 * <li>Calcular previsão de conclusão</li>
 * </ul>
 */
package br.com.cervejaria.servico.producao;
