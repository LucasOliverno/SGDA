package br.com.cervejaria.aplicacao.lote;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.lote.Lote;
import br.com.cervejaria.dominio.receita.Receita;
import br.com.cervejaria.infraestrutura.memoria.LoteRepositorioMemoria;
import br.com.cervejaria.infraestrutura.memoria.ReceitaRepositorioMemoria;

/**
 * Caso de Uso: Criar Lote
 * 
 * <p>
 * Cria um novo lote de produção a partir de uma receita ativa.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class CriarLoteUseCase {

    private final LoteRepositorioMemoria loteRepositorio;
    private final ReceitaRepositorioMemoria receitaRepositorio;

    public CriarLoteUseCase(LoteRepositorioMemoria loteRepositorio,
            ReceitaRepositorioMemoria receitaRepositorio) {
        this.loteRepositorio = loteRepositorio;
        this.receitaRepositorio = receitaRepositorio;
    }

    /**
     * Resultado da criação.
     */
    public record CriarLoteOutput(
            String loteId,
            String codigo,
            String receitaNome,
            String status,
            int totalEtapas,
            String mensagem) {
    }

    /**
     * Cria um lote a partir de uma receita.
     * 
     * @param receitaId ID da receita
     * @return resultado da criação
     * @throws DominioException se a receita não existir ou não estiver ativa
     */
    public CriarLoteOutput executar(String receitaId) {
        if (receitaId == null || receitaId.isBlank()) {
            throw new DominioException("ID da receita é obrigatório");
        }

        // Busca a receita
        Receita receita = receitaRepositorio.buscarPorId(receitaId)
                .orElseThrow(() -> new DominioException(
                        String.format("Receita não encontrada: %s", receitaId)));

        // Cria o lote (a validação de status é feita no construtor do Lote)
        Lote lote = new Lote(receita);

        // Salva
        loteRepositorio.salvar(lote);

        return new CriarLoteOutput(
                lote.getId(),
                lote.getCodigo(),
                receita.getNome(),
                lote.getStatus().getDescricao(),
                lote.getEtapasExecutadas().size(),
                String.format("Lote %s criado com sucesso! Status: PLANEJADO", lote.getCodigo()));
    }
}
