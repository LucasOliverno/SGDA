package br.com.cervejaria.aplicacao.lote;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.lote.Lote;
import br.com.cervejaria.infraestrutura.memoria.LoteRepositorioMemoria;

/**
 * Caso de Uso: Descartar Lote
 * 
 * <p>
 * Descarta um lote de produção, movendo-o para o estado final DESCARTADO.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class DescartarLoteUseCase {

    private final LoteRepositorioMemoria loteRepositorio;

    public DescartarLoteUseCase(LoteRepositorioMemoria loteRepositorio) {
        this.loteRepositorio = loteRepositorio;
    }

    /**
     * Dados de entrada.
     */
    public record DescartarLoteInput(
            String loteId,
            String justificativa) {
    }

    /**
     * Resultado da operação.
     */
    public record DescartarLoteOutput(
            String loteId,
            String codigo,
            String statusAnterior,
            String justificativa,
            String mensagem) {
    }

    /**
     * Descarta um lote.
     * 
     * @param input dados de entrada
     * @return resultado da operação
     * @throws DominioException se o lote não existir ou não puder ser descartado
     */
    public DescartarLoteOutput executar(DescartarLoteInput input) {
        if (input.loteId() == null || input.loteId().isBlank()) {
            throw new DominioException("ID do lote é obrigatório");
        }
        if (input.justificativa() == null || input.justificativa().isBlank()) {
            throw new DominioException("Justificativa é obrigatória para descartar um lote");
        }

        Lote lote = loteRepositorio.buscarPorId(input.loteId())
                .orElseThrow(() -> new DominioException(
                        String.format("Lote não encontrado: %s", input.loteId())));

        String statusAnterior = lote.getStatus().getDescricao();

        // Descarta (a validação de transição é feita no método)
        lote.descartar(input.justificativa());

        // Salva
        loteRepositorio.salvar(lote);

        return new DescartarLoteOutput(
                lote.getId(),
                lote.getCodigo(),
                statusAnterior,
                input.justificativa(),
                String.format("Lote %s descartado. Status anterior: %s. Justificativa: %s",
                        lote.getCodigo(), statusAnterior, input.justificativa()));
    }
}
