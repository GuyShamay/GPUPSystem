package application.components.task;

import component.target.RunResult;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TT {
    private String name;
    SimpleObjectProperty<RunResult> runResult;

    public TT(String name) {
        runResult = new SimpleObjectProperty<>();
        this.name = name;
        runResult.setValue(RunResult.FROZEN);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RunResult getRunResult() {
        return runResult.get();
    }

    public SimpleObjectProperty<RunResult> runResultProperty() {
        return runResult;
    }

    public void setRunResult(float p) {
        /*if (p < 0.5) {
            this.runResult.set(RunResult.SKIPPED);
        } else if (p < 0.8) {
            this.runResult.set(RunResult.WAITING);
        } else {*/
            this.runResult.set(RunResult.FINISHED);

    }



}
