package br.com.cervejaria.servico.calculo;

import br.com.cervejaria.dominio.comum.Densidade;
import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.comum.Medida;
import br.com.cervejaria.dominio.insumo.CategoriaInsumo;
import br.com.cervejaria.dominio.lote.Lote;
import br.com.cervejaria.dominio.receita.ItemReceita;
import br.com.cervejaria.dominio.receita.Receita;

import java.util.List;

/**
 * Serviço de Domínio para cálculos cervejeiros.
 * 
 * <p>
 * Realiza cálculos técnicos como IBU, ABV, eficiência e ajuste de volume.
 * </p>
 * 
 * <p>
 * Este serviço é stateless - não mantém estado entre chamadas.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class ServicoCalculoCervejeiro {

    /**
     * Calcula o ABV (Alcohol By Volume) a partir de OG e FG.
     * 
     * <p>
     * Fórmula: ABV = (OG - FG) × 131.25
     * </p>
     * 
     * @param og Densidade Original
     * @param fg Densidade Final
     * @return ABV em percentual (ex: 5.5 para 5.5%)
     * @throws DominioException se OG ou FG forem nulos, ou FG >= OG
     */
    public double calcularABV(Densidade og, Densidade fg) {
        if (og == null) {
            throw new DominioException("Densidade Original (OG) não pode ser nula para calcular ABV");
        }
        if (fg == null) {
            throw new DominioException("Densidade Final (FG) não pode ser nula para calcular ABV");
        }

        double ogSG = og.getValorEmSG();
        double fgSG = fg.getValorEmSG();

        if (fgSG >= ogSG) {
            throw new DominioException(
                    String.format("FG (%.3f) deve ser menor que OG (%.3f) para calcular ABV", fgSG, ogSG));
        }

        return (ogSG - fgSG) * 131.25;
    }

    /**
     * Calcula o ABV de um lote.
     * 
     * @param lote lote com OG e FG registrados
     * @return ABV em percentual
     * @throws DominioException se o lote não tiver OG e FG
     */
    public double calcularABV(Lote lote) {
        if (lote == null) {
            throw new DominioException("Lote não pode ser nulo");
        }
        return calcularABV(lote.getOg(), lote.getFg());
    }

    /**
     * Calcula a atenuação aparente.
     * 
     * <p>
     * Indica quanto dos açúcares foram convertidos em álcool.
     * </p>
     * <p>
     * Fórmula: Atenuação = (OG - FG) / (OG - 1.0) × 100
     * </p>
     * 
     * @param og Densidade Original
     * @param fg Densidade Final
     * @return atenuação em percentual
     */
    public double calcularAtenuacao(Densidade og, Densidade fg) {
        if (og == null || fg == null) {
            throw new DominioException("OG e FG são necessários para calcular atenuação");
        }

        double ogSG = og.getValorEmSG();
        double fgSG = fg.getValorEmSG();

        if (ogSG <= 1.0) {
            throw new DominioException("OG deve ser maior que 1.000");
        }

        return ((ogSG - fgSG) / (ogSG - 1.0)) * 100;
    }

    /**
     * Calcula o IBU estimado de uma receita (fórmula simplificada).
     * 
     * <p>
     * Fórmula Tinseth simplificada:
     * IBU = (massa_g × alfa_acido × utilizacao) / volume_L
     * </p>
     * 
     * <p>
     * A utilização depende do tempo de fervura e da densidade.
     * </p>
     * 
     * @param receita receita com lúpulos
     * @return IBU estimado
     */
    public double calcularIBUEstimado(Receita receita) {
        if (receita == null) {
            throw new DominioException("Receita não pode ser nula");
        }

        List<ItemReceita> lupulos = receita.getItensPorCategoria(CategoriaInsumo.LUPULO);
        if (lupulos.isEmpty()) {
            return 0;
        }

        double volumeLitros = receita.getVolumeProjetado().getValor();
        double ibuTotal = 0;

        for (ItemReceita item : lupulos) {
            // Para cálculo simplificado, assume alfa-ácido padrão de 5%
            // e utilização de 25% (média para 60min de fervura)
            double massaGramas = item.getQuantidade()
                    .converterPara(br.com.cervejaria.dominio.comum.UnidadeMedida.GRAMA)
                    .getValor();
            double alfaAcido = 0.05; // 5% - valor default
            double utilizacao = calcularUtilizacao(item.getTempoAdicaoMinutos());

            ibuTotal += (massaGramas * alfaAcido * utilizacao * 1000) / volumeLitros;
        }

        return ibuTotal;
    }

    /**
     * Calcula a utilização do lúpulo baseada no tempo de fervura.
     * 
     * @param tempoMinutos tempo de fervura em minutos
     * @return fator de utilização (0 a 0.35)
     */
    private double calcularUtilizacao(Integer tempoMinutos) {
        if (tempoMinutos == null || tempoMinutos <= 0) {
            return 0.05; // Adição em flameout
        }
        if (tempoMinutos <= 5)
            return 0.05;
        if (tempoMinutos <= 10)
            return 0.10;
        if (tempoMinutos <= 15)
            return 0.15;
        if (tempoMinutos <= 30)
            return 0.20;
        if (tempoMinutos <= 45)
            return 0.25;
        if (tempoMinutos <= 60)
            return 0.30;
        return 0.35; // 60+ minutos
    }

    /**
     * Escala uma receita para um novo volume.
     * 
     * <p>
     * Nota: Este método não modifica a receita original,
     * apenas retorna o fator de escala a ser aplicado.
     * </p>
     * 
     * @param receita    receita original
     * @param novoVolume novo volume desejado
     * @return fator de escala
     */
    public double calcularFatorEscala(Receita receita, Medida novoVolume) {
        if (receita == null || novoVolume == null) {
            throw new DominioException("Receita e novo volume são obrigatórios");
        }

        double volumeOriginal = receita.getVolumeProjetado().getValor();
        double volumeNovo = novoVolume.getValor();

        if (volumeOriginal <= 0) {
            throw new DominioException("Volume original deve ser maior que zero");
        }

        return volumeNovo / volumeOriginal;
    }

    /**
     * Calcula a eficiência da brassagem.
     * 
     * <p>
     * Compara a densidade obtida com a esperada baseada nos grãos usados.
     * </p>
     * 
     * @param ogObtida   OG realmente obtida
     * @param ogEsperada OG esperada (teórica)
     * @return eficiência em percentual
     */
    public double calcularEficiencia(Densidade ogObtida, Densidade ogEsperada) {
        if (ogObtida == null || ogEsperada == null) {
            throw new DominioException("OG obtida e esperada são necessárias");
        }

        double pontosobtidos = (ogObtida.getValorEmSG() - 1.0) * 1000;
        double pontosEsperados = (ogEsperada.getValorEmSG() - 1.0) * 1000;

        if (pontosEsperados <= 0) {
            return 0;
        }

        return (pontosobtidos / pontosEsperados) * 100;
    }

    /**
     * Estima a cor da cerveja em SRM (simplificado).
     * 
     * <p>
     * Fórmula Morey: SRM = 1.4922 × MCU^0.6859
     * </p>
     * <p>
     * MCU = (peso_lb × cor_lovibond) / volume_gal
     * </p>
     * 
     * @param receita receita com maltes
     * @return cor estimada em SRM
     */
    public double estimarCorSRM(Receita receita) {
        if (receita == null) {
            throw new DominioException("Receita não pode ser nula");
        }

        List<ItemReceita> maltes = receita.getItensPorCategoria(CategoriaInsumo.MALTE);
        if (maltes.isEmpty()) {
            return 0;
        }

        // Cálculo simplificado - assume cor média de 5 Lovibond para malte base
        double volumeGaloes = receita.getVolumeProjetado().getValor() * 0.264172; // L para gal
        double mcu = 0;

        for (ItemReceita item : maltes) {
            double pesoLibras = item.getQuantidade()
                    .converterPara(br.com.cervejaria.dominio.comum.UnidadeMedida.QUILOGRAMA)
                    .getValor() * 2.20462; // kg para lb
            double lovibond = 5.0; // valor default
            mcu += (pesoLibras * lovibond) / volumeGaloes;
        }

        return 1.4922 * Math.pow(mcu, 0.6859);
    }
}
