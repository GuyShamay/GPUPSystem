package component.task;

import component.target.FinishResult;
import component.target.Target;
import dto.StatisticsDTO;

import java.util.List;

public interface Task {
    FinishResult run() throws InterruptedException;

    String getDirectoryPath();

    void setDirectoryPath(String path);

    long getProcessingTime();

    default void updateProcessingTime() {
    }

    void updateRelevantTargets(List<Target> targets);

    int getParallelism();

    void incParallelism();
//    List<StatisticsDTO.TargetRunDTO> getTargetsRunInfo();
}
