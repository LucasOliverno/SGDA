package br.com.cervejaria.aplicacao.receita;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.receita.Receita;
import br.com.cervejaria.infraestrutura.memoria.ReceitaRepositorioMemoria;

/**
 * Caso de Uso: Ativar Receita
 * 
 * <p>
 * Ativa uma receita em rascunho, permitindo criar lotes a partir dela.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class AtivarReceitaUseCase {

    private final ReceitaRepositorioMemoria receitaRepositorio;

    public AtivarReceitaUseCase(ReceitaRepositorioMemoria receitaRepositorio) {
        this.receitaRepositorio = receitaRepositorio;
    }

    /**
     * Resultado da ativação.
     */
    public record AtivarReceitaOutput(
            String receitaId,
            String nome,
            String status,
            int totalItens,
            int totalEtapas,
            String mensagem) {
    }

    /**
     * Ativa uma receita.
     * 
     * @param receitaId ID da receita a ativar
     * @return resultado da ativação
     * @throws DominioException se a receita não existir ou não puder ser ativada
     */
    public AtivarReceitaOutput executar(String receitaId) {
        if (receitaId == null || receitaId.isBlank()) {
            throw new DominioException("ID da receita é obrigatório");
        }

        Receita receita = receitaRepositorio.buscarPorId(receitaId)
                .orElseThrow(() -> new DominioException(
                        String.format("Receita não encontrada: %s", receitaId)));

        // Tenta ativar (inclui validação de requisitos mínimos)
        receita.ativar();

        // Salva a alteração
        receitaRepositorio.salvar(receita);

        return new AtivarReceitaOutput(
                receita.getId(),
                receita.getNome(),
                receita.getStatus().getDescricao(),
                receita.getItens().size(),
                receita.getEtapas().size(),
                "Receita ativada com sucesso! Agora pode gerar lotes de produção.");
    }
}
