package application.components.app;


import application.tools.AppTools;
import gpup.engine.Engine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;

import javafx.scene.control.ComboBox;

public class AppController {
    private Engine engine;

    @FXML
    private Button buttonLoadFile;

    @FXML
    private Button buttonInfo;

    @FXML
    private Button buttonActions;

    @FXML
    private Button buttonTask;

    public void setModel(Engine engine) {
        this.engine = engine;
    }

    @FXML
    private ComboBox<String> ComboBoxActions;

    @FXML
    void buttonActionsClicked(ActionEvent event) {

    }

    @FXML
    void buttonInfoClicked(ActionEvent event) {

    }

    @FXML
    void buttonLoadFileClicked(ActionEvent event) {
        Node node = (Node) event.getSource();
        File file = loadFile(node);
        try {
            engine.buildGraphFromXml(file);
        } catch (JAXBException e) {
            AppTools.warningAlert("File loading", "Something went wrong", "Probably with laoding the XML Schema");
        } catch (FileNotFoundException e) {
            AppTools.warningAlert("File loading", "The file isn't exist", "In order to continue please load again");
        }
    }

    private File loadFile(Node node) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Xml Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(node.getScene().getWindow());

        return selectedFile;
    }

    @FXML
    void buttonTaskClicked(ActionEvent event) {

    }

    @FXML
    public void initialize() {
        ComboBoxActions.getItems().removeAll(ComboBoxActions.getItems());
        ComboBoxActions.getItems().addAll("Find Path", "Find Circle", "What-if?");
    }

    @FXML
    void ActionChoosen(ActionEvent event) {

    }


}