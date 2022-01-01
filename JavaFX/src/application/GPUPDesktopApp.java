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
    private static final String APP_FXML_NAME = "components/app/app.fxml";
    private static final String LOAD_FXML_NAME = "components/app/app.fxml";

    private Engine engine = new GPUPEngine(); // Model

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(APP_FXML_NAME);
        fxmlLoader.setLocation(url);
        BorderPane root = fxmlLoader.load(url.openStream());
        AppController appController = fxmlLoader.getController();
        appController.setModel(engine);

        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
