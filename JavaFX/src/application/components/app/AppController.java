package application.components.app;


import application.Component;
import application.ComponentCreator;
import application.ComponentType;
import application.Controller;
import application.components.findPathes.FindPathesController;
import application.tools.AppTools;
import gpup.engine.Engine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;

import javafx.scene.control.ComboBox;

public class AppController implements Controller {
    private Engine engine;
    private FindPathesController findPathesController;
    private static final String FINDPATHES_FXML_NAME = "../findPathes/pathes.fxml";

    @FXML
    private BorderPane borderPaneApp;

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
        Button btn = (Button) event.getSource();
        loadFile(btn);


    }

    private void loadFile(Button btn) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Xml Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(btn.getScene().getWindow());

        if (selectedFile != null) {
            try {
                engine.buildGraphFromXml(selectedFile);
            } catch (JAXBException e) {
                AppTools.warningAlert("File loading", "Something went wrong", "Probably with laoding the XML Schema");
            }catch (FileNotFoundException e) {
                AppTools.warningAlert("File loading", "The file isn't exist", "In order to continue please load again");
            }
        }
    }

    @FXML
    void buttonTaskClicked(ActionEvent event) {

    }

    @FXML
    public void initialize() {
        ComboBoxActions.getItems().removeAll(ComboBoxActions.getItems());
        ComboBoxActions.getItems().addAll("Find Path","Find Circle","What-if?");
    }

    @FXML
    void ActionChoosen(ActionEvent event) {
        switch (ComboBoxActions.getSelectionModel().getSelectedItem()){
            case "Find Path":
                findPath();
                break;
            case "Find Circle":
                findCircle();
                break;
            case "What-if?":
                whatif();
                break;
        }
    }

    private void whatif() {
    }

    private void findCircle() {
    }

    private void findPath() {
            URL url = getClass().getResource(FINDPATHES_FXML_NAME);
            Component findPathComponent = ComponentCreator.createComponent(url);
            findPathComponent.getController().setAppController(this);
            borderPaneApp.setCenter(findPathComponent.getPane());
    }

    public List<String> getTargetsList(){
        return engine.getTargetsNamesList();
    }

    @Override
    public void setAppController(Controller appController) {

    }
}