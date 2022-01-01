package application;

import application.components.app.AppController;
import gpup.engine.Engine;
import gpup.engine.GPUPEngine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class GPUPDesktopApp extends Application {
    private static final String APP_FXML_NAME = "application/components/app/app.fxml";

    private Engine engine = new GPUPEngine(); // Model

    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene s = null;
        AppController appController = createAppComponent(s);

        primaryStage.setScene(s);

    }

    private AppController createAppComponent(Scene scene) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(APP_FXML_NAME);
        fxmlLoader.setLocation(url);
        BorderPane root = fxmlLoader.load(url.openStream());
        scene = new Scene(root, 600, 600);
        return fxmlLoader.getController();
    }
}
