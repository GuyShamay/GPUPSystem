package application.components.app;


import application.components.findPathes.PathsController;
import application.components.task.TaskController;
import application.components.welcome.WelcomeController;
import application.components.graphinfo.InfoController;
import application.general.Component;
import application.general.ComponentCreator;
import application.general.Controller;
import application.tools.AppTools;
import component.target.TargetsRelationType;
import component.task.RunTask;
import component.task.config.TaskConfig;
import dto.*;
import engine.Engine;
import exception.ElementExistException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class AppController implements Controller {
    //--------------------------------------------------------------------------
    //Controllers & Modal:
    private Engine engine;
    private PathsController findPathesController;
    private InfoController graphInfoController;
    private TaskController taskController;

    //--------------------------------------------------------------------------
    //Vars & Const:
    private AnchorPane welcomePage;
    private WelcomeController welcomeController;
    private static final String FIND_PATHS_FXML_NAME = "../findPathes/pathes.fxml";
    private static final String INFO_FXML_NAME = "../graphinfo/graph-info.fxml";
    private static final String FINDCIRCUIT_FXML_NAME = "../findCircuit/findCircuit.fxml";
    private static final String WHATIF_FXML_NAME = "../whatIf/whatIf.fxml";
    private static final String TASK_FXML_NAME = "../task/task2.fxml";

    //--------------------------------------------------------------------------
    //FXML Controls:

    @FXML
    private BorderPane borderPaneApp;
    @FXML
    private Button buttonInfo;
    @FXML
    private Button buttonTask;
    @FXML
    private ComboBox<String> comboBoxActions;
    @FXML
    private ComboBox<?> comboBoxThemes;
    @FXML
    private CheckBox checkBoxAnimations;

    @FXML
    public void initialize() {
        comboBoxActions.getItems().addAll("Find Path", "Find Circuit", "What-if?");
        comboBoxActions.valueProperty().addListener(((observable, oldValue, newValue) -> {
            comboBoxActionChosen(newValue);
        }));
        // Disable all buttons except Load
        setDisableControls(true);

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
        if (list != null) {
            return FXCollections.observableArrayList(list);
        }
        return null;
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
            if (loadFile(btn)) {
                setDisableControls(false);
                welcomeController.loadSuccess();
            }
        } else { // there is a loaded file
            if (AppTools.confirmationAlert("File Loading", "There is a loaded file in the system.", "Are you sure you want to load a new file, and overwrite the existing one?")) {
                loadFile(btn);
                borderPaneApp.setCenter(welcomePage);
            }
        }
    }

    private boolean loadFile(Button btn) {
        /*
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Xml Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(btn.getScene().getWindow());
        */
        File selectedFile = new File("C:\\Users\\guysh\\Downloads\\ex2-big.xml");
        if (selectedFile != null) {
            try {
                engine.buildGraphFromXml(selectedFile);
                return true;
            } catch (JAXBException e) {
                AppTools.warningAlert("File loading", "Something went wrong", "Probably with laoding the XML Schema");
            } catch (FileNotFoundException e) {
                AppTools.warningAlert("File loading", "The file isn't exist", "In order to continue please load again");
            } catch (ElementExistException | NoSuchElementException e) {
                AppTools.warningAlert("File loading", e.getMessage(), "In order to continue please load again");
            }
        }
        return false;
    }

    private void setDisableControls(boolean isDisable) {
        buttonInfo.setDisable(isDisable);
        buttonTask.setDisable(isDisable);
        comboBoxActions.setDisable(isDisable);
    }

    //--------------------------------------------------------------------------
    // Run Task
    @FXML
    void buttonTaskClicked(ActionEvent event) {
        URL url = getClass().getResource(TASK_FXML_NAME);
        Component taskComponent = ComponentCreator.createComponent(url);
        taskController = (TaskController) taskComponent.getController();
        taskController.setAppController(this);
        borderPaneApp.setCenter(taskComponent.getPane());
    }

    public int getMaxParallelism() {
        return engine.getMaxParallelism();
    }

    public void incParallelism(Integer newVal) {
        engine.increaseThreadsNum(newVal);
    }

    public boolean isCircuit() {
        return engine.isCircuit();
    }

    //--------------------------------------------------------------------------

    // Actions: paths, circle, what-if
    private void comboBoxActionChosen(String newValue) {
        String path = "";
        switch (newValue) {
            case "Find Path":
                path = FIND_PATHS_FXML_NAME;
                break;
            case "Find Circuit":
                path = FINDCIRCUIT_FXML_NAME;
                break;
            case "What-if?":
                path = WHATIF_FXML_NAME;
                break;
        }
        URL url = getClass().getResource(path);
        makeComponent(url);
    }

    @FXML
    void ActionChosen(ActionEvent event) {
        String path = "";
        switch (comboBoxActions.getValue()) {
            case "Find Path":
                path = FIND_PATHS_FXML_NAME;
                break;
            case "Find Circuit":
                path = FINDCIRCUIT_FXML_NAME;
                break;
            case "What-if?":
                path = WHATIF_FXML_NAME;
                break;
        }
        URL url = getClass().getResource(path);
        makeComponent(url);

        comboBoxActions.setValue("");
    }

    private void makeComponent(URL url) {
        Component component = ComponentCreator.createComponent(url);
        component.getController().setParentController(this);
        component.getController().init();
        borderPaneApp.setCenter(component.getPane());
    }


    public TargetGraphDTO getGraphInfo() {
        return engine.getGraphInfo();
    }

    public PathsDTO getFoundPaths(String src, String dest, TargetsRelationType type) {
        return engine.findPaths(src, dest, type);
    }

    public Set<String> getAllTargetsByName() {
        return engine.getAllTargetsByName();
    }

    public Set<String> getCurrentTaskTargetsByName() {
        return engine.getCurrentTaskTargetByName();
    }

    public void fillComboBoxWithTargets(ComboBox<String> comboBox) {
        Set<String> targets = getAllTargetsByName();
        ObservableList<String> list = comboBox.getItems();
        list.addAll(targets);
    }

    public CircuitDTO findCircuit(String target) {
        return engine.findCircuit(target);
    }

    public List<TargetInfoDTO> getTarget(String targetName, TargetsRelationType relationType) {
        return engine.getTargetsByRelation(targetName, relationType);
    }

    public void initTask(TaskConfig taskConfig) {
        engine.initTask(taskConfig);
    }

    public void startTask() throws IOException, InterruptedException {
        new Thread(engine.getCurrTask()).start();
    }

    public void pauseTask() {
        engine.pause();
    }

    public void resumeTask() {
        engine.resume();
    }

        public RunTask getCurrTask() {
        return engine.getCurrTask();
    }

    public ObservableList<String> getList(String runStatus) {
        return engine.getList(runStatus);
    }
    public TargetInfoDTO getTargetInfoDTOByName(String name) {
        return engine.getTargetInfoDTO(name);
    }
}