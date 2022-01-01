package gpup.dto;

import gpup.component.target.FinishResult;

import java.time.Duration;
import java.util.List;

public class StatisticsDTO implements GPUPConsumerDTO {

    public StatisticsDTO() {
    }

    public StatisticsDTO(Duration totalRunDuration, List<TargetRunDTO> targets) {
        this.totalRunDuration = totalRunDuration;
        this.targets = targets;
        calcTargetsRunResultStatistics();
    }

    private void calcTargetsRunResultStatistics() {
        targets.forEach((targetRunDTO -> {
            if(targetRunDTO.finishResult!=null) {
                switch (targetRunDTO.finishResult) {
                    case SUCCESS:
                        successTargetsNum++;
                        break;
                    case WARNING:
                        warningsTargetsNum++;
                        break;
                    case FAILURE:
                        failureTargetsNum++;
                        break;
                }
            }
        else
            skippedTargetsNum++;
        }));
    }

    private Duration totalRunDuration;
    private List<TargetRunDTO> targets;
    private int successTargetsNum = 0;
    private int warningsTargetsNum = 0;
    private int failureTargetsNum = 0;
    private int skippedTargetsNum = 0;

    public class TargetRunDTO{
        private String name;
        private FinishResult finishResult;
        private Duration runTimeDuration;

        public TargetRunDTO(String name,FinishResult runResult,Duration runTimeDuration ){
            this.name=name;
            this.finishResult =runResult;
            this.runTimeDuration=runTimeDuration;
        }

        @Override
        public String toString() {
            String runResult = finishResult==null? "-NOT- Finished , SKIPPED" : "FINISHED with "+ finishResult;
            String runTime = runTimeDuration==null ? "" : "Procceced In " + String.format("%d:%02d:%02d",
                    runTimeDuration.toHours(),
                    runTimeDuration.toMinutes(),
                    runTimeDuration.getSeconds());
        return "\n    Target " + name + "\n "+ runResult+ "\n "+ runTime;
        }
    }

    @Override
    public String toString(){

        String res ="~~~~~~~~~~~~~~ RUN SUMMARY ~~~~~~~~~~~~~~"+
                "\nTotal Task Run Duration : " + String.format("%d:%02d:%02d",
                totalRunDuration.toHours(),
                totalRunDuration.toMinutes(),
                totalRunDuration.getSeconds()) +
                "\n                STATISTICS                  "+
                "\n SUCCES TARGETS........." + successTargetsNum +
                "\n WARNING TARGETS........" + warningsTargetsNum+
                "\n FAILURE TARGETS........" + failureTargetsNum+
                "\n SKIPPED TARGETS........" + skippedTargetsNum+
                "\n\n                 TARGETS                   " ;

        for (TargetRunDTO targetRunDTO:targets) {
            res+=targetRunDTO.toString();
        }
        return res;
    }
}
