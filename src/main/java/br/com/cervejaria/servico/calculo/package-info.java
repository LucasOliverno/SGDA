/**
 * Serviços de Cálculo Cervejeiro.
 * 
 * <p>
 * Contém os serviços responsáveis pelos cálculos técnicos
 * da cervejaria.
 * </p>
 * 
 * <h2>Classes Planejadas</h2>
 * <ul>
 * <li>{@code CalculoCervejeiroService} - Serviço principal de cálculos</li>
 * </ul>
 * 
 * <h2>Operações</h2>
 * <ul>
 * <li>Calcular IBU estimado de uma receita</li>
 * <li>Calcular ABV a partir de OG e FG</li>
 * <li>Calcular eficiência de brassagem</li>
 * <li>Ajustar receita para novo volume</li>
 * <li>Calcular cor estimada (SRM/EBC)</li>
 * </ul>
 * 
 * <h2>Fórmulas Utilizadas</h2>
 * <ul>
 * <li>ABV = (OG - FG) × 131.25</li>
 * <li>IBU = (massa × alfa-ácido × utilização) / volume</li>
 * </ul>
 */
package br.com.cervejaria.servico.calculo;
