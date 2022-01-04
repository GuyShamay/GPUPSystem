package application.components.task;

import application.components.app.AppController;
import application.components.task.config.TaskConfigController;
import application.general.Component;
import application.general.ComponentCreator;
import application.general.Controller;
import component.task.config.TaskConfig;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

public class TaskController implements Controller {
    private static final String TASK_CONFIG_FXML_NAME = "config/task-config.fxml";

    @FXML
    private Button settingsButton;
    @FXML
    private Button runTaskButton;
    @FXML
    private Button pauseAndResumeButton;
    @FXML
    private Label selectedTNameLabel;
    @FXML
    private Label selectedTTypeLabel;
    @FXML
    private FlowPane selectedTargetFlowPanel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label taskMessageLabel;
    @FXML
    private TableView<?> runResultTable;
    @FXML
    private TableColumn<?, ?> frozenCol;
    @FXML
    private TableColumn<?, ?> waitingCol;
    @FXML
    private TableColumn<?, ?> skippedCol;
    @FXML
    private TableColumn<?, ?> successCol;
    @FXML
    private TableColumn<?, ?> warningsCol;
    @FXML
    private TableColumn<?, ?> failureCol;

    private AppController appController;
    private TaskConfigController taskConfigController;
    private TaskConfig taskConfig;
    private BooleanProperty isRunning;
    private BooleanProperty isFinished;

    @FXML
    public void initialize() {
        isRunning = new SimpleBooleanProperty(false);
        runTaskButton.setDisable(true);
        pauseAndResumeButton.setDisable(true);
        pauseAndResumeButton.textProperty().bind(
                Bindings.when(isRunning)
                        .then("Pause")
                        .otherwise("Resume"));
    }


    @FXML
    void pauseAndResumeButtonClicked(ActionEvent event) {
        isRunning.set(!isRunning.get());

    }

    @FXML
    void runTaskButtonClicked(ActionEvent event) {
        if (taskConfig != null) {
            try {
                appController.initTask(taskConfig);
                appController.startTask();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            taskMessageLabel.setText("Please define task's Settings");
        }
    }

    @FXML
    void settingButtonClicked(ActionEvent event) {
        Stage stage = new Stage();

        URL url = getClass().getResource(TASK_CONFIG_FXML_NAME);
        Component taskConfigComponent = ComponentCreator.createComponent(url);
        taskConfigController = (TaskConfigController) taskConfigComponent.getController();
        taskConfigController.setParentController(this);
        taskConfigController.fetchData();
        taskConfigController.submittedProperty().addListener(((observable, oldValue, newValue) -> {
            pauseAndResumeButton.setDisable(!newValue);
            runTaskButton.setDisable(!newValue);
        }));
        Scene scene = new Scene(taskConfigComponent.getPane(), 404, 504);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }


    public Set<String> getTargetsListByName() {
        return appController.getTargetsListByName();
    }

    public int getMaxParallelism() {
        return appController.getMaxParallelism();
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void setTasConfig(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }
}
