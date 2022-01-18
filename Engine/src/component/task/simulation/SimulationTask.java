package component.task.simulation;


import component.target.FinishResult;
import component.target.Target;
import component.task.Task;
import component.task.config.SimulationConfig;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;
import java.util.Random;

public class SimulationTask implements Task {
    private final int processingTimeInMs;
    private final ProcessingTimeType processingTimeType;
    private final float successProb;
    private final float successWithWarningsProb;
    private long sleepingTime;
    private final Random random;
    private List<Target> targets;
    private int parallelism;
    private SimpleStringProperty taskOutput;

    public SimulationTask(String name, ProcessingTimeType processingTime, float succesProb, float ifSucces_withWarningsProb, int processingTimeInMs) {
        this.random = new Random();
        this.processingTimeInMs = processingTimeInMs;
        this.processingTimeType = processingTime;
        this.successProb = succesProb;
        this.successWithWarningsProb = ifSucces_withWarningsProb;
    }

    public SimulationTask(SimulationConfig config, int parallelism) {
        this.random = new Random();
        this.processingTimeInMs = config.getProcessingTime();
        this.processingTimeType = config.getProcessingTimeType();
        this.successProb = config.getSuccessProb();
        this.successWithWarningsProb = config.getSuccessWithWarningsProb();
        this.parallelism = parallelism;
        taskOutput = new SimpleStringProperty();

    }

    public FinishResult run(String targetName, String userData) throws InterruptedException {
        float LuckyNumber = (float) Math.random();
        calcSingleTargetProcessingTimeInMs();
        Platform.runLater(() -> {
            taskOutput.setValue(targetName + ": Sleeping Time - " + sleepingTime + "\n");
        });
        FinishResult res = LuckyNumber < successProb ? FinishResult.SUCCESS : FinishResult.FAILURE;

        if (res == FinishResult.SUCCESS) {
            LuckyNumber = (float) Math.random();
            res = LuckyNumber < successWithWarningsProb ? FinishResult.WARNING : FinishResult.SUCCESS;
        }
        Platform.runLater(() -> {
            taskOutput.setValue(targetName + " going to sleep\n");
        });
        Thread.sleep(sleepingTime);
        Platform.runLater(() -> {
            taskOutput.setValue(targetName + " woke up\n");
        });
        return res;
    }

    @Override
    public long getProcessingTime() {
        return sleepingTime;
    }

    private void calcSingleTargetProcessingTimeInMs() {
        int res = 0;
        switch (processingTimeType) {
            case Permanent:
                res = processingTimeInMs;
                break;
            case Random: {
                res = random.nextInt(processingTimeInMs);
                break;
            }
        }
        sleepingTime = res;
    }

    @Override
    public void updateRelevantTargets(List<Target> targets) {
        this.targets = targets;
    }

    @Override
    public int getParallelism() {
        return parallelism;
    }

    @Override
    public void incParallelism(Integer newVal) {
        parallelism = newVal;
    }

    @Override
    public SimpleStringProperty getTaskOutput() {
        return taskOutput;
    }
}