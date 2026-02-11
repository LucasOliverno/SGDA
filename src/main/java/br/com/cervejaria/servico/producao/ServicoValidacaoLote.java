package br.com.cervejaria.servico.producao;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.lote.Lote;
import br.com.cervejaria.dominio.lote.StatusLote;
import br.com.cervejaria.dominio.qualidade.AvaliacaoQualidade;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de Domínio para validação de lotes.
 * 
 * <p>
 * Verifica se um lote está cumprindo os parâmetros esperados
 * e pode avançar entre etapas.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class ServicoValidacaoLote {

    /**
     * Valida se um lote pode ser envasado.
     * 
     * @param lote lote a validar
     * @return lista de problemas encontrados (vazia se OK)
     */
    public List<String> validarParaEnvase(Lote lote) {
        List<String> problemas = new ArrayList<>();

        if (lote == null) {
            problemas.add("Lote não pode ser nulo");
            return problemas;
        }

        // Verifica status
        if (lote.getStatus() != StatusLote.PRONTO_PARA_ENVASE) {
            problemas.add(String.format("Lote deve estar 'Pronto para Envase' para ser envasado. Status atual: %s",
                    lote.getStatus()));
        }

        // Verifica avaliação aprovada
        if (!lote.temAvaliacaoAprovada()) {
            problemas.add("Lote não possui avaliação de qualidade aprovada");
        }

        // Verifica se todas as etapas foram concluídas
        if (!lote.todasEtapasConcluidas()) {
            problemas.add("Nem todas as etapas de produção foram concluídas");
        }

        // Verifica se OG e FG foram registrados
        if (lote.getOg() == null) {
            problemas.add("Densidade Original (OG) não foi registrada");
        }
        if (lote.getFg() == null) {
            problemas.add("Densidade Final (FG) não foi registrada");
        }

        return problemas;
    }

    /**
     * Verifica se o lote pode ser envasado.
     * 
     * @param lote lote a verificar
     * @return true se pode ser envasado
     */
    public boolean podeEnvasar(Lote lote) {
        return validarParaEnvase(lote).isEmpty();
    }

    /**
     * Valida se um lote pode iniciar produção.
     * 
     * @param lote lote a validar
     * @return lista de problemas encontrados (vazia se OK)
     */
    public List<String> validarParaIniciarProducao(Lote lote) {
        List<String> problemas = new ArrayList<>();

        if (lote == null) {
            problemas.add("Lote não pode ser nulo");
            return problemas;
        }

        if (lote.getStatus() != StatusLote.PLANEJADO) {
            problemas.add(String.format("Lote deve estar 'Planejado' para iniciar produção. Status atual: %s",
                    lote.getStatus()));
        }

        if (lote.getReceita() == null) {
            problemas.add("Lote não está vinculado a uma receita");
        }

        if (lote.getEtapasExecutadas().isEmpty()) {
            problemas.add("Lote não possui etapas de produção definidas");
        }

        return problemas;
    }

    /**
     * Valida se um lote pode avançar de etapa.
     * 
     * @param lote lote a validar
     * @return lista de problemas (vazia se OK)
     */
    public List<String> validarParaAvancarEtapa(Lote lote) {
        List<String> problemas = new ArrayList<>();

        if (lote == null) {
            problemas.add("Lote não pode ser nulo");
            return problemas;
        }

        if (lote.isEstadoFinal()) {
            problemas.add("Lote está em estado final e não pode avançar");
        }

        if (lote.getEtapaEmAndamento().isEmpty()) {
            problemas.add("Não há etapa em andamento para concluir");
        }

        return problemas;
    }

    /**
     * Valida uma avaliação de qualidade.
     * 
     * @param avaliacao avaliação a validar
     * @return lista de problemas (vazia se OK)
     */
    public List<String> validarAvaliacao(AvaliacaoQualidade avaliacao) {
        List<String> problemas = new ArrayList<>();

        if (avaliacao == null) {
            problemas.add("Avaliação não pode ser nula");
            return problemas;
        }

        if (avaliacao.getParecer() == null) {
            problemas.add("Parecer da avaliação não foi definido");
        }

        return problemas;
    }
}
