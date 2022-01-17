package component.task;

import component.target.FinishResult;
import component.target.Target;

import java.util.List;

public interface Task {

    FinishResult run() throws InterruptedException;

    long getProcessingTime();

    default void updateProcessingTime() {
    }

    void updateRelevantTargets(List<Target> targets);

    int getParallelism();

    void incParallelism(Integer newVal);
}
