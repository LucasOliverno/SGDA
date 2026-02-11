package br.com.cervejaria.servico.producao;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.comum.Medida;
import br.com.cervejaria.dominio.equipamento.Equipamento;
import br.com.cervejaria.dominio.equipamento.TipoEquipamento;
import br.com.cervejaria.dominio.estoque.Estoque;
import br.com.cervejaria.dominio.insumo.Insumo;
import br.com.cervejaria.dominio.receita.EtapaProducao;
import br.com.cervejaria.dominio.receita.Receita;
import br.com.cervejaria.servico.estoque.ServicoGestaoEstoque;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Serviço de Domínio para planejamento de produção.
 * 
 * <p>
 * Verifica viabilidade de produzir uma receita, considerando
 * estoque e equipamentos disponíveis.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class ServicoPlanejamentoProducao {

    private final ServicoGestaoEstoque servicoEstoque;

    public ServicoPlanejamentoProducao() {
        this.servicoEstoque = new ServicoGestaoEstoque();
    }

    public ServicoPlanejamentoProducao(ServicoGestaoEstoque servicoEstoque) {
        this.servicoEstoque = servicoEstoque;
    }

    /**
     * Resultado da análise de viabilidade de produção.
     */
    public record ResultadoViabilidade(
        boolean viavel,
        Map<Insumo, Medida> insumosFaltantes,
        List<TipoEquipamento> equipamentosFaltantes,
        List<String> problemas
    ) {
        public boolean isViavel() {
            return viavel;
        }
    }

    /**
     * Verifica a viabilidade de produzir uma receita.
     * 
     * @param receita      receita a produzir
     * @param estoques     mapa de estoques disponíveis
     * @param equipamentos lista de equipamentos disponíveis
     * @return resultado da análise de viabilidade
     */
    public ResultadoViabilidade verificarViabilidade(
            Receita receita,
            Map<String, Estoque> estoques,
            Collection<Equipamento> equipamentos) {

        List<String> problemas = new ArrayList<>();

        if (receita == null) {
            problemas.add("Receita não pode ser nula");
            return new ResultadoViabilidade(false, Map.of(), List.of(), problemas);
        }

        if (!receita.podeCriarLote()) {
            problemas.add(String.format("Receita '%s' não está ativa", receita.getNome()));
        }

        // Verifica estoque
        Map<Insumo, Medida> insumosFaltantes = servicoEstoque.verificarDisponibilidade(
                receita, estoques != null ? estoques : Map.of());

        if (!insumosFaltantes.isEmpty()) {
            problemas.add("Insumos insuficientes no estoque");
        }

        // Verifica equipamentos necessários
        List<TipoEquipamento> equipamentosFaltantes = verificarEquipamentosNecessarios(
                receita, equipamentos != null ? equipamentos : List.of());

        if (!equipamentosFaltantes.isEmpty()) {
            problemas.add("Equipamentos necessários não disponíveis");
        }

        // Verifica capacidade do fermentador
        if (equipamentos != null) {
            verificarCapacidadeFermentador(receita, equipamentos, problemas);
        }

        boolean viavel = problemas.isEmpty();
        return new ResultadoViabilidade(viavel, insumosFaltantes, equipamentosFaltantes, problemas);
    }

    private List<TipoEquipamento> verificarEquipamentosNecessarios(
            Receita receita, Collection<Equipamento> equipamentos) {

        List<TipoEquipamento> faltantes = new ArrayList<>();

        // Equipamentos básicos necessários
        Set<TipoEquipamento> necessarios = EnumSet.of(
                TipoEquipamento.FERMENTADOR);

        for (TipoEquipamento tipo : necessarios) {
            boolean temDisponivel = equipamentos.stream()
                    .anyMatch(e -> e.getTipo() == tipo && e.podeSerAlocado());

            if (!temDisponivel) {
                faltantes.add(tipo);
            }
        }

        return faltantes;
    }

    private void verificarCapacidadeFermentador(
            Receita receita, Collection<Equipamento> equipamentos, List<String> problemas) {

        Medida volumeReceita = receita.getVolumeProjetado();

        Optional<Equipamento> fermentadorAdequado = equipamentos.stream()
                .filter(e -> e.getTipo() == TipoEquipamento.FERMENTADOR)
                .filter(Equipamento::podeSerAlocado)
                .filter(e -> e.getCapacidade() != null &&
                        e.getCapacidade().isMaiorOuIgualA(volumeReceita))
                .findFirst();

        if (fermentadorAdequado.isEmpty()) {
            // Verifica se há algum fermentador disponível mas com capacidade insuficiente
            Optional<Equipamento> qualquerFermentador = equipamentos.stream()
                    .filter(e -> e.getTipo() == TipoEquipamento.FERMENTADOR)
                    .filter(Equipamento::podeSerAlocado)
                    .findFirst();

            if (qualquerFermentador.isPresent()) {
                Equipamento f = qualquerFermentador.get();
                problemas.add(String.format(
                        "Fermentador '%s' tem capacidade de %s, mas receita requer %s",
                        f.getNome(), f.getCapacidade(), volumeReceita));
            }
        }
    }

    /**
     * Calcula a duração estimada total de produção de uma receita.
     * 
     * @param receita receita a calcular
     * @return duração total estimada
     */
    public Duration calcularDuracaoEstimada(Receita receita) {
        if (receita == null) {
            throw new DominioException("Receita não pode ser nula");
        }

        Duration total = Duration.ZERO;
        for (EtapaProducao etapa : receita.getEtapas()) {
            if (etapa.getDuracaoEsperada() != null) {
                total = total.plus(etapa.getDuracaoEsperada());
            }
        }
        return total;
    }

    /**
     * Calcula a data estimada de conclusão se iniciar agora.
     * 
     * @param receita receita a calcular
     * @return data/hora estimada de conclusão
     */
    public LocalDateTime calcularDataConclusaoEstimada(Receita receita) {
        Duration duracao = calcularDuracaoEstimada(receita);
        return LocalDateTime.now().plus(duracao);
    }
}
