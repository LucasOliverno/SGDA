/**
 * Subdomínio de Lotes de Produção.
 * 
 * <p>Contém as classes relacionadas à execução de produções de cerveja.
 * Um lote representa uma brassagem real baseada em uma receita.</p>
 * 
 * <h2>Classes Planejadas</h2>
 * <ul>
 *   <li>{@code Lote} - Entidade principal: produção física de cerveja</li>
 *   <li>{@code EtapaExecutada} - Registro real de uma etapa no lote</li>
 *   <li>{@code StatusLote} - Enum: PLANEJADO, EM_BRASSAGEM, FERMENTANDO, etc.</li>
 * </ul>
 * 
 * <h2>Relacionamentos</h2>
 * <ul>
 *   <li>Lote É BASEADO EM Receita (agregação N:1)</li>
 *   <li>Lote CONTÉM EtapaExecutada (composição 1:N)</li>
 *   <li>Lote UTILIZA Equipamento (agregação N:N)</li>
 *   <li>Lote RECEBE AvaliacaoQualidade (composição 1:N)</li>
 * </ul>
 * 
 * <h2>Ciclo de Vida</h2>
 * <p>PLANEJADO → EM_BRASSAGEM → FERMENTANDO → MATURANDO → 
 * PRONTO_PARA_ENVASE → ENVASADO (ou DESCARTADO)</p>
 */
package br.com.cervejaria.dominio.lote;
