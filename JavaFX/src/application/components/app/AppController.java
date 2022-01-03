package application.components.app;


import application.components.findPathes.PathsController;
import application.components.task.config.TaskConfigController;
import application.components.welcome.WelcomeController;
import application.general.Component;
import application.general.ComponentCreator;
import application.general.Controller;
import application.tools.AppTools;
import component.target.TargetsRelationType;
import dto.PathsDTO;
import application.components.graphinfo.InfoController;
import dto.SerialSetDTO;
import dto.TargetGraphDTO;
import dto.TargetInfoDTO;
import engine.Engine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import java.net.URL;
import java.util.Set;

import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class AppController implements Controller {
    //--------------------------------------------------------------------------
    //Controllers & Modal:
    private Engine engine;
    private PathsController findPathesController;
    private InfoController graphInfoController;

    //--------------------------------------------------------------------------
    //Vars & Const:
    private AnchorPane welcomePage;
    private WelcomeController welcomeController;
    private static final String FIND_PATHS_FXML_NAME = "../findPathes/pathes.fxml";
    private static final String INFO_FXML_NAME = "../graphinfo/graph-info.fxml";
    private static final String TASK_CONFIG_FXML_NAME = "../task/config/task-config.fxml";

    //--------------------------------------------------------------------------
    //FXML Controls:

    @FXML
    private BorderPane borderPaneApp;

    @FXML
    private Button buttonLoadFile;

    @FXML
    private Button buttonInfo;

    @FXML
    private Button buttonTask;

    @FXML
    private ComboBox<String> comboBoxActions;

    @FXML
    public void initialize() {
        comboBoxActions.getItems().removeAll(comboBoxActions.getItems());
        comboBoxActions.getItems().addAll("Find Path", "Find Circle", "What-if?");

        // Disable all buttons except Load
        buttonInfo.setDisable(true);
        buttonTask.setDisable(true);
        comboBoxActions.setDisable(true);
    }

    public void setWelcomePage(AnchorPane welcomePage) {
        this.welcomePage = welcomePage;
    }

    public void setModel(Engine engine) {
        this.engine = engine;
    }

    public void setWelcomeController(WelcomeController welcomeController) {
        this.welcomeController = welcomeController;
    }
    //--------------------------------------------------------------------------
    //Show Information

    @FXML
    void buttonInfoClicked(ActionEvent event) {

        URL url = getClass().getResource(INFO_FXML_NAME);
        Component infoComponent = ComponentCreator.createComponent(url);
        graphInfoController = (InfoController) infoComponent.getController();
        graphInfoController.setAppController(this);
        graphInfoController.fetchData();
        borderPaneApp.setCenter(infoComponent.getPane());
    }

    public ObservableList<SerialSetDTO> getSerialSetInfo() {
        List<SerialSetDTO> list = engine.getSerialSetInfo();
        return FXCollections.observableArrayList(list);
    }

    public ObservableList<TargetInfoDTO> getTargetsInfo() {
        List<TargetInfoDTO> list = engine.getTargetsInfo();
        return FXCollections.observableArrayList(list);
    }
    //--------------------------------------------------------------------------
    //Load File

    @FXML
    void buttonLoadFileClicked(ActionEvent event) {
        Button btn = (Button) event.getSource();
        if (!engine.isInitialized()) {
            loadFile(btn);

            buttonInfo.setDisable(false);
            buttonTask.setDisable(false);
            comboBoxActions.setDisable(false);
            welcomeController.loadSuccess();
        } else { // there is a loaded file
            if (AppTools.confirmationAlert("File Loading", "There is a loaded file in the system.", "Are you sure you want to load a new file, and overwrite the existing one?")) {
                loadFile(btn);
                borderPaneApp.setCenter(welcomePage);
            }
        }
    }

    private void loadFile(Button btn) {
        /*FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Xml Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(btn.getScene().getWindow());*/
        File selectedFile = new File("C:\\Users\\guysh\\Downloads\\ex2-big.xml");
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
    //--------------------------------------------------------------------------
    // Run Task

    @FXML
    void buttonTaskClicked(ActionEvent event) {
        Stage stage = new Stage();

        URL url = getClass().getResource(TASK_CONFIG_FXML_NAME);
        Component taskConfigComponent = ComponentCreator.createComponent(url);
        TaskConfigController t = (TaskConfigController) taskConfigComponent.getController();
        t.setAppController(this);
        t.fetchData();
        Scene scene = new Scene(taskConfigComponent.getPane(), 404,504);
        stage.setScene(scene);
        stage.show();
    }
    //--------------------------------------------------------------------------
    // Actions: paths, circle, what-if

    @FXML
    void buttonActionsClicked(ActionEvent event) {

    }

    @FXML
    void ActionChosen(ActionEvent event) {
        switch (comboBoxActions.getSelectionModel().getSelectedItem()) {
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
//        comboBoxActions.getSelectionModel().clearSelection();
    }

    private void whatif() {
    }

    private void findCircle() {
    }

    private void findPath() {
        URL url = getClass().getResource(FIND_PATHS_FXML_NAME);
        Component findPathComponent = ComponentCreator.createComponent(url);
        findPathComponent.getController().setAppController(this);
        findPathComponent.getController().Init();
        borderPaneApp.setCenter(findPathComponent.getPane());
    }

    public TargetGraphDTO getGraphInfo() {
        return engine.getGraphInfo();
    }

    public PathsDTO getFoundPaths(String src, String dest, TargetsRelationType type) {
        return engine.findPaths(src, dest, type);
    }

    public Set<String> getTargetsListByName() {
        return engine.getTargetsNamesList();
    }

}