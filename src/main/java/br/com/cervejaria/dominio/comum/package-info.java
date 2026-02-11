/**
 * Value Objects Compartilhados.
 * 
 * <p>
 * Contém os value objects que são utilizados por múltiplas
 * entidades do domínio.
 * </p>
 * 
 * <h2>Classes Planejadas</h2>
 * <ul>
 * <li>{@code Medida} - Valor numérico + unidade (kg, g, L, mL)</li>
 * <li>{@code Densidade} - Valor + tipo (OG/FG) + escala (Plato/SG)</li>
 * <li>{@code FaixaParametro} - Mínimo + máximo + unidade</li>
 * <li>{@code Periodo} - Data/hora início + fim</li>
 * <li>{@code Contato} - Telefone + e-mail + endereço</li>
 * <li>{@code UnidadeMedida} - Enum: QUILOGRAMA, GRAMA, LITRO, MILILITRO,
 * UNIDADE</li>
 * </ul>
 * 
 * <h2>Características de Value Objects</h2>
 * <ul>
 * <li>Imutáveis após criação</li>
 * <li>Igualdade por valor (não por referência)</li>
 * <li>Sem identidade própria</li>
 * <li>Podem ser compartilhados entre entidades</li>
 * </ul>
 */
package br.com.cervejaria.dominio.comum;
