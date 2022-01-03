package component.task.config;

import component.target.TargetsRelationType;

import java.util.List;

public class TaskConfig {
    private boolean isAllTargets;
    private boolean fromScratch;
    private boolean incremental;
    private List<String> customTargets;
    private String whatIfTarget;
    private TargetsRelationType whatIfRelation;
    private int threadsParallelism;
    private Config specificConfig;


    public boolean isAllTargets() {
        return isAllTargets;
    }

    public void setAllTargets(boolean allTargets) {
        isAllTargets = allTargets;
    }

    public boolean isFromScratch() {
        return fromScratch;
    }

    public void setFromScratch(boolean fromScratch) {
        this.fromScratch = fromScratch;
    }

    public boolean isIncremental() {
        return incremental;
    }

    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
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
