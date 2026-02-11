package br.com.cervejaria.aplicacao.receita;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.comum.Medida;
import br.com.cervejaria.dominio.insumo.Estilo;
import br.com.cervejaria.dominio.receita.Receita;
import br.com.cervejaria.infraestrutura.memoria.ReceitaRepositorioMemoria;

/**
 * Caso de Uso: Criar Receita
 * 
 * <p>
 * Cria uma nova receita em status RASCUNHO.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class CriarReceitaUseCase {

    private final ReceitaRepositorioMemoria receitaRepositorio;

    public CriarReceitaUseCase(ReceitaRepositorioMemoria receitaRepositorio) {
        this.receitaRepositorio = receitaRepositorio;
    }

    /**
     * Dados de entrada para criar uma receita.
     */
    public record CriarReceitaInput(
            String nome,
            Medida volumeProjetado,
            Estilo estilo,
            String notas) {
    }

    /**
     * Resultado da criação.
     */
    public record CriarReceitaOutput(
            String receitaId,
            String nome,
            String status,
            String mensagem) {
    }

    /**
     * Executa a criação de uma receita.
     * 
     * @param input dados de entrada
     * @return resultado da criação
     * @throws DominioException se dados inválidos
     */
    public CriarReceitaOutput executar(CriarReceitaInput input) {
        // Valida entrada
        if (input.nome() == null || input.nome().isBlank()) {
            throw new DominioException("Nome da receita é obrigatório");
        }
        if (input.volumeProjetado() == null) {
            throw new DominioException("Volume projetado é obrigatório");
        }

        // Verifica nome duplicado
        if (receitaRepositorio.existePorNome(input.nome())) {
            throw new DominioException(
                    String.format("Já existe uma receita com o nome '%s'", input.nome()));
        }

        // Cria a receita
        Receita receita = new Receita(input.nome(), input.volumeProjetado());

        if (input.estilo() != null) {
            receita.setEstilo(input.estilo());
        }
        if (input.notas() != null) {
            receita.setNotas(input.notas());
        }

        // Salva
        receitaRepositorio.salvar(receita);

        return new CriarReceitaOutput(
                receita.getId(),
                receita.getNome(),
                receita.getStatus().getDescricao(),
                "Receita criada com sucesso em status RASCUNHO");
    }
}
