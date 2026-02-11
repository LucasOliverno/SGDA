package br.com.cervejaria.servico.estoque;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.comum.Medida;
import br.com.cervejaria.dominio.estoque.Estoque;
import br.com.cervejaria.dominio.estoque.MotivoMovimento;
import br.com.cervejaria.dominio.insumo.Insumo;
import br.com.cervejaria.dominio.receita.ItemReceita;
import br.com.cervejaria.dominio.receita.Receita;

import java.util.*;

/**
 * Serviço de Domínio para gestão de estoque.
 * 
 * <p>
 * Coordena operações de estoque: verificação de disponibilidade,
 * reserva e baixa de insumos para produção.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class ServicoGestaoEstoque {

    /**
     * Verifica se há estoque suficiente para produzir uma receita.
     * 
     * @param receita  receita a verificar
     * @param estoques mapa de insumoId -> Estoque
     * @return mapa de insumos faltantes com quantidade faltante
     */
    public Map<Insumo, Medida> verificarDisponibilidade(Receita receita, Map<String, Estoque> estoques) {
        if (receita == null) {
            throw new DominioException("Receita não pode ser nula");
        }
        if (estoques == null) {
            estoques = Collections.emptyMap();
        }

        Map<Insumo, Medida> faltantes = new LinkedHashMap<>();

        for (ItemReceita item : receita.getItens()) {
            Insumo insumo = item.getInsumo();
            Medida necessaria = item.getQuantidade();

            Estoque estoque = estoques.get(insumo.getId());

            if (estoque == null) {
                // Não há estoque cadastrado
                faltantes.put(insumo, necessaria);
            } else if (!estoque.temDisponivel(necessaria)) {
                // Estoque insuficiente
                Medida disponivel = estoque.getQuantidadeAtual();
                Medida falta = necessaria.subtrair(disponivel);
                faltantes.put(insumo, falta);
            }
        }

        return faltantes;
    }

    /**
     * Verifica se há estoque suficiente para produzir uma receita.
     * 
     * @param receita  receita a verificar
     * @param estoques mapa de insumoId -> Estoque
     * @return true se há estoque suficiente para todos os insumos
     */
    public boolean temEstoqueSuficiente(Receita receita, Map<String, Estoque> estoques) {
        return verificarDisponibilidade(receita, estoques).isEmpty();
    }

    /**
     * Dá baixa no estoque para todos os insumos de uma receita.
     * 
     * @param receita  receita sendo produzida
     * @param loteId   ID do lote de produção
     * @param estoques mapa de insumoId -> Estoque
     * @throws DominioException se não houver estoque suficiente
     */
    public void darBaixaParaProducao(Receita receita, String loteId, Map<String, Estoque> estoques) {
        if (receita == null) {
            throw new DominioException("Receita não pode ser nula");
        }
        if (loteId == null || loteId.isBlank()) {
            throw new DominioException("ID do lote é obrigatório");
        }
        if (estoques == null) {
            estoques = Collections.emptyMap();
        }

        // Primeiro verifica se há estoque suficiente para tudo
        Map<Insumo, Medida> faltantes = verificarDisponibilidade(receita, estoques);
        if (!faltantes.isEmpty()) {
            StringBuilder msg = new StringBuilder("Estoque insuficiente para os seguintes insumos:\n");
            faltantes.forEach(
                    (insumo, falta) -> msg.append(String.format("- %s: faltam %s\n", insumo.getNome(), falta)));
            throw new DominioException(msg.toString());
        }

        // Agora dá baixa em cada item
        for (ItemReceita item : receita.getItens()) {
            Estoque estoque = estoques.get(item.getInsumo().getId());
            estoque.registrarSaida(item.getQuantidade(), MotivoMovimento.USO_PRODUCAO, loteId);
        }
    }

    /**
     * Retorna lista de estoques abaixo do mínimo.
     * 
     * @param estoques coleção de estoques
     * @return lista de estoques abaixo do mínimo
     */
    public List<Estoque> getEstoquesAbaixoDoMinimo(Collection<Estoque> estoques) {
        if (estoques == null) {
            return Collections.emptyList();
        }
        return estoques.stream()
                .filter(Estoque::isAbaixoDoMinimo)
                .toList();
    }

    /**
     * Gera alertas de estoque baixo.
     * 
     * @param estoques coleção de estoques
     * @return lista de mensagens de alerta
     */
    public List<String> gerarAlertasEstoqueBaixo(Collection<Estoque> estoques) {
        return getEstoquesAbaixoDoMinimo(estoques).stream()
                .map(e -> String.format("ALERTA: Estoque de '%s' abaixo do mínimo. " +
                        "Atual: %s, Mínimo: %s, Faltam: %s",
                        e.getInsumo().getNome(),
                        e.getQuantidadeAtual(),
                        e.getQuantidadeMinima(),
                        e.getQuantidadeFaltante()))
                .toList();
    }
}
