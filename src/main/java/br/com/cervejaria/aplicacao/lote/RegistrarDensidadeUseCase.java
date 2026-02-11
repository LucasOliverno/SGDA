package br.com.cervejaria.aplicacao.lote;

import br.com.cervejaria.dominio.comum.Densidade;
import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.comum.EscalaDensidade;
import br.com.cervejaria.dominio.comum.TipoDensidade;
import br.com.cervejaria.dominio.lote.Lote;
import br.com.cervejaria.infraestrutura.memoria.LoteRepositorioMemoria;

/**
 * Caso de Uso: Registrar Densidade
 * 
 * <p>
 * Registra a densidade (OG ou FG) de um lote.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class RegistrarDensidadeUseCase {

    private final LoteRepositorioMemoria loteRepositorio;

    public RegistrarDensidadeUseCase(LoteRepositorioMemoria loteRepositorio) {
        this.loteRepositorio = loteRepositorio;
    }

    /**
     * Dados de entrada.
     */
    public record RegistrarDensidadeInput(
            String loteId,
            TipoDensidade tipo,
            double valor,
            EscalaDensidade escala) {
    }

    /**
     * Resultado da operação.
     */
    public record RegistrarDensidadeOutput(
            String loteId,
            String tipo,
            double valor,
            String escala,
            Double abvEstimado,
            String mensagem) {
    }

    /**
     * Registra a densidade.
     * 
     * @param input dados de entrada
     * @return resultado da operação
     */
    public RegistrarDensidadeOutput executar(RegistrarDensidadeInput input) {
        if (input.loteId() == null || input.loteId().isBlank()) {
            throw new DominioException("ID do lote é obrigatório");
        }
        if (input.tipo() == null) {
            throw new DominioException("Tipo de densidade (OG/FG) é obrigatório");
        }

        Lote lote = loteRepositorio.buscarPorId(input.loteId())
                .orElseThrow(() -> new DominioException(
                        String.format("Lote não encontrado: %s", input.loteId())));

        // Cria a densidade
        EscalaDensidade escala = input.escala() != null ? input.escala() : EscalaDensidade.SG;
        Densidade densidade = new Densidade(input.valor(), input.tipo(), escala);

        // Registra conforme o tipo
        if (input.tipo() == TipoDensidade.OG) {
            lote.registrarOG(densidade);
        } else {
            lote.registrarFG(densidade);
        }

        // Salva
        loteRepositorio.salvar(lote);

        // Calcula ABV se possível
        Double abvEstimado = lote.calcularABV();

        String mensagem = String.format("Densidade %s registrada: %.3f %s",
                input.tipo(), input.valor(), escala.getSimbolo());

        if (abvEstimado != null) {
            mensagem += String.format(". ABV estimado: %.1f%%", abvEstimado);
        }

        return new RegistrarDensidadeOutput(
                lote.getId(),
                input.tipo().name(),
                input.valor(),
                escala.getSimbolo(),
                abvEstimado,
                mensagem);
    }
}
