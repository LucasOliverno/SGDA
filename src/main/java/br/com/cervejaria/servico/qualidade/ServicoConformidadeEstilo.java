package br.com.cervejaria.servico.qualidade;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.comum.FaixaParametro;
import br.com.cervejaria.dominio.insumo.Estilo;
import br.com.cervejaria.dominio.lote.Lote;
import br.com.cervejaria.dominio.receita.Receita;
import br.com.cervejaria.servico.calculo.ServicoCalculoCervejeiro;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de Domínio para verificar conformidade com estilos.
 * 
 * <p>
 * Compara parâmetros de uma cerveja com as faixas esperadas
 * para seu estilo.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class ServicoConformidadeEstilo {

    private final ServicoCalculoCervejeiro servicoCalculo;

    public ServicoConformidadeEstilo() {
        this.servicoCalculo = new ServicoCalculoCervejeiro();
    }

    public ServicoConformidadeEstilo(ServicoCalculoCervejeiro servicoCalculo) {
        this.servicoCalculo = servicoCalculo;
    }

    /**
     * Resultado da verificação de conformidade.
     */
    public record ResultadoConformidade(
        boolean conforme,
        List<String> naoConformidades,
        Double abvCalculado,
        Double ibuEstimado
    ) {
        public boolean isConforme() {
            return conforme;
        }
    }

    /**
     * Verifica a conformidade de um lote com o estilo da receita.
     * 
     * @param lote lote a verificar
     * @return resultado da verificação
     */
    public ResultadoConformidade verificarConformidade(Lote lote) {
        if (lote == null) {
            throw new DominioException("Lote não pode ser nulo");
        }

        Receita receita = lote.getReceita();
        Estilo estilo = receita.getEstilo();

        List<String> naoConformidades = new ArrayList<>();
        Double abvCalculado = null;
        Double ibuEstimado = null;

        // Calcula ABV se possível
        if (lote.getOg() != null && lote.getFg() != null) {
            abvCalculado = servicoCalculo.calcularABV(lote);

            // Verifica conformidade com estilo
            if (estilo != null && estilo.getFaixaABV() != null) {
                FaixaParametro faixaABV = estilo.getFaixaABV();
                if (!faixaABV.contem(abvCalculado)) {
                    naoConformidades.add(String.format(
                            "ABV (%.1f%%) fora da faixa do estilo %s (%s)",
                            abvCalculado, estilo.getNome(), faixaABV));
                }
            }
        }

        // Calcula IBU estimado
        ibuEstimado = servicoCalculo.calcularIBUEstimado(receita);

        if (estilo != null && estilo.getFaixaIBU() != null) {
            FaixaParametro faixaIBU = estilo.getFaixaIBU();
            if (!faixaIBU.contem(ibuEstimado)) {
                naoConformidades.add(String.format(
                        "IBU estimado (%.0f) fora da faixa do estilo %s (%s)",
                        ibuEstimado, estilo.getNome(), faixaIBU));
            }
        }

        boolean conforme = naoConformidades.isEmpty();
        return new ResultadoConformidade(conforme, naoConformidades, abvCalculado, ibuEstimado);
    }

    /**
     * Verifica a conformidade de uma receita com seu estilo (apenas IBU).
     * 
     * @param receita receita a verificar
     * @return resultado da verificação
     */
    public ResultadoConformidade verificarConformidadeReceita(Receita receita) {
        if (receita == null) {
            throw new DominioException("Receita não pode ser nula");
        }

        Estilo estilo = receita.getEstilo();
        List<String> naoConformidades = new ArrayList<>();

        Double ibuEstimado = servicoCalculo.calcularIBUEstimado(receita);

        if (estilo != null && estilo.getFaixaIBU() != null) {
            FaixaParametro faixaIBU = estilo.getFaixaIBU();
            if (!faixaIBU.contem(ibuEstimado)) {
                naoConformidades.add(String.format(
                        "IBU estimado (%.0f) fora da faixa do estilo %s (%s)",
                        ibuEstimado, estilo.getNome(), faixaIBU));
            }
        }

        boolean conforme = naoConformidades.isEmpty();
        return new ResultadoConformidade(conforme, naoConformidades, null, ibuEstimado);
    }

    /**
     * Gera relatório de conformidade textual.
     * 
     * @param lote lote a analisar
     * @return relatório em texto
     */
    public String gerarRelatorioConformidade(Lote lote) {
        ResultadoConformidade resultado = verificarConformidade(lote);

        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO DE CONFORMIDADE ===\n");
        sb.append(String.format("Lote: %s\n", lote.getCodigo()));
        sb.append(String.format("Receita: %s\n", lote.getReceita().getNome()));

        Estilo estilo = lote.getReceita().getEstilo();
        if (estilo != null) {
            sb.append(String.format("Estilo: %s\n", estilo.getNome()));
        }

        sb.append("\n--- Parâmetros Calculados ---\n");
        if (resultado.abvCalculado() != null) {
            sb.append(String.format("ABV: %.1f%%\n", resultado.abvCalculado()));
        } else {
            sb.append("ABV: (não calculado - falta OG ou FG)\n");
        }
        if (resultado.ibuEstimado() != null) {
            sb.append(String.format("IBU estimado: %.0f\n", resultado.ibuEstimado()));
        }

        sb.append("\n--- Resultado ---\n");
        if (resultado.isConforme()) {
            sb.append("✓ CONFORME ao estilo\n");
        } else {
            sb.append("✗ NÃO CONFORME\n");
            sb.append("Não conformidades:\n");
            for (String nc : resultado.naoConformidades()) {
                sb.append("  - ").append(nc).append("\n");
            }
        }

        return sb.toString();
    }
}
