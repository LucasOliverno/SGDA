package br.com.cervejaria.aplicacao.lote;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.lote.Lote;
import br.com.cervejaria.dominio.qualidade.AvaliacaoQualidade;
import br.com.cervejaria.dominio.qualidade.ParecerQualidade;
import br.com.cervejaria.dominio.qualidade.TipoAvaliacaoQualidade;
import br.com.cervejaria.infraestrutura.memoria.LoteRepositorioMemoria;

/**
 * Caso de Uso: Registrar Avaliação de Qualidade
 * 
 * <p>
 * Registra uma avaliação de qualidade (sensorial ou técnica) em um lote.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class RegistrarAvaliacaoUseCase {

    private final LoteRepositorioMemoria loteRepositorio;

    public RegistrarAvaliacaoUseCase(LoteRepositorioMemoria loteRepositorio) {
        this.loteRepositorio = loteRepositorio;
    }

    /**
     * Dados de entrada para avaliação sensorial.
     */
    public record AvaliacaoSensorialInput(
            String loteId,
            String avaliador,
            int notaAparencia,
            int notaAroma,
            int notaSabor,
            int notaCorpo,
            ParecerQualidade parecer,
            String observacoes) {
    }

    /**
     * Dados de entrada para avaliação técnica.
     */
    public record AvaliacaoTecnicaInput(
            String loteId,
            String avaliador,
            Double ph,
            Double temperatura,
            ParecerQualidade parecer,
            String observacoes) {
    }

    /**
     * Resultado da operação.
     */
    public record RegistrarAvaliacaoOutput(
            String loteId,
            String tipo,
            String parecer,
            Double media,
            boolean aprovadoParaEnvase,
            String mensagem) {
    }

    /**
     * Registra avaliação sensorial.
     */
    public RegistrarAvaliacaoOutput executarSensorial(AvaliacaoSensorialInput input) {
        if (input.loteId() == null || input.loteId().isBlank()) {
            throw new DominioException("ID do lote é obrigatório");
        }

        Lote lote = loteRepositorio.buscarPorId(input.loteId())
                .orElseThrow(() -> new DominioException(
                        String.format("Lote não encontrado: %s", input.loteId())));

        // Cria avaliação
        AvaliacaoQualidade avaliacao = new AvaliacaoQualidade(
                TipoAvaliacaoQualidade.SENSORIAL, input.avaliador());

        avaliacao.definirNotasSensoriais(
                input.notaAparencia(), input.notaAroma(),
                input.notaSabor(), input.notaCorpo());
        avaliacao.setParecer(input.parecer());
        avaliacao.setObservacoes(input.observacoes());

        // Adiciona ao lote
        lote.adicionarAvaliacao(avaliacao);

        // Salva
        loteRepositorio.salvar(lote);

        return new RegistrarAvaliacaoOutput(
                lote.getId(),
                "SENSORIAL",
                input.parecer().getDescricao(),
                avaliacao.getMediaNotasSensoriais(),
                avaliacao.aprovaParaEnvase(),
                String.format("Avaliação sensorial registrada: %s (média: %.1f)",
                        input.parecer(), avaliacao.getMediaNotasSensoriais()));
    }

    /**
     * Registra avaliação técnica.
     */
    public RegistrarAvaliacaoOutput executarTecnica(AvaliacaoTecnicaInput input) {
        if (input.loteId() == null || input.loteId().isBlank()) {
            throw new DominioException("ID do lote é obrigatório");
        }

        Lote lote = loteRepositorio.buscarPorId(input.loteId())
                .orElseThrow(() -> new DominioException(
                        String.format("Lote não encontrado: %s", input.loteId())));

        // Cria avaliação
        AvaliacaoQualidade avaliacao = new AvaliacaoQualidade(
                TipoAvaliacaoQualidade.TECNICA, input.avaliador());

        avaliacao.setPh(input.ph());
        avaliacao.setTemperatura(input.temperatura());
        avaliacao.setParecer(input.parecer());
        avaliacao.setObservacoes(input.observacoes());

        // Adiciona ao lote
        lote.adicionarAvaliacao(avaliacao);

        // Salva
        loteRepositorio.salvar(lote);

        String detalhes = "";
        if (input.ph() != null) {
            detalhes += String.format("pH: %.2f", input.ph());
        }

        return new RegistrarAvaliacaoOutput(
                lote.getId(),
                "TECNICA",
                input.parecer().getDescricao(),
                null,
                avaliacao.aprovaParaEnvase(),
                String.format("Avaliação técnica registrada: %s. %s",
                        input.parecer(), detalhes));
    }
}
