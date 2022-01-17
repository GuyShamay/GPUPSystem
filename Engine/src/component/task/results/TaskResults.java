package component.task.results;

import component.target.Target;
import dto.TargetDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TaskResults extends Task<Boolean> {
    List<TargetDTO> targets;
    private SimpleIntegerProperty warningsTargets;
    private SimpleIntegerProperty failureTargets;
    private SimpleIntegerProperty skippedTargets;
    private SimpleIntegerProperty successTargets;
    private SimpleBooleanProperty isFinish;
    private SimpleStringProperty taskStatus;


    public int getSuccessTargets() {
        return successTargets.get();
    }

    public SimpleIntegerProperty successTargetsProperty() {
        return successTargets;
    }

    public int getWarningsTargets() {
        return warningsTargets.get();
    }

    public SimpleIntegerProperty warningsTargetsProperty() {
        return warningsTargets;
    }

    public int getFailureTargets() {
        return failureTargets.get();
    }

    public SimpleIntegerProperty failureTargetsProperty() {
        return failureTargets;
    }

    public int getSkippedTargets() {
        return skippedTargets.get();
    }

    public SimpleIntegerProperty skippedTargetsProperty() {
        return skippedTargets;
    }

    public boolean isIsFinish() {
        return isFinish.get();
    }

    public SimpleBooleanProperty isFinishProperty() {
        return isFinish;
    }

    public TaskResults(List<Target> targets) {
        taskStatus = new SimpleStringProperty();
        successTargets = new SimpleIntegerProperty();
        warningsTargets = new SimpleIntegerProperty();
        failureTargets = new SimpleIntegerProperty();
        skippedTargets = new SimpleIntegerProperty();
        isFinish = new SimpleBooleanProperty(false);
        updateTaskResults(targets);
    }

    private void updateTaskResults(List<Target> targets) {
        this.targets = new ArrayList<>();
        targets.forEach(((target) -> {
            this.targets.add(new TargetDTO(target));
        }));
    }

    private void calsRunResults() {

        targets.forEach((target -> {
            if (target.getFinishResult() != null) {
                switch (target.getFinishResult()) {
                    case SUCCESS:
                        successTargets.set(skippedTargets.get() + 1);
                        break;
                    case WARNING:
                        warningsTargets.set(skippedTargets.get() + 1);
                        break;
                    case FAILURE:
                        failureTargets.set(skippedTargets.get() + 1);
                        break;
                }
            } else
                skippedTargets.set(skippedTargets.get() + 1);
        }));
        if (skippedTargets.get() != 0) {
            taskStatus.set("Task Failed");
        } else {
            taskStatus.set("Task Succeeded");
        }
        updateMessage(taskStatus.get());
    }

    @Override
    protected Boolean call() throws Exception {
        Platform.runLater(() -> calsRunResults());
        Platform.runLater(() -> {
            isFinish.set(false);
        });
        return true;
    }
}
