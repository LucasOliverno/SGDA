package br.com.cervejaria.apresentacao;

import br.com.cervejaria.aplicacao.lote.*;
import br.com.cervejaria.dominio.comum.*;
import br.com.cervejaria.dominio.lote.*;
import br.com.cervejaria.dominio.receita.*;
import br.com.cervejaria.infraestrutura.memoria.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * Controller para tela de gestão de lotes de produção.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class LoteController {

    private final AppContext appContext;
    private final LoteRepositorioMemoria loteRepo;
    private final ReceitaRepositorioMemoria receitaRepo;
    private final CriarLoteUseCase criarLoteUseCase;
    private final IniciarProducaoUseCase iniciarProducaoUseCase;
    private final RegistrarEtapaUseCase registrarEtapaUseCase;
    private final DescartarLoteUseCase descartarLoteUseCase;

    private final VBox view;

    // Componentes
    private ComboBox<Receita> cmbReceitasAtivas;
    private TableView<Lote> tabelaLotes;
    private ObservableList<Lote> listaLotes;

    // Detalhes do lote
    private Label lblStatus;
    private Label lblReceita;
    private Label lblCodigo;
    private ListView<String> lstEtapas;
    private Button btnIniciarProducao;
    private Button btnAvancarEtapa;
    private Button btnDescartar;

    public LoteController(AppContext appContext) {
        this.appContext = appContext;
        this.loteRepo = appContext.getLoteRepositorio();
        this.receitaRepo = appContext.getReceitaRepositorio();
        this.criarLoteUseCase = appContext.getCriarLoteUseCase();
        this.iniciarProducaoUseCase = appContext.getIniciarProducaoUseCase();
        this.registrarEtapaUseCase = appContext.getRegistrarEtapaUseCase();
        this.descartarLoteUseCase = appContext.getDescartarLoteUseCase();
        this.listaLotes = FXCollections.observableArrayList();
        this.view = criarView();
        atualizarLista();
    }

    public VBox getView() {
        return view;
    }

    private VBox criarView() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));

        Label titulo = new Label("Gestão de Lotes de Produção");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox splitPane = new HBox(20);
        splitPane.setAlignment(Pos.TOP_LEFT);

        VBox ladoEsquerdo = criarLadoEsquerdo();
        ladoEsquerdo.setPrefWidth(400);

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

        // Criar lote
        TitledPane paneCriar = new TitledPane();
        paneCriar.setText("Criar Novo Lote");
        paneCriar.setCollapsible(false);

        VBox criarBox = new VBox(10);
        criarBox.setPadding(new Insets(10));

        cmbReceitasAtivas = new ComboBox<>();
        cmbReceitasAtivas.setPromptText("Selecione uma receita ativa");
        cmbReceitasAtivas.setPrefWidth(250);
        cmbReceitasAtivas.setCellFactory(lv -> new ListCell<Receita>() {
            @Override
            protected void updateItem(Receita item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome() + " (" + item.getVolumeProjetado() + ")");
            }
        });
        cmbReceitasAtivas.setButtonCell(new ListCell<Receita>() {
            @Override
            protected void updateItem(Receita item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome());
            }
        });

        Button btnCriarLote = new Button("✓ Criar Lote");
        btnCriarLote.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnCriarLote.setOnAction(e -> criarLote());

        Button btnAtualizar = new Button("⟳ Atualizar Lista");
        btnAtualizar.setOnAction(e -> {
            atualizarReceitas();
            atualizarLista();
        });

        HBox botoesBox = new HBox(10, btnCriarLote, btnAtualizar);
        criarBox.getChildren().addAll(
                new Label("Receita:"),
                cmbReceitasAtivas,
                botoesBox);
        paneCriar.setContent(criarBox);

        // Tabela de lotes
        TitledPane paneTabela = new TitledPane();
        paneTabela.setText("Lotes de Produção");
        paneTabela.setCollapsible(false);
        VBox.setVgrow(paneTabela, Priority.ALWAYS);

        tabelaLotes = criarTabelaLotes();
        VBox tableBox = new VBox(tabelaLotes);
        VBox.setVgrow(tabelaLotes, Priority.ALWAYS);
        paneTabela.setContent(tableBox);

        box.getChildren().addAll(paneCriar, paneTabela);
        VBox.setVgrow(paneTabela, Priority.ALWAYS);

        atualizarReceitas();
        return box;
    }

    private VBox criarLadoDireito() {
        VBox box = new VBox(10);

        TitledPane paneDetalhes = new TitledPane();
        paneDetalhes.setText("Detalhes do Lote Selecionado");
        paneDetalhes.setCollapsible(false);
        VBox.setVgrow(paneDetalhes, Priority.ALWAYS);

        VBox detalhesBox = new VBox(10);
        detalhesBox.setPadding(new Insets(10));

        // Informações
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        infoGrid.setVgap(5);

        lblCodigo = new Label("-");
        lblCodigo.setStyle("-fx-font-weight: bold;");
        lblReceita = new Label("-");
        lblStatus = new Label("-");
        lblStatus.setStyle("-fx-font-weight: bold;");

        infoGrid.add(new Label("Código:"), 0, 0);
        infoGrid.add(lblCodigo, 1, 0);
        infoGrid.add(new Label("Receita:"), 0, 1);
        infoGrid.add(lblReceita, 1, 1);
        infoGrid.add(new Label("Status:"), 0, 2);
        infoGrid.add(lblStatus, 1, 2);

        // Lista de etapas
        lstEtapas = new ListView<>();
        lstEtapas.setPrefHeight(200);
        lstEtapas.setPlaceholder(new Label("Selecione um lote"));

        // Botões de ação
        btnIniciarProducao = new Button("▶ Iniciar Produção");
        btnIniciarProducao.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        btnIniciarProducao.setOnAction(e -> iniciarProducao());
        btnIniciarProducao.setDisable(true);

        btnAvancarEtapa = new Button("→ Concluir Etapa Atual");
        btnAvancarEtapa.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        btnAvancarEtapa.setOnAction(e -> avancarEtapa());
        btnAvancarEtapa.setDisable(true);

        Button btnAvancarStatus = new Button("↑ Avançar Status");
        btnAvancarStatus.setOnAction(e -> avancarStatus());

        btnDescartar = new Button("✗ Descartar Lote");
        btnDescartar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnDescartar.setOnAction(e -> descartarLote());
        btnDescartar.setDisable(true);

        HBox acoesBox1 = new HBox(10, btnIniciarProducao, btnAvancarEtapa);
        HBox acoesBox2 = new HBox(10, btnAvancarStatus, btnDescartar);

        detalhesBox.getChildren().addAll(
                infoGrid,
                new Separator(),
                new Label("Etapas de Produção:"),
                lstEtapas,
                new Separator(),
                new Label("Ações:"),
                acoesBox1,
                acoesBox2);

        paneDetalhes.setContent(detalhesBox);
        box.getChildren().add(paneDetalhes);
        VBox.setVgrow(paneDetalhes, Priority.ALWAYS);

        return box;
    }

    @SuppressWarnings("unchecked")
    private TableView<Lote> criarTabelaLotes() {
        TableView<Lote> tabela = new TableView<>();
        tabela.setPlaceholder(new Label("Nenhum lote cadastrado"));

        TableColumn<Lote, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colCodigo.setPrefWidth(100);

        TableColumn<Lote, StatusLote> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(120);

        tabela.getColumns().addAll(colCodigo, colStatus);
        tabela.setItems(listaLotes);

        tabela.getSelectionModel().selectedItemProperty().addListener((obs, old, novo) -> {
            if (novo != null) {
                carregarDetalhesLote(novo);
            }
        });

        return tabela;
    }

    private void criarLote() {
        Receita receita = cmbReceitasAtivas.getValue();
        if (receita == null) {
            mostrarErro("Selecione uma receita");
            return;
        }

        try {
            CriarLoteUseCase.CriarLoteOutput output = criarLoteUseCase.executar(receita.getId());
            atualizarLista();
            mostrarSucesso(output.mensagem());

        } catch (DominioException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void iniciarProducao() {
        Lote lote = tabelaLotes.getSelectionModel().getSelectedItem();
        if (lote == null)
            return;

        try {
            IniciarProducaoUseCase.IniciarProducaoOutput output = iniciarProducaoUseCase.executar(lote.getId());

            atualizarLista();
            carregarDetalhesLote(loteRepo.buscarPorId(lote.getId()).orElseThrow());
            mostrarSucesso(output.mensagem());

        } catch (DominioException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void avancarEtapa() {
        Lote lote = tabelaLotes.getSelectionModel().getSelectedItem();
        if (lote == null)
            return;

        try {
            RegistrarEtapaUseCase.RegistrarEtapaInput input = new RegistrarEtapaUseCase.RegistrarEtapaInput(
                    lote.getId(), null, "Etapa concluída", false);

            RegistrarEtapaUseCase.RegistrarEtapaOutput output = registrarEtapaUseCase.executar(input);

            carregarDetalhesLote(loteRepo.buscarPorId(lote.getId()).orElseThrow());
            mostrarSucesso(output.mensagem());

        } catch (DominioException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void avancarStatus() {
        Lote lote = tabelaLotes.getSelectionModel().getSelectedItem();
        if (lote == null)
            return;

        try {
            StatusLote statusAtual = lote.getStatus();

            switch (statusAtual) {
                case EM_BRASSAGEM -> lote.iniciarFermentacao();
                case FERMENTANDO -> lote.iniciarMaturacao();
                case MATURANDO -> lote.marcarProntoParaEnvase();
                case PRONTO_PARA_ENVASE -> lote.envasar();
                default -> {
                    mostrarErro("Não é possível avançar deste status");
                    return;
                }
            }

            loteRepo.salvar(lote);
            atualizarLista();
            carregarDetalhesLote(lote);
            mostrarSucesso("Status atualizado para: " + lote.getStatus());

        } catch (DominioException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void descartarLote() {
        Lote lote = tabelaLotes.getSelectionModel().getSelectedItem();
        if (lote == null)
            return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Descartar Lote");
        dialog.setHeaderText("Informe a justificativa para descarte:");
        dialog.setContentText("Justificativa:");

        dialog.showAndWait().ifPresent(justificativa -> {
            try {
                DescartarLoteUseCase.DescartarLoteInput input = new DescartarLoteUseCase.DescartarLoteInput(
                        lote.getId(), justificativa);

                DescartarLoteUseCase.DescartarLoteOutput output = descartarLoteUseCase.executar(input);

                atualizarLista();
                carregarDetalhesLote(loteRepo.buscarPorId(lote.getId()).orElseThrow());
                mostrarSucesso(output.mensagem());

            } catch (DominioException e) {
                mostrarErro(e.getMessage());
            }
        });
    }

    private void carregarDetalhesLote(Lote lote) {
        lblCodigo.setText(lote.getCodigo());
        lblReceita.setText(lote.getReceita().getNome());
        lblStatus.setText(lote.getStatus().getDescricao());

        // Cor do status
        String corStatus = switch (lote.getStatus()) {
            case PLANEJADO -> "#7f8c8d";
            case EM_BRASSAGEM, FERMENTANDO, MATURANDO -> "#f39c12";
            case PRONTO_PARA_ENVASE -> "#3498db";
            case ENVASADO -> "#27ae60";
            case DESCARTADO -> "#e74c3c";
        };
        lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: " + corStatus + ";");

        // Etapas
        lstEtapas.getItems().clear();
        for (EtapaProducaoExecutada etapa : lote.getEtapasExecutadas()) {
            String statusIcon = switch (etapa.getStatus()) {
                case PENDENTE -> "⬜";
                case EM_ANDAMENTO -> "🔄";
                case CONCLUIDA -> "✅";
                case COM_PROBLEMAS -> "⚠️";
            };
            lstEtapas.getItems().add(statusIcon + " " + etapa.getModelo().getTipo().getNome() +
                    " [" + etapa.getStatus() + "]");
        }

        // Habilitar/desabilitar botões
        boolean isFinal = lote.isEstadoFinal();
        btnIniciarProducao.setDisable(lote.getStatus() != StatusLote.PLANEJADO);
        btnAvancarEtapa.setDisable(isFinal || lote.getEtapaEmAndamento().isEmpty());
        btnDescartar.setDisable(isFinal);
    }

    private void atualizarReceitas() {
        cmbReceitasAtivas.getItems().clear();
        cmbReceitasAtivas.getItems().addAll(receitaRepo.buscarAtivas());
    }

    private void atualizarLista() {
        listaLotes.clear();
        listaLotes.addAll(loteRepo.buscarTodos());
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
