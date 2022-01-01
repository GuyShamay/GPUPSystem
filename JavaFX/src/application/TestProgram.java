package application;

import gpup.component.targetgraph.TargetGraph;
import gpup.engine.GPUPEngine;
import gpup.jaxb.schema.generated.v2.GPUPDescriptor;
import gpup.jaxb.schema.parser.GPUPParser;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

public class TestProgram extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        GPUPEngine engine = new GPUPEngine();
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        URL url = getClass().getResource("body.fxml");
//        fxmlLoader.setLocation(url);
//        ScrollPane root = fxmlLoader.load(url.openStream());

//        Controller welcomeController = fxmlLoader.getController();
//        welcomeController.setModel(engine);
//        primaryStage.setTitle("hi");
        GPUPDescriptor gpupDescriptor = openfile(primaryStage);
        TargetGraph targetGraph = GPUPParser.parseTargetGraph(gpupDescriptor);
//
//        Scene scene = new Scene(root, 300, 275);
//        primaryStage.setScene(scene);
//        primaryStage.show();

    }

    public GPUPDescriptor openfile(Stage primaryStage) throws FileNotFoundException, JAXBException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Xml Files", "*.xml"),
                new FileChooser.ExtensionFilter("Image Files", ".png", ".jpg", "*.gif"));

        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        GPUPDescriptor gpupDescriptor = null;

        if (selectedFile != null) {
            final String PACKAGE_NAME = "gpup.jaxb.schema.generated.v2";
            InputStream inputStream = new FileInputStream(selectedFile);
            JAXBContext jc = JAXBContext.newInstance(PACKAGE_NAME);
            Unmarshaller u = jc.createUnmarshaller();
            gpupDescriptor = (GPUPDescriptor) u.unmarshal(inputStream);
            //    targetGraph = GPUPParser.parseTargetGraph(gpupDescriptor);
            String noam = "noam";
        }
        return gpupDescriptor;
    }
}

