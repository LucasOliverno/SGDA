package br.com.cervejaria.apresentacao;

import br.com.cervejaria.aplicacao.lote.*;
import br.com.cervejaria.aplicacao.receita.*;
import br.com.cervejaria.infraestrutura.memoria.*;
import br.com.cervejaria.servico.calculo.*;
import br.com.cervejaria.servico.estoque.*;
import br.com.cervejaria.servico.producao.*;
import br.com.cervejaria.servico.qualidade.*;

/**
 * Contexto de aplicação para injeção manual de dependências.
 * 
 * <p>
 * Singleton que mantém as instâncias compartilhadas entre controllers.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class AppContext {

    private static AppContext instance;

    // Repositórios
    private final InsumoRepositorioMemoria insumoRepositorio;
    private final ReceitaRepositorioMemoria receitaRepositorio;
    private final LoteRepositorioMemoria loteRepositorio;
    private final EquipamentoRepositorioMemoria equipamentoRepositorio;
    private final EstoqueRepositorioMemoria estoqueRepositorio;

    // Serviços
    private final ServicoCalculoCervejeiro servicoCalculo;
    private final ServicoGestaoEstoque servicoEstoque;
    private final ServicoValidacaoLote servicoValidacaoLote;
    private final ServicoPlanejamentoProducao servicoPlanejamento;
    private final ServicoConformidadeEstilo servicoConformidade;

    // Casos de Uso
    private final CriarReceitaUseCase criarReceitaUseCase;
    private final AtivarReceitaUseCase ativarReceitaUseCase;
    private final CriarLoteUseCase criarLoteUseCase;
    private final IniciarProducaoUseCase iniciarProducaoUseCase;
    private final RegistrarEtapaUseCase registrarEtapaUseCase;
    private final RegistrarDensidadeUseCase registrarDensidadeUseCase;
    private final RegistrarAvaliacaoUseCase registrarAvaliacaoUseCase;
    private final DescartarLoteUseCase descartarLoteUseCase;

    private AppContext() {
        // Inicializa repositórios
        this.insumoRepositorio = new InsumoRepositorioMemoria();
        this.receitaRepositorio = new ReceitaRepositorioMemoria();
        this.loteRepositorio = new LoteRepositorioMemoria();
        this.equipamentoRepositorio = new EquipamentoRepositorioMemoria();
        this.estoqueRepositorio = new EstoqueRepositorioMemoria();

        // Inicializa serviços
        this.servicoCalculo = new ServicoCalculoCervejeiro();
        this.servicoEstoque = new ServicoGestaoEstoque();
        this.servicoValidacaoLote = new ServicoValidacaoLote();
        this.servicoPlanejamento = new ServicoPlanejamentoProducao(servicoEstoque);
        this.servicoConformidade = new ServicoConformidadeEstilo(servicoCalculo);

        // Inicializa casos de uso
        this.criarReceitaUseCase = new CriarReceitaUseCase(receitaRepositorio);
        this.ativarReceitaUseCase = new AtivarReceitaUseCase(receitaRepositorio);
        this.criarLoteUseCase = new CriarLoteUseCase(loteRepositorio, receitaRepositorio);
        this.iniciarProducaoUseCase = new IniciarProducaoUseCase(loteRepositorio, servicoValidacaoLote);
        this.registrarEtapaUseCase = new RegistrarEtapaUseCase(loteRepositorio);
        this.registrarDensidadeUseCase = new RegistrarDensidadeUseCase(loteRepositorio);
        this.registrarAvaliacaoUseCase = new RegistrarAvaliacaoUseCase(loteRepositorio);
        this.descartarLoteUseCase = new DescartarLoteUseCase(loteRepositorio);
    }

    /**
     * Retorna a instância única do contexto.
     */
    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    // ==================== GETTERS REPOSITÓRIOS ====================

    public InsumoRepositorioMemoria getInsumoRepositorio() {
        return insumoRepositorio;
    }

    public ReceitaRepositorioMemoria getReceitaRepositorio() {
        return receitaRepositorio;
    }

    public LoteRepositorioMemoria getLoteRepositorio() {
        return loteRepositorio;
    }

    public EquipamentoRepositorioMemoria getEquipamentoRepositorio() {
        return equipamentoRepositorio;
    }

    public EstoqueRepositorioMemoria getEstoqueRepositorio() {
        return estoqueRepositorio;
    }

    // ==================== GETTERS SERVIÇOS ====================

    public ServicoCalculoCervejeiro getServicoCalculo() {
        return servicoCalculo;
    }

    public ServicoGestaoEstoque getServicoEstoque() {
        return servicoEstoque;
    }

    public ServicoValidacaoLote getServicoValidacaoLote() {
        return servicoValidacaoLote;
    }

    public ServicoPlanejamentoProducao getServicoPlanejamento() {
        return servicoPlanejamento;
    }

    public ServicoConformidadeEstilo getServicoConformidade() {
        return servicoConformidade;
    }

    // ==================== GETTERS CASOS DE USO ====================

    public CriarReceitaUseCase getCriarReceitaUseCase() {
        return criarReceitaUseCase;
    }

    public AtivarReceitaUseCase getAtivarReceitaUseCase() {
        return ativarReceitaUseCase;
    }

    public CriarLoteUseCase getCriarLoteUseCase() {
        return criarLoteUseCase;
    }

    public IniciarProducaoUseCase getIniciarProducaoUseCase() {
        return iniciarProducaoUseCase;
    }

    public RegistrarEtapaUseCase getRegistrarEtapaUseCase() {
        return registrarEtapaUseCase;
    }

    public RegistrarDensidadeUseCase getRegistrarDensidadeUseCase() {
        return registrarDensidadeUseCase;
    }

    public RegistrarAvaliacaoUseCase getRegistrarAvaliacaoUseCase() {
        return registrarAvaliacaoUseCase;
    }

    public DescartarLoteUseCase getDescartarLoteUseCase() {
        return descartarLoteUseCase;
    }
}
