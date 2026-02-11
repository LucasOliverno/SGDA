/**
 * Repositórios em Memória.
 * 
 * <p>
 * Implementações de repositório que mantêm dados em memória,
 * adequadas para sistema didático ou testes.
 * </p>
 * 
 * <h2>Classes Planejadas</h2>
 * <ul>
 * <li>{@code ReceitaRepositorioMemoria} - Repositório de receitas</li>
 * <li>{@code LoteRepositorioMemoria} - Repositório de lotes</li>
 * <li>{@code InsumoRepositorioMemoria} - Repositório de insumos</li>
 * <li>{@code EstoqueRepositorioMemoria} - Repositório de estoque</li>
 * <li>{@code EquipamentoRepositorioMemoria} - Repositório de equipamentos</li>
 * <li>{@code FornecedorRepositorioMemoria} - Repositório de fornecedores</li>
 * <li>{@code EstiloRepositorioMemoria} - Repositório de estilos</li>
 * </ul>
 * 
 * <h2>Implementação</h2>
 * <p>
 * Utiliza {@code Map<ID, Entidade>} para armazenamento.
 * Dados são perdidos ao encerrar a aplicação.
 * </p>
 * 
 * <h2>Evolução Futura</h2>
 * <p>
 * Pode ser substituído por implementações JPA/JDBC para
 * persistência em banco de dados.
 * </p>
 */
package br.com.cervejaria.infraestrutura.memoria;
