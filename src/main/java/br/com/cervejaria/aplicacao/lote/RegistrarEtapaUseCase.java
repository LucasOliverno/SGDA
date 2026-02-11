package br.com.cervejaria.aplicacao.lote;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.lote.EtapaProducaoExecutada;
import br.com.cervejaria.dominio.lote.Lote;
import br.com.cervejaria.infraestrutura.memoria.LoteRepositorioMemoria;

/**
 * Caso de Uso: Registrar Etapa
 * 
 * <p>
 * Registra a conclusão da etapa atual de um lote.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class RegistrarEtapaUseCase {

    private final LoteRepositorioMemoria loteRepositorio;

    public RegistrarEtapaUseCase(LoteRepositorioMemoria loteRepositorio) {
        this.loteRepositorio = loteRepositorio;
    }

    /**
     * Dados de entrada.
     */
    public record RegistrarEtapaInput(
            String loteId,
            Double temperaturaReal,
            String observacoes,
            boolean comProblemas) {
    }

    /**
     * Resultado da operação.
     */
    public record RegistrarEtapaOutput(
            String loteId,
            String etapaConcluida,
            String proximaEtapa,
            boolean todasConcluidas,
            String mensagem) {
    }

    /**
     * Registra a conclusão da etapa atual.
     * 
     * @param input dados de entrada
     * @return resultado da operação
     */
    public RegistrarEtapaOutput executar(RegistrarEtapaInput input) {
        if (input.loteId() == null || input.loteId().isBlank()) {
            throw new DominioException("ID do lote é obrigatório");
        }

        Lote lote = loteRepositorio.buscarPorId(input.loteId())
                .orElseThrow(() -> new DominioException(
                        String.format("Lote não encontrado: %s", input.loteId())));

        // Obtém etapa atual antes de concluir
        EtapaProducaoExecutada etapaAtual = lote.getEtapaEmAndamento()
                .orElseThrow(() -> new DominioException("Não há etapa em andamento"));

        String nomeEtapaConcluida = etapaAtual.getModelo().getTipo().getNome();

        // Conclui a etapa
        if (input.comProblemas()) {
            etapaAtual.concluirComProblemas(input.observacoes());
            // Inicia próxima etapa manualmente
            lote.getProximaEtapaPendente().ifPresent(EtapaProducaoExecutada::iniciar);
        } else {
            lote.concluirEtapaAtual(input.temperaturaReal(), input.observacoes());
        }

        // Salva
        loteRepositorio.salvar(lote);

        // Obtém próxima etapa (se houver)
        String proximaEtapa = lote.getEtapaEmAndamento()
                .map(e -> e.getModelo().getTipo().getNome())
                .orElse("(todas concluídas)");

        return new RegistrarEtapaOutput(
                lote.getId(),
                nomeEtapaConcluida,
                proximaEtapa,
                lote.todasEtapasConcluidas(),
                String.format("Etapa '%s' concluída%s. Próxima: %s",
                        nomeEtapaConcluida,
                        input.comProblemas() ? " COM PROBLEMAS" : "",
                        proximaEtapa));
    }
}
