package br.com.cervejaria.aplicacao.lote;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.lote.EtapaProducaoExecutada;
import br.com.cervejaria.dominio.lote.Lote;
import br.com.cervejaria.infraestrutura.memoria.LoteRepositorioMemoria;
import br.com.cervejaria.servico.producao.ServicoValidacaoLote;

import java.util.List;

/**
 * Caso de Uso: Iniciar Produção
 * 
 * <p>
 * Inicia a produção de um lote, mudando de PLANEJADO para EM_BRASSAGEM.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class IniciarProducaoUseCase {

    private final LoteRepositorioMemoria loteRepositorio;
    private final ServicoValidacaoLote servicoValidacao;

    public IniciarProducaoUseCase(LoteRepositorioMemoria loteRepositorio) {
        this.loteRepositorio = loteRepositorio;
        this.servicoValidacao = new ServicoValidacaoLote();
    }

    public IniciarProducaoUseCase(LoteRepositorioMemoria loteRepositorio,
            ServicoValidacaoLote servicoValidacao) {
        this.loteRepositorio = loteRepositorio;
        this.servicoValidacao = servicoValidacao;
    }

    /**
     * Resultado da operação.
     */
    public record IniciarProducaoOutput(
            String loteId,
            String codigo,
            String status,
            String etapaAtual,
            String mensagem) {
    }

    /**
     * Inicia a produção de um lote.
     * 
     * @param loteId ID do lote
     * @return resultado da operação
     * @throws DominioException se o lote não existir ou não puder iniciar
     */
    public IniciarProducaoOutput executar(String loteId) {
        if (loteId == null || loteId.isBlank()) {
            throw new DominioException("ID do lote é obrigatório");
        }

        Lote lote = loteRepositorio.buscarPorId(loteId)
                .orElseThrow(() -> new DominioException(
                        String.format("Lote não encontrado: %s", loteId)));

        // Valida
        List<String> problemas = servicoValidacao.validarParaIniciarProducao(lote);
        if (!problemas.isEmpty()) {
            throw new DominioException(
                    "Não é possível iniciar produção:\n- " + String.join("\n- ", problemas));
        }

        // Inicia
        lote.iniciarProducao();

        // Salva
        loteRepositorio.salvar(lote);

        // Obtém etapa atual
        String etapaAtual = lote.getEtapaEmAndamento()
                .map(e -> e.getModelo().getTipo().getNome())
                .orElse("N/A");

        return new IniciarProducaoOutput(
                lote.getId(),
                lote.getCodigo(),
                lote.getStatus().getDescricao(),
                etapaAtual,
                String.format("Produção do lote %s iniciada! Etapa atual: %s",
                        lote.getCodigo(), etapaAtual));
    }
}
