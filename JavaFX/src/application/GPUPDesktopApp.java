package application;

import application.components.app.AppController;
import gpup.engine.Engine;
import gpup.engine.GPUPEngine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class GPUPDesktopApp extends Application {
    private static final String APP_FXML_NAME = "components/app/app.fxml";
    private static final String WELCOME_FXML_NAME = "components/welcome/welcome.fxml";

    private Engine engine = new GPUPEngine(); // Model
    private AppController appController;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(APP_FXML_NAME);
        fxmlLoader.setLocation(url);
        InputStream i = url.openStream();
        BorderPane root = fxmlLoader.load(i);
        appController = fxmlLoader.getController();
        appController.setModel(engine);

        addComponent(root, WELCOME_FXML_NAME);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setMinWidth(690);
        primaryStage.setMinHeight(450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addComponent(BorderPane root, String welcomeFxmlName) throws IOException {

        URL url = getClass().getResource(WELCOME_FXML_NAME);
        FXMLLoader newfxmlLoader = new FXMLLoader();
        newfxmlLoader.setLocation(url);
        GridPane welcomeComponent = newfxmlLoader.load(url.openStream());
        appController.setWelcomePage(welcomeComponent);
        root.setCenter(welcomeComponent);
    }
}
