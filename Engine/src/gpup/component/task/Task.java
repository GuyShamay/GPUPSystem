package gpup.component.task;

import gpup.component.target.FinishResult;
import gpup.component.target.Target;
import gpup.dto.StatisticsDTO;

import java.util.List;

public interface Task {
    FinishResult run() throws InterruptedException;

    String getDirectoryPath();

    void setDirectoryPath(String path);

    long getProcessingTime();

    default void updateProcessingTime() {
    }

    void updateRelevantTargets(List<Target> targets);

    List<StatisticsDTO.TargetRunDTO> getTargetsRunInfo();
}
