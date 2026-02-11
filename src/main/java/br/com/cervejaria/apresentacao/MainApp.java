package br.com.cervejaria.apresentacao;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Aplicação JavaFX principal do sistema de cervejaria.
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public class MainApp extends Application {

    private static final String APP_TITLE = "🍺 Sistema de Cervejaria Artesanal";
    private static final double WINDOW_WIDTH = 1000;
    private static final double WINDOW_HEIGHT = 700;

    private AppContext appContext;
    private BorderPane root;
    private TabPane tabPane;

    @Override
    public void init() {
        // Inicializa o contexto da aplicação
        appContext = AppContext.getInstance();
    }

    @Override
    public void start(Stage primaryStage) {
        // Cria o layout principal
        root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Cria o cabeçalho
        root.setTop(criarCabecalho());

        // Cria as abas
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Adiciona as telas
        tabPane.getTabs().addAll(
                criarAbaInsumos(),
                criarAbaReceitas(),
                criarAbaLotes(),
                criarAbaQualidade());

        root.setCenter(tabPane);

        // Cria o rodapé
        root.setBottom(criarRodape());

        // Configura a cena
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Configura o palco
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    private HBox criarCabecalho() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #2c3e50;");

        Label titulo = new Label(APP_TITLE);
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        header.getChildren().add(titulo);
        return header;
    }

    private HBox criarRodape() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10));
        footer.setStyle("-fx-background-color: #34495e;");

        Label info = new Label("Sistema Didático de Gestão de Cervejaria Artesanal v1.0");
        info.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 11px;");

        footer.getChildren().add(info);
        return footer;
    }

    private Tab criarAbaInsumos() {
        Tab tab = new Tab("📦 Insumos");
        InsumoController controller = new InsumoController(appContext);
        tab.setContent(controller.getView());
        return tab;
    }

    private Tab criarAbaReceitas() {
        Tab tab = new Tab("📝 Receitas");
        ReceitaController controller = new ReceitaController(appContext);
        tab.setContent(controller.getView());
        return tab;
    }

    private Tab criarAbaLotes() {
        Tab tab = new Tab("🏭 Lotes");
        LoteController controller = new LoteController(appContext);
        tab.setContent(controller.getView());
        return tab;
    }

    private Tab criarAbaQualidade() {
        Tab tab = new Tab("🔬 Qualidade");
        QualidadeController controller = new QualidadeController(appContext);
        tab.setContent(controller.getView());
        return tab;
    }

    @Override
    public void stop() {
        System.out.println("Aplicação encerrada.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
