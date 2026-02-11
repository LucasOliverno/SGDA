package br.com.cervejaria.dominio.receita;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.comum.Medida;
import br.com.cervejaria.dominio.comum.UnidadeMedida;
import br.com.cervejaria.dominio.insumo.CategoriaInsumo;
import br.com.cervejaria.dominio.insumo.Estilo;
import br.com.cervejaria.dominio.insumo.Insumo;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Aggregate Root que representa uma receita de cerveja.
 * 
 * <p>
 * Uma receita define a "fórmula" de uma cerveja: quais insumos usar,
 * em que proporções, e quais etapas seguir durante a produção.
 * </p>
 * 
 * <p>
 * A Receita é dona de seus ItemReceita e EtapaProducao (composição).
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class Receita {

    private final String id;
    private String nome;
    private Estilo estilo;
    private Medida volumeProjetado;
    private StatusReceita status;
    private String notas;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private int versao;

    // Composições - Receita é dona
    private final List<ItemReceita> itens;
    private final List<EtapaProducao> etapas;

    /**
     * Cria uma nova receita em status RASCUNHO.
     * 
     * @param nome            nome da receita
     * @param volumeProjetado volume projetado em litros
     */
    public Receita(String nome, Medida volumeProjetado) {
        this.id = UUID.randomUUID().toString();
        this.itens = new ArrayList<>();
        this.etapas = new ArrayList<>();
        this.status = StatusReceita.RASCUNHO;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
        this.versao = 1;

        setNome(nome);
        setVolumeProjetado(volumeProjetado);
    }

    /**
     * Construtor para reconstituição.
     */
    public Receita(String id, String nome, Medida volumeProjetado) {
        if (id == null || id.isBlank()) {
            throw new DominioException("ID da receita não pode ser nulo ou vazio");
        }
        this.id = id;
        this.itens = new ArrayList<>();
        this.etapas = new ArrayList<>();
        this.status = StatusReceita.RASCUNHO;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
        this.versao = 1;

        setNome(nome);
        setVolumeProjetado(volumeProjetado);
    }

    // ==================== GETTERS/SETTERS ====================

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DominioException("Nome da receita não pode ser nulo ou vazio");
        }
        this.nome = nome.trim();
        atualizarTimestamp();
    }

    public Estilo getEstilo() {
        return estilo;
    }

    public void setEstilo(Estilo estilo) {
        this.estilo = estilo;
        atualizarTimestamp();
    }

    public Medida getVolumeProjetado() {
        return volumeProjetado;
    }

    public void setVolumeProjetado(Medida volumeProjetado) {
        if (volumeProjetado == null) {
            throw new DominioException("Volume projetado não pode ser nulo");
        }
        if (volumeProjetado.getUnidade().getTipo() != UnidadeMedida.TipoMedida.VOLUME) {
            throw new DominioException("Volume projetado deve usar unidade de volume");
        }
        if (volumeProjetado.isZero()) {
            throw new DominioException("Volume projetado não pode ser zero");
        }
        this.volumeProjetado = volumeProjetado;
        atualizarTimestamp();
    }

    public StatusReceita getStatus() {
        return status;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
        atualizarTimestamp();
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public int getVersao() {
        return versao;
    }

    // ==================== GERENCIAMENTO DE ITENS ====================

    /**
     * Retorna cópia imutável da lista de itens.
     */
    public List<ItemReceita> getItens() {
        return Collections.unmodifiableList(itens);
    }

    /**
     * Adiciona um item à receita.
     * 
     * @param insumo     insumo a adicionar
     * @param quantidade quantidade do insumo
     * @return o item adicionado
     */
    public ItemReceita adicionarItem(Insumo insumo, Medida quantidade) {
        verificarPodeEditar();

        // Verifica se o insumo já existe na receita
        Optional<ItemReceita> existente = itens.stream()
                .filter(i -> i.getInsumo().getId().equals(insumo.getId()))
                .findFirst();

        if (existente.isPresent()) {
            throw new DominioException(
                    String.format("Insumo '%s' já existe na receita. Use atualizarItem para modificar.",
                            insumo.getNome()));
        }

        ItemReceita item = new ItemReceita(insumo, quantidade);
        itens.add(item);
        atualizarTimestamp();
        return item;
    }

    /**
     * Remove um item da receita.
     */
    public void removerItem(Insumo insumo) {
        verificarPodeEditar();
        boolean removido = itens.removeIf(i -> i.getInsumo().getId().equals(insumo.getId()));
        if (!removido) {
            throw new DominioException(
                    String.format("Insumo '%s' não encontrado na receita", insumo.getNome()));
        }
        atualizarTimestamp();
    }

    /**
     * Busca um item pelo insumo.
     */
    public Optional<ItemReceita> buscarItem(Insumo insumo) {
        return itens.stream()
                .filter(i -> i.getInsumo().getId().equals(insumo.getId()))
                .findFirst();
    }

    /**
     * Retorna todos os itens de uma categoria específica.
     */
    public List<ItemReceita> getItensPorCategoria(CategoriaInsumo categoria) {
        return itens.stream()
                .filter(i -> i.getInsumo().getCategoria() == categoria)
                .toList();
    }

    // ==================== GERENCIAMENTO DE ETAPAS ====================

    /**
     * Retorna cópia imutável da lista de etapas (ordenada).
     */
    public List<EtapaProducao> getEtapas() {
        return Collections.unmodifiableList(etapas);
    }

    /**
     * Adiciona uma etapa na posição especificada.
     */
    public EtapaProducao adicionarEtapa(TipoEtapaProducao tipo) {
        verificarPodeEditar();
        int novaOrdem = etapas.size() + 1;
        EtapaProducao etapa = new EtapaProducao(novaOrdem, tipo);
        etapas.add(etapa);
        atualizarTimestamp();
        return etapa;
    }

    /**
     * Adiciona uma etapa com todos os parâmetros.
     */
    public EtapaProducao adicionarEtapa(TipoEtapaProducao tipo, long duracaoHoras,
            Double temperaturaAlvo, String instrucoes) {
        EtapaProducao etapa = adicionarEtapa(tipo);
        etapa.setDuracaoEsperadaEmHoras(duracaoHoras);
        etapa.setTemperaturaAlvo(temperaturaAlvo);
        etapa.setInstrucoes(instrucoes);
        return etapa;
    }

    /**
     * Remove a última etapa da receita.
     */
    public void removerUltimaEtapa() {
        verificarPodeEditar();
        if (etapas.isEmpty()) {
            throw new DominioException("Não há etapas para remover");
        }
        etapas.remove(etapas.size() - 1);
        atualizarTimestamp();
    }

    /**
     * Retorna a etapa por ordem.
     */
    public Optional<EtapaProducao> getEtapaPorOrdem(int ordem) {
        return etapas.stream()
                .filter(e -> e.getOrdem() == ordem)
                .findFirst();
    }

    // ==================== MUDANÇAS DE STATUS ====================

    /**
     * Ativa a receita, tornando-a disponível para criar lotes.
     * 
     * @throws DominioException se a receita não tiver os requisitos mínimos
     */
    public void ativar() {
        if (status == StatusReceita.ATIVA) {
            return; // Já está ativa
        }

        validarParaAtivacao();
        this.status = StatusReceita.ATIVA;
        atualizarTimestamp();
    }

    /**
     * Arquiva a receita.
     */
    public void arquivar() {
        this.status = StatusReceita.ARQUIVADA;
        atualizarTimestamp();
    }

    /**
     * Volta a receita para rascunho.
     */
    public void voltarParaRascunho() {
        this.status = StatusReceita.RASCUNHO;
        atualizarTimestamp();
    }

    // ==================== VALIDAÇÕES ====================

    /**
     * Valida se a receita tem os requisitos mínimos para ativação.
     */
    public void validarParaAtivacao() {
        List<String> erros = new ArrayList<>();

        // Verifica insumos obrigatórios
        for (CategoriaInsumo categoria : CategoriaInsumo.values()) {
            if (categoria.isObrigatorioEmReceita()) {
                boolean temCategoria = itens.stream()
                        .anyMatch(i -> i.getInsumo().getCategoria() == categoria);
                if (!temCategoria) {
                    erros.add(String.format("Falta insumo obrigatório: %s", categoria.getNome()));
                }
            }
        }

        // Verifica se tem pelo menos uma etapa
        if (etapas.isEmpty()) {
            erros.add("Receita deve ter pelo menos uma etapa de produção");
        }

        if (!erros.isEmpty()) {
            throw new DominioException(
                    "Receita não pode ser ativada:\n- " + String.join("\n- ", erros));
        }
    }

    /**
     * Verifica se a receita pode gerar lotes.
     */
    public boolean podeCriarLote() {
        return status.podeCriarLote();
    }

    /**
     * Verifica se a receita está em rascunho (editável).
     */
    public boolean isRascunho() {
        return status == StatusReceita.RASCUNHO;
    }

    /**
     * Verifica se a receita está ativa.
     */
    public boolean isAtiva() {
        return status == StatusReceita.ATIVA;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private void verificarPodeEditar() {
        if (status != StatusReceita.RASCUNHO) {
            throw new DominioException(
                    String.format("Receita '%s' não pode ser editada no status %s. " +
                            "Volte para RASCUNHO primeiro.", nome, status));
        }
    }

    private void atualizarTimestamp() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Incrementa a versão da receita.
     */
    public void incrementarVersao() {
        this.versao++;
        atualizarTimestamp();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Receita receita = (Receita) o;
        return Objects.equals(id, receita.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Receita{id='%s', nome='%s', status=%s, volume=%s, itens=%d, etapas=%d}",
                id, nome, status, volumeProjetado, itens.size(), etapas.size());
    }
}
