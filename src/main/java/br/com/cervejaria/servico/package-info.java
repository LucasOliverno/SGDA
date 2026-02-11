/**
 * Camada de Serviços de Negócio.
 * 
 * <p>
 * Contém a lógica de negócio que envolve múltiplas entidades
 * ou cálculos complexos que não pertencem a uma entidade específica.
 * </p>
 * 
 * <h2>Subpacotes</h2>
 * <ul>
 * <li>{@code calculo} - Cálculos cervejeiros (IBU, ABV, eficiência)</li>
 * <li>{@code estoque} - Gestão de estoque (reserva, baixa, alertas)</li>
 * <li>{@code producao} - Validação e planejamento de produção</li>
 * <li>{@code qualidade} - Conformidade de estilo e qualidade</li>
 * </ul>
 * 
 * <h2>Características</h2>
 * <ul>
 * <li>Serviços são stateless</li>
 * <li>Recebem dados, processam, retornam resultado</li>
 * <li>Não mantêm estado entre chamadas</li>
 * </ul>
 * 
 * <h2>O que NÃO deve existir aqui</h2>
 * <ul>
 * <li>Estado mutável</li>
 * <li>Referências a interface gráfica</li>
 * <li>Lógica de persistência direta</li>
 * </ul>
 */
package br.com.cervejaria.servico;
