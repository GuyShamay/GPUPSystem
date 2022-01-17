package application.components.task;

import application.components.app.AppController;
import application.components.task.config.TaskConfigController;
import application.general.Component;
import application.general.ComponentCreator;
import application.general.Controller;
import component.task.config.TaskConfig;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskController implements Controller {
    private static final String TASK_CONFIG_FXML_NAME = "config/task-config.fxml";

    @FXML
    private Button buttonSettings;
    @FXML
    private Button buttonRunTask;
    @FXML
    private Button buttonPauseAndResume;
    @FXML
    private Button buttonTargetPick;
    @FXML
    private ComboBox<String> comboBoxTargetPick;
    @FXML
    private FlowPane selectedTargetFlowPanel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label labelRunTaskStatus;
    @FXML
    private Label labelPickedName;
    @FXML
    private Label labelPickedType;
    @FXML
    private Label labelSkipped;
    @FXML
    private Label labelSuccess;
    @FXML
    private Label labelWarnings;
    @FXML
    private Label labelFailure;
    @FXML
    private Label labelTaskStatus;
    @FXML
    private Label labelTaskMessage;
    @FXML
    private ListView<String> frozenCol;
    @FXML
    private ListView<String> waitingCol;
    @FXML
    private ListView<String> skippedCol;
    @FXML
    private ListView<String> failureCol;
    @FXML
    private ListView<String> warningsCol;
    @FXML
    private ListView<String> successCol;
    @FXML
    private ListView<String> inProcessCol;


    private AppController appController;
    private TaskConfigController taskConfigController;
    private TaskConfig taskConfig;

    private SimpleBooleanProperty isRunning;
    private SimpleBooleanProperty isFinished;
    EngineTT e;

    @FXML
    public void initialize() {
        isRunning = new SimpleBooleanProperty(false);
        isFinished = new SimpleBooleanProperty(true);
        buttonRunTask.setDisable(true);
        buttonPauseAndResume.disableProperty().bind(
                Bindings.when(isFinished)
                        .then(true)
                        .otherwise(false));
        buttonSettings.disableProperty().bind(
                Bindings.when(isFinished)
                        .then(false)
                        .otherwise(true));

        buttonPauseAndResume.textProperty().bind(
                Bindings.when(isRunning)
                        .then("Pause")
                        .otherwise("Resume"));
        // TEST
        e = new EngineTT();
    }


    @FXML
    void pauseAndResumeButtonClicked(ActionEvent event) {
        isRunning.set(!isRunning.get());
        if (!isRunning.get()) {
            // need to pause
            appController.pauseTask();
        } else {
            // need to resume
            appController.resumeTask();
        }
    }

    @FXML
    void runTaskButtonClicked(ActionEvent event) {
        if (isFinished.get()) {
            labelRunTaskStatus.setText("");
            cleanData();
            isRunning.set(true);
            isFinished.set(false);

            // Invoke task in engine
            if (taskConfig != null) {
                try {
                    appController.initTask(taskConfig);
                    bindTaskToUI();
                    appController.startTask();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                labelTaskMessage.setText("Please define task's Settings");
            }
            // TEST
            //e.initTask();
           // labelTaskMessage.textProperty().bind(e.getCurrTask().messageProperty());
         //   progressBar.progressProperty().bind(e.getCurrTask().progressProperty());
           // successCol.setItems(e.getList("success"));
          //  frozenCol.setItems(e.getList("frozen"));
           // waitingCol.setItems(e.getList("waiting"));

            // Binding Task Result:
//            TaskResults TR = (TaskResults) ((TaskTT) e.getCurrTask()).getTaskResults();
//            labelTaskStatus.textProperty().bind(TR.messageProperty());
//            labelSuccess.textProperty().bind(TR.successTargetsProperty().asString());
//            labelWarnings.textProperty().bind(TR.warningsTargetsProperty().asString());
//            labelFailure.textProperty().bind(TR.failureTargetsProperty().asString());
//            labelSkipped.textProperty().bind(TR.skippedTargetsProperty().asString());
//            TR.valueProperty().addListener((observable, oldValue, newValue) -> {
//                onTaskResultFinished();
//            });

//            e.getCurrTask().valueProperty().addListener((observable, oldValue, newValue) -> {
//                onTaskFinished();
//            });
            // run task

          //  e.runTaskEngine();
        } else { // The task isn't over (could be in pause or running)
            if (!isRunning.get()) { // The task is in pause
                labelRunTaskStatus.setText("The task isn't over yet, resume it.");
            } else { // The task is running at the moment
                labelRunTaskStatus.setText("The task isn't over yet, still running.");
            }
        }
    }

    private void bindTaskToUI() {
        labelTaskMessage.textProperty().bind(appController.getCurrTask().messageProperty());
        progressBar.progressProperty().bind(appController.getCurrTask().progressProperty());
        frozenCol.setItems(appController.getList("frozen"));
        waitingCol.setItems(appController.getList("waiting"));
        inProcessCol.setItems(appController.getList("inProcess"));
        skippedCol.setItems(appController.getList("skipped"));
        successCol.setItems(appController.getList("success"));
        warningsCol.setItems(appController.getList("warnings"));
        failureCol.setItems(appController.getList("failure"));
        appController.getCurrTask().valueProperty().addListener(((observable, oldValue, newValue) -> {
            onTaskFinished();
        }));
    }

    private void onTaskFinished() {
        isFinished.set(true);
        isRunning.set(false);
        labelTaskMessage.textProperty().unbind();
        progressBar.progressProperty().unbind();
    }

    public void onTaskResultFinished() {
        labelTaskStatus.textProperty().unbind();
        labelSuccess.textProperty().unbind();
        labelWarnings.textProperty().unbind();
        labelFailure.textProperty().unbind();
        labelSkipped.textProperty().unbind();
    }
//        if (taskConfig != null) {
//            try {
//                appController.initTask(taskConfig);
//                appController.startTask();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        } else {
//            taskMessageLabel.setText("Please define task's Settings");
//        }

    private void cleanData() {
        successCol.getItems().clear();
        frozenCol.getItems().clear();
        waitingCol.getItems().clear();
        failureCol.getItems().clear();
        skippedCol.getItems().clear();
        inProcessCol.getItems().clear();
        warningsCol.getItems().clear();
        progressBar.setPadding(Insets.EMPTY);
        labelTaskMessage.setText("");
        labelSkipped.setText("-");
        labelFailure.setText("-");
        labelWarnings.setText("-");
        labelSuccess.setText("-");
        labelTaskStatus.setText("");
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
            buttonRunTask.setDisable(!newValue);
        }));
        Scene scene = new Scene(taskConfigComponent.getPane(), 404, 504);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        AtomicBoolean isCancel = new AtomicBoolean(false);
        stage.setOnCloseRequest(e -> {
            isCancel.set(true);
        });
        stage.showAndWait();
        if (!isCancel.get()) {
            updateTargetPick();
        }
    }

    private void updateTargetPick() {
        if (taskConfig.isAllTargets()) {
            ObservableList<String> all = FXCollections.observableArrayList(appController.getTargetsListByName());
            comboBoxTargetPick.setItems(all);
        } else if (taskConfig.getCustomTargets().size() != 0) {
            ObservableList<String> custom = FXCollections.observableArrayList(taskConfig.getCustomTargets());

            comboBoxTargetPick.setItems(custom);
        } else {
            /// NEED TO ADD
        }
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

    // Pick target while running ----------------------------------


    @FXML
    void buttonGetStatusClicked(ActionEvent event) {
        if (!comboBoxTargetPick.getSelectionModel().isEmpty()) {
            labelPickedName.setText(comboBoxTargetPick.getSelectionModel().getSelectedItem());
        }


        comboBoxTargetPick.getSelectionModel().clearSelection();
    }
}
