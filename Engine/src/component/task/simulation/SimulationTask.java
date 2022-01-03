package component.task.simulation;


import component.target.FinishResult;
import component.target.Target;
import component.task.Task;
import component.task.config.Config;
import component.task.config.SimulationConfig;
import dto.StatisticsDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationTask implements Task {
    private final String name;
    private final int processingTimeInMs;
    private final ProcessingTimeType processingTimeType;
    private final float successProb;
    private final float successWithWarningsProb;
    private String dirPath;
    private long sleepingTime;
    private Random random;
    private List<Target> targets;

    public SimulationTask(String name, ProcessingTimeType processingTime, float succesProb, float ifSucces_withWarningsProb, int processingTimeInMs) {
        this.random = new Random();
        this.name = name;
        this.processingTimeInMs = processingTimeInMs;
        this.processingTimeType = processingTime;
        this.successProb = succesProb;
        this.successWithWarningsProb = ifSucces_withWarningsProb;
    }

    public SimulationTask(Config config){
        this.random = null;
        this.name = null;
        this.processingTimeInMs = 0;
        this.processingTimeType = ProcessingTimeType.Permanent;
        this.successProb = 0;
        this.successWithWarningsProb =0;
    }

    public FinishResult run() throws InterruptedException {
        float LuckyNumber = (float) Math.random();
        FinishResult res = LuckyNumber < successProb ? FinishResult.SUCCESS : FinishResult.FAILURE;

        if (res == FinishResult.SUCCESS) {
            LuckyNumber = (float) Math.random();
            res = LuckyNumber < successWithWarningsProb ? FinishResult.WARNING : FinishResult.SUCCESS;
        }
        Thread.sleep(sleepingTime);
        return res;
    }

    @Override
    public String getDirectoryPath() {
        return dirPath;
    }

    @Override
    public void setDirectoryPath(String path) {
        dirPath = path;
    }

    @Override
    public void updateProcessingTime(){
        calcSingleTargetProcessingTimeInMs();
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
    public void updateRelevantTargets(List<Target> targets){
        this.targets=targets;
    }

    @Override
    public List<StatisticsDTO.TargetRunDTO> getTargetsRunInfo(){

        List<StatisticsDTO.TargetRunDTO> targetsRunInfoList = new ArrayList<>();

        targets.forEach(((target) -> {
            StatisticsDTO.TargetRunDTO targetRunDTO = new StatisticsDTO().new TargetRunDTO(target.getName(), target.getFinishResult(), target.getTaskRunDuration());
            targetsRunInfoList.add(targetRunDTO);
        }));

        return targetsRunInfoList;
    }
}