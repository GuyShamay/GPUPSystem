package component.task.compile;

import component.target.FinishResult;
import component.target.Target;
import component.task.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class CompileTask implements Task {
    private String srcDirectory;
    private String destDirectory;
    private String filePath;
    private int parallelism;
    private Duration processingTime;

    public CompileTask(String srcDirectory, String destDirectory, int parallelism) {
        this.srcDirectory = srcDirectory;
        this.destDirectory = destDirectory;
        this.parallelism = parallelism;
    }

    public void setPathFromFQN(String fqn) {
        filePath = srcDirectory + "\\" +
                fqn.replace(".", "\\") +
                ".java";
    }

    @Override
    public FinishResult run() throws InterruptedException {
        Instant start = Instant.now();
        FinishResult result = null;
        try {
            String[] command = {"javac", "-d", destDirectory, "-cp", srcDirectory, filePath};
            Process pb = new ProcessBuilder(command).start();

            // Need to send to UI
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(pb.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = pb.waitFor();
            System.out.println("Exit: "+ exitCode);
            result = exitCode == 0 ? FinishResult.SUCCESS : FinishResult.FAILURE;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


        Instant end = Instant.now();
        processingTime = Duration.between(start, end);
        System.out.println("Ran for: " + processingTime.toString());
        return result;
    }


    @Override
    public long getProcessingTime() {
        return 0;
    }

    @Override
    public void updateRelevantTargets(List<Target> targets) {

    }

    @Override
    public int getParallelism() {
        return parallelism;
    }

    @Override
    public void incParallelism(Integer newVal) {
        parallelism = newVal;
    }

}
