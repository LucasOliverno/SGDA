/**
 * Subdomínio de Equipamentos.
 * 
 * <p>
 * Contém as classes relacionadas aos recursos físicos da cervejaria:
 * panelas, fermentadores, maturadores, etc.
 * </p>
 * 
 * <h2>Classes Planejadas</h2>
 * <ul>
 * <li>{@code Equipamento} - Entidade principal: recurso físico</li>
 * <li>{@code TipoEquipamento} - Enum: PANELA_MOSTURA, PANELA_FERVURA,
 * FERMENTADOR, etc.</li>
 * <li>{@code StatusEquipamento} - Enum: DISPONIVEL, EM_USO, EM_MANUTENCAO</li>
 * </ul>
 * 
 * <h2>Invariantes</h2>
 * <ul>
 * <li>Equipamento só pode estar alocado a um lote ativo por vez</li>
 * <li>Equipamento em manutenção não pode ser alocado</li>
 * <li>Volume do lote não pode exceder capacidade do fermentador</li>
 * </ul>
 */
package br.com.cervejaria.dominio.equipamento;
