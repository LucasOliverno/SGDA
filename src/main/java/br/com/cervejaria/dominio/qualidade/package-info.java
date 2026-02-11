/**
 * Subdomínio de Qualidade.
 * 
 * <p>
 * Contém as classes relacionadas ao controle de qualidade,
 * avaliações sensoriais e técnicas.
 * </p>
 * 
 * <h2>Classes Planejadas</h2>
 * <ul>
 * <li>{@code AvaliacaoQualidade} - Entidade: registro de avaliação de lote</li>
 * <li>{@code TipoAvaliacaoQualidade} - Enum: SENSORIAL, TECNICA</li>
 * <li>{@code ParecerQualidade} - Enum: APROVADO, REPROVADO, COM_RESSALVAS</li>
 * </ul>
 * 
 * <h2>Avaliação Sensorial</h2>
 * <p>
 * Notas de 1-10 para: aparência, aroma, sabor, corpo.
 * </p>
 * 
 * <h2>Avaliação Técnica</h2>
 * <p>
 * Medições de: pH, densidade, temperatura, etc.
 * </p>
 * 
 * <h2>Regras de Negócio</h2>
 * <ul>
 * <li>Lote só pode ser envasado com avaliação aprovada</li>
 * <li>Toda avaliação deve estar vinculada a um lote</li>
 * </ul>
 */
package br.com.cervejaria.dominio.qualidade;
