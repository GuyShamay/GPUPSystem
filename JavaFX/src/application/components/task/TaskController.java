package application.components.task;

import application.components.app.AppController;
import application.components.task.config.TaskConfigController;
import application.general.Component;
import application.general.ComponentCreator;
import application.general.Controller;
import component.task.config.TaskConfig;
import dto.TargetInfoDTO;
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
import java.time.Duration;
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
    private ComboBox<String> comboBoxTargetPick;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Spinner<Integer> spinnerIncThreads;
    @FXML
    private Label labelIncThreads;
    @FXML
    private Label labelPickInfo;
    @FXML
    private Label labelRunTaskStatus;
    @FXML
    private Label labelPickedName;
    @FXML
    private Label labelPickedType;
    @FXML
    private Label labelSkipped;
    @FXML
    private Label labelTotal;
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
    private TextArea textAreaOutput;

    @FXML
    private ListView<String> inProcessCol;


    private AppController appController;
    private TaskConfigController taskConfigController;
    private TaskConfig taskConfig;

    private SimpleBooleanProperty isRunning;
    private SimpleBooleanProperty isFinished;

    @FXML
    public void initialize() {
        isRunning = new SimpleBooleanProperty(false);
        isFinished = new SimpleBooleanProperty(true);
        bindingControls();
    }

    private void bindingControls() {
        buttonRunTask.setDisable(true);
        spinnerIncThreads.setVisible(false);
        labelIncThreads.setVisible(false);

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

    }


    @FXML
    void pauseAndResumeButtonClicked(ActionEvent event) {
        isRunning.set(!isRunning.get());
        if (!isRunning.get()) {
            // need to pause
            spinnerIncThreads.setVisible(true);
            labelIncThreads.setVisible(true);
            appController.pauseTask();
        } else {
            // need to resume
            spinnerIncThreads.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(spinnerIncThreads.getValue(), appController.getMaxParallelism()));
            appController.incParallelism(spinnerIncThreads.getValue());
            spinnerIncThreads.setVisible(false);
            labelIncThreads.setVisible(false);
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
                // Test for compile
                /*CompileConfig compileConfig = (CompileConfig) taskConfig.getConfig();
                CompileTask task = new CompileTask(compileConfig.getSrcDir(), compileConfig.getDestDir(), 2);
                task.setPathFromFQN("gpup.compilation.example.l2.Koo");
                try {
                    task.run();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }*/
                try {
                    appController.initTask(taskConfig);
                    if (!appController.isCircuit()) {
                        updateTargetPick();
                        bindTaskToUI();
                        appController.startTask();
                    } else {
                        labelRunTaskStatus.setText("There is a cycle in the graph! can't run task.");
                        isRunning.set(false);
                        isFinished.set(true);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                labelTaskMessage.setText("Please define task's Settings");
            }
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
        textAreaOutput.textProperty().bind(Bindings.createStringBinding(() ->
                appController.getCurrTask().taskOutputProperty().get() + "-----------------\n" + textAreaOutput.getText(), appController.getCurrTask().taskOutputProperty()));
    }

    private void onTaskFinished() {
        isFinished.set(true);
        isRunning.set(false);
        spinnerIncThreads.setVisible(false);
        labelIncThreads.setVisible(false);
        labelTaskMessage.textProperty().unbind();
        progressBar.progressProperty().unbind();
        textAreaOutput.textProperty().unbind();
        updateTaskResult();
    }

    private void updateTaskResult() {
        int skipped = skippedCol.getItems().size();
        int success = successCol.getItems().size();
        int warnings = warningsCol.getItems().size();
        int fail = failureCol.getItems().size();
        int total = fail + warnings + skipped + success;

        labelSkipped.setText(String.valueOf(skipped));
        labelSuccess.setText(String.valueOf(success));
        labelWarnings.setText(String.valueOf(warnings));
        labelFailure.setText(String.valueOf(fail));
        if (skipped == 0) {
            labelTaskStatus.setText("Task finished completely");
        } else {
            labelTaskStatus.setText("Task didn't finished completely");
        }
        labelTotal.setText(String.valueOf(total));
    }

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
        textAreaOutput.setText("");
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
            spinnerIncThreads.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(taskConfig.getThreadsParallelism(), appController.getMaxParallelism()));
        }
    }

    private void updateTargetPick() {
        ObservableList<String> list = FXCollections.observableArrayList(appController.getCurrentTaskTargetsByName());
        comboBoxTargetPick.setItems(list);
    }

    public Set<String> getTargetsListByName() {
        return appController.getAllTargetsByName();
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
            String targetName = comboBoxTargetPick.getSelectionModel().getSelectedItem();
            labelPickedName.setText(targetName);
            TargetInfoDTO target = appController.getTargetInfoDTOByName(targetName);
            labelPickedType.setText(target.getType());
            String infoText = "";

            if (target.getSerialSetsList().size() != 0)
                infoText = "Serial Sets : " + target.getSerialSetsList() + "\n";

            Duration duration;
            switch (target.getRunResult()) {
                case "FROZEN":
                    infoText += "Frozen Until " + target.getDependsOnToOpenList().toString() + " Will Finish";
                    break;
                case "WAITING":
                    infoText += "Waiting Already :\n";
                    duration = target.getWaitingTimeInMs();
                    infoText += String.format("%d min\n%02d sec\n%02d ms",
                            duration.toMinutes(),
                            duration.getSeconds(),
                            duration.toMillis());
                    break;
                case "SKIPPED":
                    infoText += "Skipped Because " + target.getSkippedBecauseList().toString() + " Failed";
                    break;
                case "INPROCESS":
                    infoText += "InProcces Already :\n";
                    duration = target.getProcessingTimeInMs();
                    infoText += String.format("%d min\n%02d sec\n%02d ms",
                            duration.toMinutes(),
                            duration.getSeconds(),
                            duration.toMillis());
                    break;
                case "FINISHED":
                    infoText += "Finished With " + target.getFinishResult();
                    break;
            }

            labelPickInfo.setText(infoText);
        }
        comboBoxTargetPick.getSelectionModel().clearSelection();
    }
}
