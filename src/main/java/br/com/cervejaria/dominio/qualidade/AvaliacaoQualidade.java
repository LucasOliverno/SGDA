package br.com.cervejaria.dominio.qualidade;

import br.com.cervejaria.dominio.comum.DominioException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Avaliação de qualidade de um lote de produção.
 * 
 * <p>
 * Registra análises sensoriais ou técnicas feitas durante
 * ou após a produção.
 * </p>
 * 
 * <p>
 * Esta é uma entidade de composição - pertence a um Lote.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class AvaliacaoQualidade {

    private final String id;
    private final LocalDateTime dataAvaliacao;
    private final TipoAvaliacaoQualidade tipo;

    private String avaliador;
    private ParecerQualidade parecer;
    private String observacoes;

    // Campos para avaliação sensorial (notas 1-10)
    private Integer notaAparencia;
    private Integer notaAroma;
    private Integer notaSabor;
    private Integer notaCorpo;

    // Campos para avaliação técnica
    private Double ph;
    private Double temperatura;

    /**
     * Cria uma nova avaliação de qualidade.
     * 
     * @param tipo      tipo da avaliação
     * @param avaliador nome do avaliador
     */
    public AvaliacaoQualidade(TipoAvaliacaoQualidade tipo, String avaliador) {
        if (tipo == null) {
            throw new DominioException("Tipo da avaliação não pode ser nulo");
        }
        if (avaliador == null || avaliador.isBlank()) {
            throw new DominioException("Nome do avaliador não pode ser nulo ou vazio");
        }

        this.id = UUID.randomUUID().toString();
        this.dataAvaliacao = LocalDateTime.now();
        this.tipo = tipo;
        this.avaliador = avaliador.trim();
    }

    // ==================== GETTERS ====================

    public String getId() {
        return id;
    }

    public LocalDateTime getDataAvaliacao() {
        return dataAvaliacao;
    }

    public TipoAvaliacaoQualidade getTipo() {
        return tipo;
    }

    public String getAvaliador() {
        return avaliador;
    }

    public ParecerQualidade getParecer() {
        return parecer;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public Integer getNotaAparencia() {
        return notaAparencia;
    }

    public Integer getNotaAroma() {
        return notaAroma;
    }

    public Integer getNotaSabor() {
        return notaSabor;
    }

    public Integer getNotaCorpo() {
        return notaCorpo;
    }

    public Double getPh() {
        return ph;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    // ==================== SETTERS COM VALIDAÇÃO ====================

    public void setParecer(ParecerQualidade parecer) {
        if (parecer == null) {
            throw new DominioException("Parecer não pode ser nulo");
        }
        this.parecer = parecer;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public void setNotaAparencia(Integer nota) {
        validarNota(nota, "aparência");
        this.notaAparencia = nota;
    }

    public void setNotaAroma(Integer nota) {
        validarNota(nota, "aroma");
        this.notaAroma = nota;
    }

    public void setNotaSabor(Integer nota) {
        validarNota(nota, "sabor");
        this.notaSabor = nota;
    }

    public void setNotaCorpo(Integer nota) {
        validarNota(nota, "corpo");
        this.notaCorpo = nota;
    }

    private void validarNota(Integer nota, String campo) {
        if (nota != null && (nota < 1 || nota > 10)) {
            throw new DominioException(
                    String.format("Nota de %s deve estar entre 1 e 10. Valor informado: %d", campo, nota));
        }
    }

    public void setPh(Double ph) {
        if (ph != null && (ph < 0 || ph > 14)) {
            throw new DominioException(
                    String.format("pH deve estar entre 0 e 14. Valor informado: %.2f", ph));
        }
        this.ph = ph;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    // ==================== MÉTODOS DE NEGÓCIO ====================

    /**
     * Define todas as notas sensoriais de uma vez.
     */
    public void definirNotasSensoriais(int aparencia, int aroma, int sabor, int corpo) {
        if (tipo != TipoAvaliacaoQualidade.SENSORIAL) {
            throw new DominioException("Notas sensoriais só podem ser definidas em avaliações sensoriais");
        }
        setNotaAparencia(aparencia);
        setNotaAroma(aroma);
        setNotaSabor(sabor);
        setNotaCorpo(corpo);
    }

    /**
     * Calcula a média das notas sensoriais.
     * 
     * @return média ou null se não houver notas definidas
     */
    public Double getMediaNotasSensoriais() {
        if (notaAparencia == null || notaAroma == null ||
                notaSabor == null || notaCorpo == null) {
            return null;
        }
        return (notaAparencia + notaAroma + notaSabor + notaCorpo) / 4.0;
    }

    /**
     * Verifica se a avaliação está completa (tem parecer definido).
     */
    public boolean isCompleta() {
        return parecer != null;
    }

    /**
     * Verifica se esta avaliação aprova o lote para envase.
     */
    public boolean aprovaParaEnvase() {
        return parecer != null && parecer.permiteEnvase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AvaliacaoQualidade that = (AvaliacaoQualidade) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Avaliação %s por %s em %s: %s",
                tipo, avaliador, dataAvaliacao.toLocalDate(),
                parecer != null ? parecer : "Pendente");
    }
}
