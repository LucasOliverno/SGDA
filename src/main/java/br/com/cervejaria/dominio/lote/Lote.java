package br.com.cervejaria.dominio.lote;

import br.com.cervejaria.dominio.comum.Densidade;
import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.comum.Medida;
import br.com.cervejaria.dominio.qualidade.AvaliacaoQualidade;
import br.com.cervejaria.dominio.qualidade.ParecerQualidade;
import br.com.cervejaria.dominio.receita.EtapaProducao;
import br.com.cervejaria.dominio.receita.Receita;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Aggregate Root que representa um lote de produção de cerveja.
 * 
 * <p>
 * Um lote é a execução real de uma receita - a produção física
 * de uma quantidade de cerveja.
 * </p>
 * 
 * <p>
 * Implementa uma máquina de estados explícita para controlar
 * o ciclo de vida da produção.
 * </p>
 * 
 * <p>
 * O Lote é dono de EtapaProducaoExecutada e AvaliacaoQualidade (composição).
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class Lote {

    private final String id;
    private final String codigo;
    private final Receita receita; // Imutável - lote sempre vinculado a uma receita
    private final LocalDateTime dataCriacao;

    private StatusLote status;
    private LocalDateTime dataInicio;
    private LocalDateTime dataConclusao;
    private Medida volumeReal;
    private Densidade og;
    private Densidade fg;
    private String observacoes;
    private String justificativaDescarte;

    // Composições - Lote é dono
    private final List<EtapaProducaoExecutada> etapasExecutadas;
    private final List<AvaliacaoQualidade> avaliacoes;

    /**
     * Cria um novo lote a partir de uma receita.
     * 
     * @param receita receita base (deve estar ativa)
     */
    public Lote(Receita receita) {
        validarReceitaParaCriarLote(receita);

        this.id = UUID.randomUUID().toString();
        this.codigo = gerarCodigo();
        this.receita = receita;
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusLote.PLANEJADO;
        this.etapasExecutadas = new ArrayList<>();
        this.avaliacoes = new ArrayList<>();

        // Cria etapas executadas a partir do modelo da receita
        for (EtapaProducao etapaModelo : receita.getEtapas()) {
            etapasExecutadas.add(new EtapaProducaoExecutada(etapaModelo));
        }
    }

    /**
     * Construtor para reconstituição.
     */
    public Lote(String id, String codigo, Receita receita) {
        if (id == null || id.isBlank()) {
            throw new DominioException("ID do lote não pode ser nulo ou vazio");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new DominioException("Código do lote não pode ser nulo ou vazio");
        }
        validarReceitaParaCriarLote(receita);

        this.id = id;
        this.codigo = codigo;
        this.receita = receita;
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusLote.PLANEJADO;
        this.etapasExecutadas = new ArrayList<>();
        this.avaliacoes = new ArrayList<>();

        for (EtapaProducao etapaModelo : receita.getEtapas()) {
            etapasExecutadas.add(new EtapaProducaoExecutada(etapaModelo));
        }
    }

    private void validarReceitaParaCriarLote(Receita receita) {
        if (receita == null) {
            throw new DominioException("Receita não pode ser nula para criar um lote");
        }
        if (!receita.podeCriarLote()) {
            throw new DominioException(
                    String.format("Receita '%s' não pode gerar lotes: status = %s. " +
                            "Apenas receitas ATIVAS podem gerar lotes.",
                            receita.getNome(), receita.getStatus()));
        }
    }

    private String gerarCodigo() {
        LocalDateTime agora = LocalDateTime.now();
        return String.format("%d%02d-%04d",
                agora.getYear(), agora.getMonthValue(),
                (int) (Math.random() * 10000));
    }

    // ==================== GETTERS ====================

    public String getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public Receita getReceita() {
        return receita;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public StatusLote getStatus() {
        return status;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }

    public Medida getVolumeReal() {
        return volumeReal;
    }

    public Densidade getOg() {
        return og;
    }

    public Densidade getFg() {
        return fg;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public String getJustificativaDescarte() {
        return justificativaDescarte;
    }

    public List<EtapaProducaoExecutada> getEtapasExecutadas() {
        return Collections.unmodifiableList(etapasExecutadas);
    }

    public List<AvaliacaoQualidade> getAvaliacoes() {
        return Collections.unmodifiableList(avaliacoes);
    }

    // ==================== TRANSIÇÕES DE ESTADO ====================

    /**
     * Inicia a produção do lote (Planejado → Em Brassagem).
     */
    public void iniciarProducao() {
        verificarTransicaoPermitida(StatusLote.EM_BRASSAGEM);
        verificarPodeEditar();

        this.status = StatusLote.EM_BRASSAGEM;
        this.dataInicio = LocalDateTime.now();

        // Inicia a primeira etapa automaticamente
        if (!etapasExecutadas.isEmpty()) {
            etapasExecutadas.get(0).iniciar();
        }
    }

    /**
     * Avança para fermentação (Em Brassagem → Fermentando).
     */
    public void iniciarFermentacao() {
        verificarTransicaoPermitida(StatusLote.FERMENTANDO);
        this.status = StatusLote.FERMENTANDO;
    }

    /**
     * Avança para maturação (Fermentando → Maturando).
     */
    public void iniciarMaturacao() {
        verificarTransicaoPermitida(StatusLote.MATURANDO);
        this.status = StatusLote.MATURANDO;
    }

    /**
     * Marca como pronto para envase (Maturando → Pronto para Envase).
     */
    public void marcarProntoParaEnvase() {
        verificarTransicaoPermitida(StatusLote.PRONTO_PARA_ENVASE);
        this.status = StatusLote.PRONTO_PARA_ENVASE;
    }

    /**
     * Retorna para maturação (Pronto para Envase → Maturando).
     */
    public void retornarParaMaturacao() {
        verificarTransicaoPermitida(StatusLote.MATURANDO);
        this.status = StatusLote.MATURANDO;
    }

    /**
     * Finaliza o lote como envasado (Pronto para Envase → Envasado).
     * 
     * @throws DominioException se não houver avaliação aprovada
     */
    public void envasar() {
        verificarTransicaoPermitida(StatusLote.ENVASADO);

        // Verifica se tem avaliação aprovada
        boolean temAvaliacaoAprovada = avaliacoes.stream()
                .anyMatch(AvaliacaoQualidade::aprovaParaEnvase);

        if (!temAvaliacaoAprovada) {
            throw new DominioException(
                    "Lote não pode ser envasado sem uma avaliação de qualidade aprovada");
        }

        this.status = StatusLote.ENVASADO;
        this.dataConclusao = LocalDateTime.now();
    }

    /**
     * Descarta o lote (qualquer estado não-final → Descartado).
     * 
     * @param justificativa motivo do descarte (obrigatório)
     */
    public void descartar(String justificativa) {
        verificarTransicaoPermitida(StatusLote.DESCARTADO);

        if (justificativa == null || justificativa.isBlank()) {
            throw new DominioException("Justificativa é obrigatória para descartar um lote");
        }

        this.justificativaDescarte = justificativa.trim();
        this.status = StatusLote.DESCARTADO;
        this.dataConclusao = LocalDateTime.now();
    }

    private void verificarTransicaoPermitida(StatusLote novoStatus) {
        if (!status.podeTransicionarPara(novoStatus)) {
            throw new DominioException(
                    String.format("Transição de '%s' para '%s' não é permitida. " +
                            "Transições válidas: %s",
                            status, novoStatus, status.transicoesPermitidas()));
        }
    }

    // ==================== REGISTRO DE DADOS ====================

    /**
     * Define o volume real produzido.
     */
    public void setVolumeReal(Medida volumeReal) {
        verificarPodeEditar();
        this.volumeReal = volumeReal;
    }

    /**
     * Registra a densidade original (OG).
     */
    public void registrarOG(Densidade og) {
        verificarPodeEditar();
        if (og != null && !og.isOG()) {
            throw new DominioException("Densidade informada não é do tipo OG");
        }
        this.og = og;
    }

    /**
     * Registra a densidade final (FG).
     */
    public void registrarFG(Densidade fg) {
        verificarPodeEditar();
        if (fg != null && !fg.isFG()) {
            throw new DominioException("Densidade informada não é do tipo FG");
        }

        // Valida que FG < OG
        if (fg != null && og != null && !fg.isMenorQue(og)) {
            throw new DominioException(
                    String.format("Densidade Final (%.3f) deve ser menor que a Original (%.3f). " +
                            "Se os valores estiverem corretos, pode haver problema na fermentação.",
                            fg.getValorEmSG(), og.getValorEmSG()));
        }

        this.fg = fg;
    }

    /**
     * Define observações do lote.
     */
    public void setObservacoes(String observacoes) {
        verificarPodeEditar();
        this.observacoes = observacoes;
    }

    // ==================== GERENCIAMENTO DE ETAPAS ====================

    /**
     * Retorna a etapa atualmente em andamento, ou null se nenhuma.
     */
    public Optional<EtapaProducaoExecutada> getEtapaEmAndamento() {
        return etapasExecutadas.stream()
                .filter(EtapaProducaoExecutada::isEmAndamento)
                .findFirst();
    }

    /**
     * Retorna a próxima etapa pendente, ou null se não houver.
     */
    public Optional<EtapaProducaoExecutada> getProximaEtapaPendente() {
        return etapasExecutadas.stream()
                .filter(EtapaProducaoExecutada::isPendente)
                .findFirst();
    }

    /**
     * Conclui a etapa atual e inicia a próxima.
     * 
     * @param temperaturaReal temperatura observada
     * @param observacoes     observações do operador
     */
    public void concluirEtapaAtual(Double temperaturaReal, String observacoes) {
        verificarPodeEditar();

        EtapaProducaoExecutada etapaAtual = getEtapaEmAndamento()
                .orElseThrow(() -> new DominioException("Não há etapa em andamento para concluir"));

        etapaAtual.concluir(temperaturaReal, observacoes);

        // Inicia próxima etapa automaticamente, se houver
        getProximaEtapaPendente().ifPresent(EtapaProducaoExecutada::iniciar);
    }

    /**
     * Verifica se todas as etapas foram concluídas.
     */
    public boolean todasEtapasConcluidas() {
        return etapasExecutadas.stream().allMatch(EtapaProducaoExecutada::isFinalizada);
    }

    // ==================== GERENCIAMENTO DE AVALIAÇÕES ====================

    /**
     * Adiciona uma avaliação de qualidade ao lote.
     * 
     * @param avaliacao avaliação a adicionar
     */
    public void adicionarAvaliacao(AvaliacaoQualidade avaliacao) {
        if (avaliacao == null) {
            throw new DominioException("Avaliação não pode ser nula");
        }
        // Avaliações podem ser adicionadas mesmo em lotes finais (para histórico)
        avaliacoes.add(avaliacao);
    }

    /**
     * Verifica se o lote tem alguma avaliação que aprova para envase.
     */
    public boolean temAvaliacaoAprovada() {
        return avaliacoes.stream().anyMatch(AvaliacaoQualidade::aprovaParaEnvase);
    }

    /**
     * Retorna a última avaliação registrada.
     */
    public Optional<AvaliacaoQualidade> getUltimaAvaliacao() {
        if (avaliacoes.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(avaliacoes.get(avaliacoes.size() - 1));
    }

    // ==================== CONSULTAS ====================

    /**
     * Verifica se o lote está em estado final (não pode mais ser alterado).
     */
    public boolean isEstadoFinal() {
        return status.isEstadoFinal();
    }

    /**
     * Verifica se o lote pode ser editado (não está em estado final).
     */
    public boolean podeEditar() {
        return !isEstadoFinal();
    }

    private void verificarPodeEditar() {
        if (isEstadoFinal()) {
            throw new DominioException(
                    String.format("Lote '%s' está em estado final (%s) e não pode ser alterado",
                            codigo, status));
        }
    }

    /**
     * Calcula o ABV estimado baseado em OG e FG.
     * 
     * @return ABV em percentual, ou null se OG ou FG não estiverem definidos
     */
    public Double calcularABV() {
        if (og == null || fg == null) {
            return null;
        }
        // Fórmula: ABV = (OG - FG) × 131.25
        double ogSG = og.getValorEmSG();
        double fgSG = fg.getValorEmSG();
        return (ogSG - fgSG) * 131.25;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Lote lote = (Lote) o;
        return Objects.equals(id, lote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Lote{codigo='%s', receita='%s', status=%s}",
                codigo, receita.getNome(), status);
    }
}
