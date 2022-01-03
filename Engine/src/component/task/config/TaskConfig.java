package component.task.config;

import component.target.TargetsRelationType;
import component.task.ProcessingType;
import component.task.TaskType;

import java.util.List;

public class TaskConfig {
    private boolean isAllTargets;
    private ProcessingType processingType;
    private List<String> customTargets;
    private String whatIfTarget;
    private TargetsRelationType whatIfRelation;
    private int threadsParallelism;

    public Config getSpecificConfig() {
        return specificConfig;
    }

    private Config specificConfig;
    private TaskType taskType;

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public boolean isAllTargets() {
        return isAllTargets;
    }

    public void setAllTargets(boolean allTargets) {
        isAllTargets = allTargets;
    }

    public ProcessingType getProcessingType() {
        return processingType;
    }

    public void setProcessingType(ProcessingType processingType) {
        this.processingType = processingType;
    }

    public List<String> getCustomTargets() {
        return customTargets;
    }

    public void setCustomTargets(List<String> customTargets) {
        this.customTargets = customTargets;
    }

    public String getWhatIfTarget() {
        return whatIfTarget;
    }

    public void setWhatIfTarget(String whatIfTarget) {
        this.whatIfTarget = whatIfTarget;
    }

    public TargetsRelationType getWhatIfRelation() {
        return whatIfRelation;
    }

    public void setWhatIfRelation(TargetsRelationType whatIfRelation) {
        this.whatIfRelation = whatIfRelation;
    }

    public int getThreadsParallelism() {
        return threadsParallelism;
    }

    public void setThreadsParallelism(int threadsParallelism) {
        this.threadsParallelism = threadsParallelism;
    }

}
