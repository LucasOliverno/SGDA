/**
 * Camada de apresentação JavaFX.
 * 
 * <p>
 * Contém a interface gráfica do sistema de cervejaria artesanal.
 * </p>
 * 
 * <h2>Estrutura</h2>
 * <ul>
 * <li>{@code MainApp} - Aplicação JavaFX principal</li>
 * <li>{@code AppContext} - Contexto de injeção de dependências</li>
 * <li>Controllers para cada tela</li>
 * </ul>
 * 
 * <h2>Regras</h2>
 * <ul>
 * <li>A GUI não instancia domínio nem serviços diretamente</li>
 * <li>Todas as ações passam por casos de uso</li>
 * <li>Erros de domínio são exibidos com Alert</li>
 * </ul>
 */
package br.com.cervejaria.apresentacao;
