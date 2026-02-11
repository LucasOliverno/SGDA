package br.com.cervejaria.apresentacao;

import br.com.cervejaria.dominio.comum.DominioException;
import br.com.cervejaria.dominio.comum.Medida;
import br.com.cervejaria.dominio.comum.UnidadeMedida;
import br.com.cervejaria.dominio.insumo.CategoriaInsumo;
import br.com.cervejaria.dominio.insumo.Insumo;
import br.com.cervejaria.infraestrutura.memoria.InsumoRepositorioMemoria;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * Controller para tela de gestão de insumos.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class InsumoController {

    private final AppContext appContext;
    private final InsumoRepositorioMemoria insumoRepo;
    private final VBox view;

    // Campos do formulário
    private TextField txtNome;
    private ComboBox<CategoriaInsumo> cmbCategoria;
    private ComboBox<UnidadeMedida> cmbUnidade;
    private TextField txtFabricante;

    // Tabela de insumos
    private TableView<Insumo> tabelaInsumos;
    private ObservableList<Insumo> listaInsumos;

    public InsumoController(AppContext appContext) {
        this.appContext = appContext;
        this.insumoRepo = appContext.getInsumoRepositorio();
        this.listaInsumos = FXCollections.observableArrayList();
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
        Label titulo = new Label("Gestão de Insumos");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Formulário
        GridPane formulario = criarFormulario();

        // Botões
        HBox botoesBox = criarBotoes();

        // Tabela
        tabelaInsumos = criarTabela();

        // Separador
        Separator separator = new Separator();

        container.getChildren().addAll(
                titulo,
                formulario,
                botoesBox,
                separator,
                new Label("Insumos Cadastrados:"),
                tabelaInsumos);

        VBox.setVgrow(tabelaInsumos, Priority.ALWAYS);

        return container;
    }

    private GridPane criarFormulario() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

        // Nome
        Label lblNome = new Label("Nome:");
        txtNome = new TextField();
        txtNome.setPromptText("Ex: Malte Pilsen");
        txtNome.setPrefWidth(200);

        // Categoria
        Label lblCategoria = new Label("Categoria:");
        cmbCategoria = new ComboBox<>();
        cmbCategoria.getItems().addAll(CategoriaInsumo.values());
        cmbCategoria.setPromptText("Selecione...");

        // Unidade
        Label lblUnidade = new Label("Unidade:");
        cmbUnidade = new ComboBox<>();
        cmbUnidade.getItems().addAll(UnidadeMedida.values());
        cmbUnidade.setPromptText("Selecione...");

        // Fabricante
        Label lblFabricante = new Label("Fabricante:");
        txtFabricante = new TextField();
        txtFabricante.setPromptText("(opcional)");

        // Layout
        grid.add(lblNome, 0, 0);
        grid.add(txtNome, 1, 0);
        grid.add(lblCategoria, 2, 0);
        grid.add(cmbCategoria, 3, 0);
        grid.add(lblUnidade, 0, 1);
        grid.add(cmbUnidade, 1, 1);
        grid.add(lblFabricante, 2, 1);
        grid.add(txtFabricante, 3, 1);

        return grid;
    }

    private HBox criarBotoes() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);

        Button btnCadastrar = new Button("✓ Cadastrar");
        btnCadastrar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnCadastrar.setOnAction(e -> cadastrarInsumo());

        Button btnLimpar = new Button("⟳ Limpar");
        btnLimpar.setOnAction(e -> limparFormulario());

        Button btnRemover = new Button("✗ Remover Selecionado");
        btnRemover.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnRemover.setOnAction(e -> removerSelecionado());

        box.getChildren().addAll(btnCadastrar, btnLimpar, btnRemover);
        return box;
    }

    @SuppressWarnings("unchecked")
    private TableView<Insumo> criarTabela() {
        TableView<Insumo> tabela = new TableView<>();
        tabela.setPlaceholder(new Label("Nenhum insumo cadastrado"));

        TableColumn<Insumo, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colNome.setPrefWidth(200);

        TableColumn<Insumo, CategoriaInsumo> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colCategoria.setPrefWidth(120);

        TableColumn<Insumo, UnidadeMedida> colUnidade = new TableColumn<>("Unidade");
        colUnidade.setCellValueFactory(new PropertyValueFactory<>("unidadePadrao"));
        colUnidade.setPrefWidth(100);

        TableColumn<Insumo, String> colFabricante = new TableColumn<>("Fabricante");
        colFabricante.setCellValueFactory(new PropertyValueFactory<>("fabricante"));
        colFabricante.setPrefWidth(150);

        tabela.getColumns().addAll(colNome, colCategoria, colUnidade, colFabricante);
        tabela.setItems(listaInsumos);

        return tabela;
    }

    private void cadastrarInsumo() {
        try {
            // Validações básicas de UI
            if (txtNome.getText().isBlank()) {
                mostrarErro("Nome é obrigatório");
                return;
            }
            if (cmbCategoria.getValue() == null) {
                mostrarErro("Selecione uma categoria");
                return;
            }
            if (cmbUnidade.getValue() == null) {
                mostrarErro("Selecione uma unidade");
                return;
            }

            // Cria o insumo
            Insumo insumo = new Insumo(
                    txtNome.getText().trim(),
                    cmbCategoria.getValue(),
                    cmbUnidade.getValue());

            if (!txtFabricante.getText().isBlank()) {
                insumo.setFabricante(txtFabricante.getText().trim());
            }

            // Salva
            insumoRepo.salvar(insumo);

            // Atualiza UI
            atualizarLista();
            limparFormulario();
            mostrarSucesso("Insumo '" + insumo.getNome() + "' cadastrado com sucesso!");

        } catch (DominioException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void removerSelecionado() {
        Insumo selecionado = tabelaInsumos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarErro("Selecione um insumo para remover");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Remoção");
        confirm.setHeaderText("Remover insumo?");
        confirm.setContentText("Deseja remover o insumo '" + selecionado.getNome() + "'?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                insumoRepo.remover(selecionado.getId());
                atualizarLista();
                mostrarSucesso("Insumo removido!");
            }
        });
    }

    private void atualizarLista() {
        listaInsumos.clear();
        listaInsumos.addAll(insumoRepo.buscarTodos());
    }

    private void limparFormulario() {
        txtNome.clear();
        cmbCategoria.setValue(null);
        cmbUnidade.setValue(null);
        txtFabricante.clear();
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
