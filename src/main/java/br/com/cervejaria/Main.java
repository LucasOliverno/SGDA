package br.com.cervejaria;

import br.com.cervejaria.aplicacao.lote.*;
import br.com.cervejaria.aplicacao.receita.*;
import br.com.cervejaria.dominio.comum.*;
import br.com.cervejaria.dominio.insumo.*;
import br.com.cervejaria.dominio.lote.*;
import br.com.cervejaria.dominio.qualidade.*;
import br.com.cervejaria.dominio.receita.*;
import br.com.cervejaria.infraestrutura.memoria.*;
import br.com.cervejaria.servico.calculo.*;
import br.com.cervejaria.servico.qualidade.*;

/**
 * Aplicação de demonstração do sistema de cervejaria artesanal.
 * 
 * <p>
 * Demonstra o fluxo completo: criar receita, ativar, criar lote,
 * produzir, registrar densidades, avaliar e envasar.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class Main {

    // Repositórios
    private static final InsumoRepositorioMemoria insumoRepo = new InsumoRepositorioMemoria();
    private static final ReceitaRepositorioMemoria receitaRepo = new ReceitaRepositorioMemoria();
    private static final LoteRepositorioMemoria loteRepo = new LoteRepositorioMemoria();

    // Serviços
    private static final ServicoCalculoCervejeiro servicoCalculo = new ServicoCalculoCervejeiro();
    private static final ServicoConformidadeEstilo servicoConformidade = new ServicoConformidadeEstilo();

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║     🍺 SISTEMA DE GESTÃO DE CERVEJARIA ARTESANAL 🍺        ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        try {
            // 1. Cadastrar insumos
            cadastrarInsumos();

            // 2. Criar e configurar receita
            Receita receita = criarReceita();

            // 3. Ativar receita
            ativarReceita(receita);

            // 4. Criar lote de produção
            Lote lote = criarLote(receita);

            // 5. Iniciar e executar produção
            executarProducao(lote);

            // 6. Registrar densidades
            registrarDensidades(lote);

            // 7. Avaliar qualidade
            avaliarQualidade(lote);

            // 8. Envasar
            envasarLote(lote);

            // 9. Relatório final
            imprimirRelatorioFinal(lote);

        } catch (DominioException e) {
            System.err.println("\n❌ ERRO DE DOMÍNIO: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\n❌ ERRO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== ETAPA 1: CADASTRAR INSUMOS ====================

    private static void cadastrarInsumos() {
        System.out.println("📦 ETAPA 1: Cadastrando insumos...");
        System.out.println("─".repeat(50));

        // Maltes
        Insumo maltePilsen = new Insumo("Malte Pilsen", CategoriaInsumo.MALTE, UnidadeMedida.QUILOGRAMA);
        maltePilsen.setFabricante("Castle Malting");
        insumoRepo.salvar(maltePilsen);

        Insumo malteCaramelo = new Insumo("Malte Caramelo 40L", CategoriaInsumo.MALTE, UnidadeMedida.QUILOGRAMA);
        insumoRepo.salvar(malteCaramelo);

        // Lúpulos
        Insumo lupuloCascade = new Insumo("Lúpulo Cascade", CategoriaInsumo.LUPULO, UnidadeMedida.GRAMA);
        lupuloCascade.setCaracteristicas("Alfa-ácido: 5-7%. Aroma cítrico/floral.");
        insumoRepo.salvar(lupuloCascade);

        Insumo lupuloCitra = new Insumo("Lúpulo Citra", CategoriaInsumo.LUPULO, UnidadeMedida.GRAMA);
        lupuloCitra.setCaracteristicas("Alfa-ácido: 11-13%. Aroma tropical intenso.");
        insumoRepo.salvar(lupuloCitra);

        // Levedura
        Insumo leveduraUS05 = new Insumo("Levedura US-05", CategoriaInsumo.LEVEDURA, UnidadeMedida.GRAMA);
        leveduraUS05.setFabricante("Fermentis");
        insumoRepo.salvar(leveduraUS05);

        // Água
        Insumo agua = new Insumo("Água Mineral", CategoriaInsumo.AGUA, UnidadeMedida.LITRO);
        insumoRepo.salvar(agua);

        System.out.println("   ✓ " + insumoRepo.contar() + " insumos cadastrados");
        insumoRepo.buscarTodos()
                .forEach(i -> System.out.println("     - " + i.getNome() + " (" + i.getCategoria() + ")"));
        System.out.println();
    }

    // ==================== ETAPA 2: CRIAR RECEITA ====================

    private static Receita criarReceita() {
        System.out.println("📝 ETAPA 2: Criando receita...");
        System.out.println("─".repeat(50));

        // Cria a receita
        Receita receita = new Receita("American Pale Ale", Medida.litros(20));
        receita.setNotas("Receita base de APA com perfil cítrico e tropical");

        // Define estilo
        Estilo estiloAPA = new Estilo("American Pale Ale", "Ale");
        estiloAPA.comFaixas(
                FaixaParametro.ibu(30, 50),
                FaixaParametro.abv(4.5, 6.2),
                FaixaParametro.corSRM(5, 10));
        receita.setEstilo(estiloAPA);

        // Adiciona insumos
        Insumo maltePilsen = insumoRepo.buscarPorNome("Malte Pilsen").get(0);
        Insumo malteCaramelo = insumoRepo.buscarPorNome("Caramelo").get(0);
        Insumo lupuloCascade = insumoRepo.buscarPorNome("Cascade").get(0);
        Insumo lupuloCitra = insumoRepo.buscarPorNome("Citra").get(0);
        Insumo levedura = insumoRepo.buscarPorNome("US-05").get(0);
        Insumo agua = insumoRepo.buscarPorNome("Água").get(0);

        receita.adicionarItem(maltePilsen, Medida.quilogramas(4.5));
        receita.adicionarItem(malteCaramelo, Medida.quilogramas(0.3));

        ItemReceita itemCascade = receita.adicionarItem(lupuloCascade, Medida.gramas(30));
        itemCascade.setTempoAdicaoMinutos(60); // Amargor

        ItemReceita itemCitra = receita.adicionarItem(lupuloCitra, Medida.gramas(20));
        itemCitra.setTempoAdicaoMinutos(5); // Aroma

        receita.adicionarItem(levedura, Medida.gramas(11.5));
        receita.adicionarItem(agua, Medida.litros(25));

        // Adiciona etapas
        receita.adicionarEtapa(TipoEtapaProducao.MOSTURA, 1, 67.0, "Mostura simples a 67°C");
        receita.adicionarEtapa(TipoEtapaProducao.FILTRAGEM, 1, null, "Lavagem com 10L de água a 78°C");
        receita.adicionarEtapa(TipoEtapaProducao.FERVURA, 1, 100.0, "Fervura com adições de lúpulo");
        receita.adicionarEtapa(TipoEtapaProducao.RESFRIAMENTO, 1, 20.0, "Resfriar até 20°C");
        receita.adicionarEtapa(TipoEtapaProducao.FERMENTACAO_PRIMARIA, 168, 18.0, "7 dias a 18°C"); // 7 dias
        receita.adicionarEtapa(TipoEtapaProducao.MATURACAO, 336, 4.0, "14 dias a 4°C"); // 14 dias

        // Salva
        receitaRepo.salvar(receita);

        System.out.println("   ✓ Receita criada: " + receita.getNome());
        System.out.println("   ✓ Volume: " + receita.getVolumeProjetado());
        System.out.println("   ✓ Estilo: " + receita.getEstilo());
        System.out.println("   ✓ Status: " + receita.getStatus());
        System.out.println("   ✓ Itens: " + receita.getItens().size());
        System.out.println("   ✓ Etapas: " + receita.getEtapas().size());
        System.out.println();

        return receita;
    }

    // ==================== ETAPA 3: ATIVAR RECEITA ====================

    private static void ativarReceita(Receita receita) {
        System.out.println("✅ ETAPA 3: Ativando receita...");
        System.out.println("─".repeat(50));

        AtivarReceitaUseCase useCase = new AtivarReceitaUseCase(receitaRepo);
        AtivarReceitaUseCase.AtivarReceitaOutput output = useCase.executar(receita.getId());

        System.out.println("   ✓ " + output.mensagem());
        System.out.println("   ✓ Status: " + output.status());
        System.out.println();
    }

    // ==================== ETAPA 4: CRIAR LOTE ====================

    private static Lote criarLote(Receita receita) {
        System.out.println("🏭 ETAPA 4: Criando lote de produção...");
        System.out.println("─".repeat(50));

        CriarLoteUseCase useCase = new CriarLoteUseCase(loteRepo, receitaRepo);
        CriarLoteUseCase.CriarLoteOutput output = useCase.executar(receita.getId());

        System.out.println("   ✓ " + output.mensagem());
        System.out.println("   ✓ Código: " + output.codigo());
        System.out.println("   ✓ Etapas: " + output.totalEtapas());
        System.out.println();

        return loteRepo.buscarPorId(output.loteId()).orElseThrow();
    }

    // ==================== ETAPA 5: EXECUTAR PRODUÇÃO ====================

    private static void executarProducao(Lote lote) {
        System.out.println("⚙️ ETAPA 5: Executando produção...");
        System.out.println("─".repeat(50));

        // Inicia produção
        IniciarProducaoUseCase iniciarUseCase = new IniciarProducaoUseCase(loteRepo);
        IniciarProducaoUseCase.IniciarProducaoOutput inicioOutput = iniciarUseCase.executar(lote.getId());
        System.out.println("   ✓ " + inicioOutput.mensagem());

        // Executa etapas
        RegistrarEtapaUseCase etapaUseCase = new RegistrarEtapaUseCase(loteRepo);

        for (int i = 0; i < lote.getEtapasExecutadas().size(); i++) {
            RegistrarEtapaUseCase.RegistrarEtapaInput input = new RegistrarEtapaUseCase.RegistrarEtapaInput(
                    lote.getId(),
                    null, // temperatura
                    "Etapa concluída conforme planejado",
                    false // sem problemas
            );

            RegistrarEtapaUseCase.RegistrarEtapaOutput output = etapaUseCase.executar(input);
            System.out.println("   ✓ " + output.etapaConcluida() + " → " + output.proximaEtapa());

            if (output.todasConcluidas()) {
                break;
            }
        }

        // Avança o estado do lote conforme ciclo de vida
        lote.iniciarFermentacao();
        System.out.println("   ✓ Iniciando fermentação...");

        lote.iniciarMaturacao();
        System.out.println("   ✓ Iniciando maturação...");

        lote.marcarProntoParaEnvase();
        System.out.println("   ✓ Marcado como pronto para envase");

        loteRepo.salvar(lote);
        System.out.println();
    }

    // ==================== ETAPA 6: REGISTRAR DENSIDADES ====================

    private static void registrarDensidades(Lote lote) {
        System.out.println("📊 ETAPA 6: Registrando densidades...");
        System.out.println("─".repeat(50));

        RegistrarDensidadeUseCase useCase = new RegistrarDensidadeUseCase(loteRepo);

        // OG
        RegistrarDensidadeUseCase.RegistrarDensidadeInput ogInput = new RegistrarDensidadeUseCase.RegistrarDensidadeInput(
                lote.getId(), TipoDensidade.OG, 1.052, EscalaDensidade.SG);
        RegistrarDensidadeUseCase.RegistrarDensidadeOutput ogOutput = useCase.executar(ogInput);
        System.out.println("   ✓ " + ogOutput.mensagem());

        // FG
        RegistrarDensidadeUseCase.RegistrarDensidadeInput fgInput = new RegistrarDensidadeUseCase.RegistrarDensidadeInput(
                lote.getId(), TipoDensidade.FG, 1.012, EscalaDensidade.SG);
        RegistrarDensidadeUseCase.RegistrarDensidadeOutput fgOutput = useCase.executar(fgInput);
        System.out.println("   ✓ " + fgOutput.mensagem());
        System.out.println();
    }

    // ==================== ETAPA 7: AVALIAR QUALIDADE ====================

    private static void avaliarQualidade(Lote lote) {
        System.out.println("🔬 ETAPA 7: Avaliação de qualidade...");
        System.out.println("─".repeat(50));

        RegistrarAvaliacaoUseCase useCase = new RegistrarAvaliacaoUseCase(loteRepo);

        // Avaliação sensorial
        RegistrarAvaliacaoUseCase.AvaliacaoSensorialInput input = new RegistrarAvaliacaoUseCase.AvaliacaoSensorialInput(
                lote.getId(),
                "Mestre Cervejeiro João",
                8, // aparência
                9, // aroma
                8, // sabor
                7, // corpo
                ParecerQualidade.APROVADO,
                "Cerveja bem equilibrada, aroma cítrico presente.");

        RegistrarAvaliacaoUseCase.RegistrarAvaliacaoOutput output = useCase.executarSensorial(input);
        System.out.println("   ✓ " + output.mensagem());
        System.out.println("   ✓ Média: " + output.media());
        System.out.println("   ✓ Aprovado para envase: " + output.aprovadoParaEnvase());
        System.out.println();
    }

    // ==================== ETAPA 8: ENVASAR ====================

    private static void envasarLote(Lote lote) {
        System.out.println("🍾 ETAPA 8: Envasando lote...");
        System.out.println("─".repeat(50));

        // Recarrega para ter avaliação
        lote = loteRepo.buscarPorId(lote.getId()).orElseThrow();

        lote.envasar();
        loteRepo.salvar(lote);

        System.out.println("   ✓ Lote " + lote.getCodigo() + " envasado com sucesso!");
        System.out.println("   ✓ Status final: " + lote.getStatus());
        System.out.println();
    }

    // ==================== RELATÓRIO FINAL ====================

    private static void imprimirRelatorioFinal(Lote lote) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    📋 RELATÓRIO FINAL                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        lote = loteRepo.buscarPorId(lote.getId()).orElseThrow();

        System.out.println("  LOTE: " + lote.getCodigo());
        System.out.println("  RECEITA: " + lote.getReceita().getNome());
        System.out.println("  ESTILO: " + lote.getReceita().getEstilo());
        System.out.println("  STATUS: " + lote.getStatus().getDescricao());
        System.out.println();
        System.out.println("  DENSIDADES:");
        System.out.println("    OG: " + lote.getOg());
        System.out.println("    FG: " + lote.getFg());
        System.out.println("    ABV: " + String.format("%.1f%%", lote.calcularABV()));
        System.out.println();

        // Conformidade
        System.out.println("  CONFORMIDADE COM ESTILO:");
        String relatorio = servicoConformidade.gerarRelatorioConformidade(lote);
        System.out.println("  " + relatorio.replace("\n", "\n  "));
        System.out.println();

        // Avaliações
        System.out.println("  AVALIAÇÕES:");
        lote.getAvaliacoes().forEach(a -> {
            System.out.println("    - " + a);
            if (a.getMediaNotasSensoriais() != null) {
                System.out.println("      Média: " + a.getMediaNotasSensoriais());
            }
        });
        System.out.println();

        System.out.println("═".repeat(60));
        System.out.println("  🎉 Produção concluída com sucesso! 🍺");
        System.out.println("═".repeat(60));
    }
}
