package br.com.cervejaria.apresentacao;

import br.com.cervejaria.aplicacao.lote.*;
import br.com.cervejaria.dominio.comum.*;
import br.com.cervejaria.dominio.lote.*;
import br.com.cervejaria.dominio.qualidade.*;
import br.com.cervejaria.infraestrutura.memoria.*;
import br.com.cervejaria.servico.qualidade.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * Controller para tela de avaliação de qualidade.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class QualidadeController {

    private final AppContext appContext;
    private final LoteRepositorioMemoria loteRepo;
    private final RegistrarDensidadeUseCase registrarDensidadeUseCase;
    private final RegistrarAvaliacaoUseCase registrarAvaliacaoUseCase;
    private final ServicoConformidadeEstilo servicoConformidade;

    private final VBox view;

    // Componentes
    private ComboBox<Lote> cmbLotes;
    private Label lblInfoLote;

    // Densidade
    private TextField txtOG;
    private TextField txtFG;
    private Label lblABV;

    // Avaliação sensorial
    private TextField txtAvaliador;
    private Slider sldAparencia;
    private Slider sldAroma;
    private Slider sldSabor;
    private Slider sldCorpo;
    private ComboBox<ParecerQualidade> cmbParecer;
    private TextArea txtObservacoes;

    // Resultado
    private TextArea txtRelatorio;

    public QualidadeController(AppContext appContext) {
        this.appContext = appContext;
        this.loteRepo = appContext.getLoteRepositorio();
        this.registrarDensidadeUseCase = appContext.getRegistrarDensidadeUseCase();
        this.registrarAvaliacaoUseCase = appContext.getRegistrarAvaliacaoUseCase();
        this.servicoConformidade = appContext.getServicoConformidade();
        this.view = criarView();
    }

    public VBox getView() {
        return view;
    }

    private VBox criarView() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));

        Label titulo = new Label("Avaliação de Qualidade");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Seleção de lote
        HBox selecaoBox = criarSelecaoLote();

        // Split horizontal
        HBox splitPane = new HBox(20);
        splitPane.setAlignment(Pos.TOP_LEFT);

        VBox ladoEsquerdo = criarLadoEsquerdo();
        ladoEsquerdo.setPrefWidth(400);

        VBox ladoDireito = criarLadoDireito();
        ladoDireito.setPrefWidth(450);
        HBox.setHgrow(ladoDireito, Priority.ALWAYS);

        splitPane.getChildren().addAll(ladoEsquerdo, ladoDireito);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        container.getChildren().addAll(titulo, selecaoBox, splitPane);
        return container;
    }

    private HBox criarSelecaoLote() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

        cmbLotes = new ComboBox<>();
        cmbLotes.setPromptText("Selecione um lote para avaliar");
        cmbLotes.setPrefWidth(300);
        cmbLotes.setCellFactory(lv -> new ListCell<Lote>() {
            @Override
            protected void updateItem(Lote item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : item.getCodigo() + " - " + item.getReceita().getNome() +
                                " [" + item.getStatus() + "]");
            }
        });
        cmbLotes.setButtonCell(new ListCell<Lote>() {
            @Override
            protected void updateItem(Lote item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getCodigo() + " - " + item.getReceita().getNome());
            }
        });
        cmbLotes.setOnAction(e -> carregarLote());

        Button btnAtualizar = new Button("⟳ Atualizar");
        btnAtualizar.setOnAction(e -> atualizarLotes());

        lblInfoLote = new Label("");
        lblInfoLote.setStyle("-fx-font-weight: bold;");

        box.getChildren().addAll(new Label("Lote:"), cmbLotes, btnAtualizar, lblInfoLote);
        atualizarLotes();

        return box;
    }

    private VBox criarLadoEsquerdo() {
        VBox box = new VBox(10);

        // Densidade
        TitledPane paneDensidade = new TitledPane();
        paneDensidade.setText("Registrar Densidades");
        paneDensidade.setCollapsible(false);

        GridPane densidadeGrid = new GridPane();
        densidadeGrid.setHgap(10);
        densidadeGrid.setVgap(10);
        densidadeGrid.setPadding(new Insets(10));

        txtOG = new TextField();
        txtOG.setPromptText("Ex: 1.052");
        txtOG.setPrefWidth(100);

        txtFG = new TextField();
        txtFG.setPromptText("Ex: 1.012");
        txtFG.setPrefWidth(100);

        lblABV = new Label("-");
        lblABV.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Button btnRegistrarDensidades = new Button("✓ Registrar");
        btnRegistrarDensidades.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnRegistrarDensidades.setOnAction(e -> registrarDensidades());

        densidadeGrid.add(new Label("OG (SG):"), 0, 0);
        densidadeGrid.add(txtOG, 1, 0);
        densidadeGrid.add(new Label("FG (SG):"), 0, 1);
        densidadeGrid.add(txtFG, 1, 1);
        densidadeGrid.add(new Label("ABV Calc:"), 0, 2);
        densidadeGrid.add(lblABV, 1, 2);
        densidadeGrid.add(btnRegistrarDensidades, 1, 3);

        paneDensidade.setContent(densidadeGrid);

        // Avaliação sensorial
        TitledPane paneAvaliacao = new TitledPane();
        paneAvaliacao.setText("Avaliação Sensorial");
        paneAvaliacao.setCollapsible(false);

        VBox avaliacaoBox = new VBox(10);
        avaliacaoBox.setPadding(new Insets(10));

        txtAvaliador = new TextField();
        txtAvaliador.setPromptText("Nome do avaliador");

        sldAparencia = criarSlider("Aparência");
        sldAroma = criarSlider("Aroma");
        sldSabor = criarSlider("Sabor");
        sldCorpo = criarSlider("Corpo");

        cmbParecer = new ComboBox<>();
        cmbParecer.getItems().addAll(ParecerQualidade.values());
        cmbParecer.setPromptText("Parecer final");

        txtObservacoes = new TextArea();
        txtObservacoes.setPromptText("Observações...");
        txtObservacoes.setPrefRowCount(2);

        Button btnRegistrarAvaliacao = new Button("✓ Registrar Avaliação");
        btnRegistrarAvaliacao.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        btnRegistrarAvaliacao.setOnAction(e -> registrarAvaliacao());

        avaliacaoBox.getChildren().addAll(
                new HBox(10, new Label("Avaliador:"), txtAvaliador),
                criarSliderBox("Aparência:", sldAparencia),
                criarSliderBox("Aroma:", sldAroma),
                criarSliderBox("Sabor:", sldSabor),
                criarSliderBox("Corpo:", sldCorpo),
                new HBox(10, new Label("Parecer:"), cmbParecer),
                new Label("Observações:"),
                txtObservacoes,
                btnRegistrarAvaliacao);

        paneAvaliacao.setContent(avaliacaoBox);

        box.getChildren().addAll(paneDensidade, paneAvaliacao);
        return box;
    }

    private Slider criarSlider(String nome) {
        Slider slider = new Slider(1, 10, 5);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(1);
        slider.setSnapToTicks(true);
        return slider;
    }

    private HBox criarSliderBox(String label, Slider slider) {
        Label lbl = new Label(label);
        lbl.setPrefWidth(80);
        Label valorLbl = new Label("5");
        valorLbl.setPrefWidth(30);
        slider.valueProperty().addListener((obs, old, novo) -> valorLbl.setText(String.valueOf(novo.intValue())));
        HBox box = new HBox(10, lbl, slider, valorLbl);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private VBox criarLadoDireito() {
        VBox box = new VBox(10);

        TitledPane paneRelatorio = new TitledPane();
        paneRelatorio.setText("Relatório de Conformidade");
        paneRelatorio.setCollapsible(false);
        VBox.setVgrow(paneRelatorio, Priority.ALWAYS);

        VBox relatorioBox = new VBox(10);
        relatorioBox.setPadding(new Insets(10));

        txtRelatorio = new TextArea();
        txtRelatorio.setEditable(false);
        txtRelatorio.setPrefRowCount(20);
        txtRelatorio.setStyle("-fx-font-family: monospace;");
        VBox.setVgrow(txtRelatorio, Priority.ALWAYS);

        Button btnGerarRelatorio = new Button("📊 Gerar Relatório");
        btnGerarRelatorio.setOnAction(e -> gerarRelatorio());

        relatorioBox.getChildren().addAll(btnGerarRelatorio, txtRelatorio);
        VBox.setVgrow(txtRelatorio, Priority.ALWAYS);

        paneRelatorio.setContent(relatorioBox);
        box.getChildren().add(paneRelatorio);
        VBox.setVgrow(paneRelatorio, Priority.ALWAYS);

        return box;
    }

    private void registrarDensidades() {
        Lote lote = cmbLotes.getValue();
        if (lote == null) {
            mostrarErro("Selecione um lote");
            return;
        }

        try {
            // Registra OG se informado
            if (!txtOG.getText().isBlank()) {
                double og = Double.parseDouble(txtOG.getText().trim());
                RegistrarDensidadeUseCase.RegistrarDensidadeInput input = new RegistrarDensidadeUseCase.RegistrarDensidadeInput(
                        lote.getId(), TipoDensidade.OG, og, EscalaDensidade.SG);
                registrarDensidadeUseCase.executar(input);
            }

            // Registra FG se informado
            if (!txtFG.getText().isBlank()) {
                double fg = Double.parseDouble(txtFG.getText().trim());
                RegistrarDensidadeUseCase.RegistrarDensidadeInput input = new RegistrarDensidadeUseCase.RegistrarDensidadeInput(
                        lote.getId(), TipoDensidade.FG, fg, EscalaDensidade.SG);
                registrarDensidadeUseCase.executar(input);
            }

            // Atualiza ABV
            lote = loteRepo.buscarPorId(lote.getId()).orElseThrow();
            Double abv = lote.calcularABV();
            if (abv != null) {
                lblABV.setText(String.format("%.1f%%", abv));
            }

            mostrarSucesso("Densidades registradas!");

        } catch (NumberFormatException e) {
            mostrarErro("Valores devem ser numéricos (ex: 1.052)");
        } catch (DominioException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void registrarAvaliacao() {
        Lote lote = cmbLotes.getValue();
        if (lote == null) {
            mostrarErro("Selecione um lote");
            return;
        }

        if (txtAvaliador.getText().isBlank()) {
            mostrarErro("Informe o nome do avaliador");
            return;
        }

        if (cmbParecer.getValue() == null) {
            mostrarErro("Selecione um parecer");
            return;
        }

        try {
            RegistrarAvaliacaoUseCase.AvaliacaoSensorialInput input = new RegistrarAvaliacaoUseCase.AvaliacaoSensorialInput(
                    lote.getId(),
                    txtAvaliador.getText().trim(),
                    (int) sldAparencia.getValue(),
                    (int) sldAroma.getValue(),
                    (int) sldSabor.getValue(),
                    (int) sldCorpo.getValue(),
                    cmbParecer.getValue(),
                    txtObservacoes.getText());

            RegistrarAvaliacaoUseCase.RegistrarAvaliacaoOutput output = registrarAvaliacaoUseCase
                    .executarSensorial(input);

            mostrarSucesso(output.mensagem());
            carregarLote();

        } catch (DominioException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void gerarRelatorio() {
        Lote lote = cmbLotes.getValue();
        if (lote == null) {
            mostrarErro("Selecione um lote");
            return;
        }

        try {
            // Recarrega o lote
            lote = loteRepo.buscarPorId(lote.getId()).orElseThrow();

            String relatorio = servicoConformidade.gerarRelatorioConformidade(lote);
            txtRelatorio.setText(relatorio);

        } catch (DominioException e) {
            txtRelatorio.setText("Erro ao gerar relatório:\n" + e.getMessage());
        }
    }

    private void carregarLote() {
        Lote lote = cmbLotes.getValue();
        if (lote == null)
            return;

        // Recarrega
        lote = loteRepo.buscarPorId(lote.getId()).orElseThrow();

        lblInfoLote.setText("Status: " + lote.getStatus().getDescricao());

        // Preenche densidades se existirem
        if (lote.getOg() != null) {
            txtOG.setText(String.format("%.3f", lote.getOg().getValorEmSG()));
        }
        if (lote.getFg() != null) {
            txtFG.setText(String.format("%.3f", lote.getFg().getValorEmSG()));
        }

        Double abv = lote.calcularABV();
        if (abv != null) {
            lblABV.setText(String.format("%.1f%%", abv));
        } else {
            lblABV.setText("-");
        }
    }

    private void atualizarLotes() {
        cmbLotes.getItems().clear();
        cmbLotes.getItems().addAll(loteRepo.buscarTodos());
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
