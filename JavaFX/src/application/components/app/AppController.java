package application.components.app;


import application.components.graphinfo.InfoController;
import application.general.Component;
import application.general.ComponentCreator;
import application.general.Controller;
import application.components.findPathes.FindPathesController;
import application.tools.AppTools;
import gpup.dto.SerialSetDTO;
import gpup.dto.TargetGraphDTO;
import gpup.dto.TargetInfoDTO;
import gpup.engine.Engine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import java.net.URL;
import java.util.Set;

import javafx.scene.control.ComboBox;

public class AppController implements Controller {
    private Engine engine;

    private GridPane welcomePage;

    private FindPathesController findPathesController;
    private InfoController graphInfoController;
    private static final String FINDPATHS_FXML_NAME = "../findPathes/pathes.fxml";
    private static final String INFO_FXML_NAME = "../graphinfo/graph-info.fxml";

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

    @FXML
    private ComboBox<String> ComboBoxActions;

    @FXML
    void buttonActionsClicked(ActionEvent event) {

    }

    @FXML
    void buttonInfoClicked(ActionEvent event) {

        URL url = getClass().getResource(INFO_FXML_NAME);
        Component infoComponent = ComponentCreator.createComponent(url);
        graphInfoController = (InfoController) infoComponent.getController();
        graphInfoController.setAppController(this);
        graphInfoController.fetchData();
        borderPaneApp.setCenter(infoComponent.getPane());
    }

    @FXML
    void buttonLoadFileClicked(ActionEvent event) {
        Button btn = (Button) event.getSource();
        if (!engine.isInitialized()) {
            loadFile(btn);
            // Add text : loaded successfully
        } else { // there is a loaded file
            if (AppTools.confirmationAlert("File Loading", "There is a loaded file in the system.", "Are you sure you want to load a new file, and overwrite the existing one?")) {
                loadFile(btn);
                borderPaneApp.setCenter(welcomePage);
            }
        }
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
            } catch (FileNotFoundException e) {
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
        ComboBoxActions.getItems().addAll("Find Path", "Find Circle", "What-if?");
    }

    @FXML
    void ActionChosen(ActionEvent event) {
        switch (ComboBoxActions.getSelectionModel().getSelectedItem()) {
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

    public void setWelcomePage(GridPane welcomePage) {
        this.welcomePage = welcomePage;
    }


    public void setModel(Engine engine) {
        this.engine = engine;
    }

    private void whatif() {
    }

    public ObservableList<TargetInfoDTO> getTargetsInfo() {
        List<TargetInfoDTO> list = engine.getTargetsInfo();
        return FXCollections.observableArrayList(list);
    }

    public TargetGraphDTO getGraphInfo() {
        return engine.getGraphInfo();
    }

    private void findCircle() {
    }

    private void findPath() {
        URL url = getClass().getResource(FINDPATHS_FXML_NAME);
        Component findPathComponent = ComponentCreator.createComponent(url);
        findPathComponent.getController().setAppController(this);
        borderPaneApp.setCenter(findPathComponent.getPane());
    }

    public Set<String> getTargetsList() {
        return engine.getTargetsNamesList();
    }

    public ObservableList<SerialSetDTO> getSerialSetInfo() {
        List<SerialSetDTO> list = engine.getSerialSetInfo();
        return FXCollections.observableArrayList(list);
    }

}