package br.com.cervejaria.apresentacao;

import br.com.cervejaria.aplicacao.receita.*;
import br.com.cervejaria.dominio.comum.*;
import br.com.cervejaria.dominio.insumo.Insumo;
import br.com.cervejaria.dominio.receita.*;
import br.com.cervejaria.infraestrutura.memoria.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Controller para tela de gestão de receitas.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class ReceitaController {

    private final AppContext appContext;
    private final ReceitaRepositorioMemoria receitaRepo;
    private final InsumoRepositorioMemoria insumoRepo;
    private final CriarReceitaUseCase criarReceitaUseCase;
    private final AtivarReceitaUseCase ativarReceitaUseCase;

    private final VBox view;

    // Campos do formulário
    private TextField txtNome;
    private TextField txtVolume;
    private TextArea txtNotas;

    // Tabelas
    private TableView<Receita> tabelaReceitas;
    private ObservableList<Receita> listaReceitas;

    // Receita selecionada para edição
    private ListView<String> lstItensReceita;
    private ListView<String> lstEtapasReceita;
    private ComboBox<Insumo> cmbInsumo;
    private TextField txtQuantidade;
    private ComboBox<TipoEtapaProducao> cmbEtapaTipo;

    public ReceitaController(AppContext appContext) {
        this.appContext = appContext;
        this.receitaRepo = appContext.getReceitaRepositorio();
        this.insumoRepo = appContext.getInsumoRepositorio();
        this.criarReceitaUseCase = appContext.getCriarReceitaUseCase();
        this.ativarReceitaUseCase = appContext.getAtivarReceitaUseCase();
        this.listaReceitas = FXCollections.observableArrayList();
        this.view = criarView();
        atualizarLista();
    }

    public VBox getView() {
        return view;
    }

    private VBox criarView() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));

        // Título
        Label titulo = new Label("Gestão de Receitas");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Split horizontal: esquerda (formulário + lista) e direita (detalhes)
        HBox splitPane = new HBox(20);
        splitPane.setAlignment(Pos.TOP_LEFT);

        // Lado esquerdo
        VBox ladoEsquerdo = criarLadoEsquerdo();
        ladoEsquerdo.setPrefWidth(400);

        // Lado direito
        VBox ladoDireito = criarLadoDireito();
        ladoDireito.setPrefWidth(450);
        HBox.setHgrow(ladoDireito, Priority.ALWAYS);

        splitPane.getChildren().addAll(ladoEsquerdo, ladoDireito);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        container.getChildren().addAll(titulo, splitPane);
        return container;
    }

    private VBox criarLadoEsquerdo() {
        VBox box = new VBox(10);

        // Formulário de criação
        TitledPane paneFormulario = new TitledPane();
        paneFormulario.setText("Nova Receita");
        paneFormulario.setCollapsible(false);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(10));

        txtNome = new TextField();
        txtNome.setPromptText("Nome da receita");

        txtVolume = new TextField();
        txtVolume.setPromptText("Volume em litros");
        txtVolume.setPrefWidth(80);

        txtNotas = new TextArea();
        txtNotas.setPromptText("Notas (opcional)");
        txtNotas.setPrefRowCount(2);

        formGrid.add(new Label("Nome:"), 0, 0);
        formGrid.add(txtNome, 1, 0);
        formGrid.add(new Label("Volume (L):"), 0, 1);
        formGrid.add(txtVolume, 1, 1);
        formGrid.add(new Label("Notas:"), 0, 2);
        formGrid.add(txtNotas, 1, 2);

        Button btnCriar = new Button("✓ Criar Receita");
        btnCriar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnCriar.setOnAction(e -> criarReceita());

        VBox formBox = new VBox(10, formGrid, btnCriar);
        formBox.setPadding(new Insets(5));
        paneFormulario.setContent(formBox);

        // Tabela de receitas
        TitledPane paneTabela = new TitledPane();
        paneTabela.setText("Receitas Cadastradas");
        paneTabela.setCollapsible(false);
        tabelaReceitas = criarTabelaReceitas();
        VBox.setVgrow(paneTabela, Priority.ALWAYS);

        VBox tableBox = new VBox(tabelaReceitas);
        VBox.setVgrow(tabelaReceitas, Priority.ALWAYS);
        paneTabela.setContent(tableBox);

        box.getChildren().addAll(paneFormulario, paneTabela);
        VBox.setVgrow(paneTabela, Priority.ALWAYS);

        return box;
    }

    private VBox criarLadoDireito() {
        VBox box = new VBox(10);

        TitledPane paneDetalhes = new TitledPane();
        paneDetalhes.setText("Detalhes da Receita Selecionada");
        paneDetalhes.setCollapsible(false);
        VBox.setVgrow(paneDetalhes, Priority.ALWAYS);

        VBox detalhesBox = new VBox(10);
        detalhesBox.setPadding(new Insets(10));

        // Adicionar insumo
        HBox addInsumoBox = new HBox(10);
        addInsumoBox.setAlignment(Pos.CENTER_LEFT);

        cmbInsumo = new ComboBox<>();
        cmbInsumo.setPromptText("Insumo");
        cmbInsumo.setPrefWidth(150);

        txtQuantidade = new TextField();
        txtQuantidade.setPromptText("Qtd");
        txtQuantidade.setPrefWidth(60);

        Button btnAddInsumo = new Button("+ Insumo");
        btnAddInsumo.setOnAction(e -> adicionarInsumo());

        addInsumoBox.getChildren().addAll(cmbInsumo, txtQuantidade, btnAddInsumo);

        // Lista de itens
        lstItensReceita = new ListView<>();
        lstItensReceita.setPrefHeight(120);
        lstItensReceita.setPlaceholder(new Label("Selecione uma receita"));

        // Adicionar etapa
        HBox addEtapaBox = new HBox(10);
        addEtapaBox.setAlignment(Pos.CENTER_LEFT);

        cmbEtapaTipo = new ComboBox<>();
        cmbEtapaTipo.getItems().addAll(TipoEtapaProducao.values());
        cmbEtapaTipo.setPromptText("Tipo de Etapa");

        Button btnAddEtapa = new Button("+ Etapa");
        btnAddEtapa.setOnAction(e -> adicionarEtapa());

        addEtapaBox.getChildren().addAll(cmbEtapaTipo, btnAddEtapa);

        // Lista de etapas
        lstEtapasReceita = new ListView<>();
        lstEtapasReceita.setPrefHeight(100);
        lstEtapasReceita.setPlaceholder(new Label("Selecione uma receita"));

        // Botões de ação
        HBox acoesBox = new HBox(10);
        acoesBox.setAlignment(Pos.CENTER_LEFT);

        Button btnAtivar = new Button("✓ Ativar Receita");
        btnAtivar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        btnAtivar.setOnAction(e -> ativarReceita());

        Button btnArquivar = new Button("Arquivar");
        btnArquivar.setOnAction(e -> arquivarReceita());

        acoesBox.getChildren().addAll(btnAtivar, btnArquivar);

        detalhesBox.getChildren().addAll(
                new Label("Adicionar Insumo:"),
                addInsumoBox,
                new Label("Itens da Receita:"),
                lstItensReceita,
                new Separator(),
                new Label("Adicionar Etapa:"),
                addEtapaBox,
                new Label("Etapas da Receita:"),
                lstEtapasReceita,
                new Separator(),
                acoesBox);

        paneDetalhes.setContent(detalhesBox);
        box.getChildren().add(paneDetalhes);
        VBox.setVgrow(paneDetalhes, Priority.ALWAYS);

        return box;
    }

    @SuppressWarnings("unchecked")
    private TableView<Receita> criarTabelaReceitas() {
        TableView<Receita> tabela = new TableView<>();
        tabela.setPlaceholder(new Label("Nenhuma receita cadastrada"));

        TableColumn<Receita, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colNome.setPrefWidth(150);

        TableColumn<Receita, StatusReceita> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(80);

        TableColumn<Receita, Medida> colVolume = new TableColumn<>("Volume");
        colVolume.setCellValueFactory(new PropertyValueFactory<>("volumeProjetado"));
        colVolume.setPrefWidth(80);

        tabela.getColumns().addAll(colNome, colStatus, colVolume);
        tabela.setItems(listaReceitas);

        // Ao selecionar, carregar detalhes
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, old, novo) -> {
            if (novo != null) {
                carregarDetalhesReceita(novo);
            }
        });

        return tabela;
    }

    private void criarReceita() {
        try {
            if (txtNome.getText().isBlank()) {
                mostrarErro("Nome é obrigatório");
                return;
            }
            if (txtVolume.getText().isBlank()) {
                mostrarErro("Volume é obrigatório");
                return;
            }

            double volume = Double.parseDouble(txtVolume.getText().trim());

            CriarReceitaUseCase.CriarReceitaInput input = new CriarReceitaUseCase.CriarReceitaInput(
                    txtNome.getText().trim(),
                    Medida.litros(volume),
                    null,
                    txtNotas.getText());

            CriarReceitaUseCase.CriarReceitaOutput output = criarReceitaUseCase.executar(input);

            atualizarLista();
            limparFormulario();
            mostrarSucesso(output.mensagem());

        } catch (NumberFormatException e) {
            mostrarErro("Volume deve ser um número válido");
        } catch (DominioException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void adicionarInsumo() {
        Receita selecionada = tabelaReceitas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarErro("Selecione uma receita primeiro");
            return;
        }

        if (!selecionada.isRascunho()) {
            mostrarErro("Só é possível editar receitas em RASCUNHO");
            return;
        }

        try {
            Insumo insumo = cmbInsumo.getValue();
            if (insumo == null) {
                mostrarErro("Selecione um insumo");
                return;
            }

            double qtd = Double.parseDouble(txtQuantidade.getText().trim());
            Medida quantidade = new Medida(qtd, insumo.getUnidadePadrao());

            selecionada.adicionarItem(insumo, quantidade);
            receitaRepo.salvar(selecionada);

            carregarDetalhesReceita(selecionada);
            txtQuantidade.clear();
            mostrarSucesso("Insumo adicionado!");

        } catch (NumberFormatException e) {
            mostrarErro("Quantidade deve ser um número válido");
        } catch (DominioException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void adicionarEtapa() {
        Receita selecionada = tabelaReceitas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarErro("Selecione uma receita primeiro");
            return;
        }

        if (!selecionada.isRascunho()) {
            mostrarErro("Só é possível editar receitas em RASCUNHO");
            return;
        }

        try {
            TipoEtapaProducao tipo = cmbEtapaTipo.getValue();
            if (tipo == null) {
                mostrarErro("Selecione um tipo de etapa");
                return;
            }

            selecionada.adicionarEtapa(tipo);
            receitaRepo.salvar(selecionada);

            carregarDetalhesReceita(selecionada);
            mostrarSucesso("Etapa adicionada!");

        } catch (DominioException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void ativarReceita() {
        Receita selecionada = tabelaReceitas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarErro("Selecione uma receita");
            return;
        }

        try {
            AtivarReceitaUseCase.AtivarReceitaOutput output = ativarReceitaUseCase.executar(selecionada.getId());

            atualizarLista();
            mostrarSucesso(output.mensagem());

        } catch (DominioException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void arquivarReceita() {
        Receita selecionada = tabelaReceitas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarErro("Selecione uma receita");
            return;
        }

        try {
            selecionada.arquivar();
            receitaRepo.salvar(selecionada);
            atualizarLista();
            mostrarSucesso("Receita arquivada!");

        } catch (DominioException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void carregarDetalhesReceita(Receita receita) {
        // Atualiza combo de insumos
        cmbInsumo.getItems().clear();
        cmbInsumo.getItems().addAll(insumoRepo.buscarTodos());

        // Carrega itens
        lstItensReceita.getItems().clear();
        for (ItemReceita item : receita.getItens()) {
            lstItensReceita.getItems().add(
                    item.getInsumo().getNome() + ": " + item.getQuantidade());
        }

        // Carrega etapas
        lstEtapasReceita.getItems().clear();
        for (EtapaProducao etapa : receita.getEtapas()) {
            lstEtapasReceita.getItems().add(etapa.toString());
        }
    }

    private void atualizarLista() {
        listaReceitas.clear();
        listaReceitas.addAll(receitaRepo.buscarTodos());
    }

    private void limparFormulario() {
        txtNome.clear();
        txtVolume.clear();
        txtNotas.clear();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Erro ao processar operação");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
