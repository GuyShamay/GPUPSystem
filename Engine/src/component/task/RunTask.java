package component.task;

import component.progressdata.ProgressData;
import component.target.FinishResult;
import component.target.Result;
import component.target.RunResult;
import component.target.Target;
import component.targetgraph.TargetGraph;
import dto.GPUPConsumer;
import dto.ProcessedTargetDTO;
import dto.SimulationOutputDTO;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

public class RunTask extends Task<Boolean> {

    private TargetGraph subTargetGraph;
    private ExecutorService threadExecutor;
    private component.task.Task task;
    private boolean runPaused;
    private final Object condition;
    private final Object changeRunResult;
    private boolean doneRunning;
    private ProgressData progressData;
   // private List<String> currRunTargets;
    private Set<Target> doneTargets;

    public RunTask(component.task.Task task){
        this.task=task;
        runPaused = false;
        doneRunning = false;
        progressData = new ProgressData();
        condition = new ReentrantLock();
        changeRunResult=new ReentrantLock();
        doneTargets = new HashSet<>();
    }

    public void setSubTargetGraph(TargetGraph subTargetGraph){
        this.subTargetGraph=subTargetGraph;
    }

    public void initProgressData(ProcessingType processingType){
        subTargetGraph.getTargetsMap().forEach((s, target) -> progressData.initToFrozen(s));
    }

    @Override
    protected Boolean call() throws Exception {
        updateMessage("Task Starting");
        updateProgress(0, subTargetGraph.count());
        startTask();
        updateMessage("Task Finished");
        return true;
    }

    public void startTask() throws InterruptedException {
        Instant totalStart, totalEnd;
        totalStart = Instant.now();
        runTask();
        totalEnd = Instant.now();
        Duration totalRunDuration = Duration.between(totalStart, totalEnd);
        //StatisticsDTO statisticsDTO = calcStatistics(totalRunDuration);
        //System.out.println(statisticsDTO);
    }

    private void runTask() throws InterruptedException {
        List<Target> waitingList = subTargetGraph.getAllWaitingTargets();
        List<Future<?>> futures = new ArrayList<Future<?>>();
        threadExecutor = Executors.newFixedThreadPool(task.getParallelism());

        do {
            while (!waitingList.isEmpty()) {
                while (runPaused) {
                    handlePause(futures, waitingList);
                    Thread.sleep(1000);
                    System.out.println("max parallism is " + task.getParallelism());
                    System.out.println("POOL SIZE:" + ((ThreadPoolExecutor) threadExecutor).getPoolSize());
                    System.out.println("Core POOL Size:" + ((ThreadPoolExecutor) threadExecutor).getCorePoolSize());
                }
                Target currentTarget = waitingList.remove(0);
                while (currentTarget.isLock()) {
                    Thread.yield();
                }
                if (!currentTarget.isLock()) {
                    Runnable r = () -> {
                        try {
                            runTarget(waitingList, currentTarget);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    };
                    Future<?> f = threadExecutor.submit(r);
                    futures.add(f);
                    // System.out.println("POOL SIZE:"+((ThreadPoolExecutor)threadExecutor).getPoolSize());
                }
            }
        } while (!AllThreadsDone(futures));


        doneRunning = true;
        threadExecutor.shutdownNow();
        //System.out.println("FINISHED WITH POOL SIZE:" + ((ThreadPoolExecutor) threadExecutor).getPoolSize());
    }

    public void resume() {
        if (!doneRunning) {
            runPaused = false;
            synchronized (condition) {
                System.out.println("thread number " + Thread.currentThread() + " im waking run");
                Thread.getAllStackTraces().keySet();
                condition.notify();
            }
        }
    }

    public void pause() {
        if (!doneRunning)
            runPaused = true;
    }

    public boolean isRunPaused() {
        return runPaused;
    }

    private void handlePause(List<Future<?>> futures, List<Target> waitingList) throws InterruptedException {
        cancelAllFutures(futures);
        try {
            synchronized (condition) {
                System.out.println("thread number " + Thread.currentThread() + "says: im going to sleep");
                condition.wait();
                ((ThreadPoolExecutor) threadExecutor).setCorePoolSize(task.getParallelism());
                updateWaitingList(waitingList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("thread number " + Thread.currentThread() + "i waked up");
    }

    private void updateWaitingList(List<Target> waitingList) {
        subTargetGraph.getTargetsMap().forEach(((s, target) -> {
            if (target.getRunResult().equals(RunResult.WAITING) && !waitingList.contains(target)) {
                waitingList.add(target);
            }
        }));
    }

    private void cancelAllFutures(List<Future<?>> futures) {
        futures.forEach(future -> {
            future.cancel(false);
        });
    }

    private boolean AllThreadsDone(List<Future<?>> futures) {
        boolean allDone = true;
        for (Future<?> future : futures) {
            allDone &= future.isDone(); // check if future is done
        }
        return allDone;
    }

    private void runTarget(List<Target> waitingList, Target currentTarget) throws InterruptedException {
        subTargetGraph.lockSerialSetOf(currentTarget);

        Instant start, end;
        start = Instant.now();

        changeRunResult(RunResult.WAITING, RunResult.INPROCESS, currentTarget);
        task.updateProcessingTime();
        currentTarget.setFinishResult(task.run());
        synchronized (changeRunResult) {
            System.out.println("TRY Adding TO DONE SET "+ currentTarget.getName()+ " . THERE IS Already "+ doneTargets.size());
            doneTargets.add(currentTarget);
            updateProgress(doneTargets.size(), subTargetGraph.count());
        }
        changeRunResult(RunResult.INPROCESS ,currentTarget.getFinishResult(), currentTarget);
        updateGraphAfterTaskResult(waitingList, currentTarget);

        end = Instant.now();

        currentTarget.setTaskRunDuration(Duration.between(start, end));
        //Temporary
        GPUPConsumer consumerDTO = new ProcessedTargetDTO(currentTarget);
        consumerDTO.setTaskOutput(new SimulationOutputDTO(task.getProcessingTime()));
        print(consumerDTO);
        // Writing to console

        subTargetGraph.unlockSerialSetOf(currentTarget);
    }

    private void changeRunResult(Result from, Result to, Target target) {
        synchronized (changeRunResult) {
            if (from != to) {
                if (to instanceof RunResult)
                    target.setRunResult((RunResult) to);
                else
                    target.setFinishResult((FinishResult) to);
                Platform.runLater(() -> {
                    progressData.move(from, to, target.getName());
                });
            }
        }
    }

    private synchronized void print(GPUPConsumer consumerDTO) {
        System.out.println(consumerDTO);
        System.out.println(Thread.currentThread().getId());
    }

    private void updateGraphAfterTaskResult(List<Target> waitingList, Target currentTarget) {
        synchronized (changeRunResult) {
            currentTarget.setRunResult(RunResult.FINISHED);
            if (currentTarget.getFinishResult().equals(FinishResult.FAILURE)) {
                subTargetGraph.dfsTravelToUpdateSkippedList(currentTarget);
                addSkippedToDoneSet(currentTarget);
                changeRunResultOfList(currentTarget.getSkippedList(), RunResult.FROZEN, RunResult.SKIPPED);
                subTargetGraph.updateTargetAdjAfterFinishWithFailure(currentTarget);
            } else {
                subTargetGraph.updateTargetAdjAfterFinishWithoutFailure(progressData,waitingList, currentTarget);
            }
        }
    }

    private void addSkippedToDoneSet(Target currentTarget) {
        currentTarget.getSkippedList().forEach(target -> {
            System.out.println("TRY Adding TO DONE SET "+ target.getName()+ " . THERE IS Already "+ doneTargets.size());
            doneTargets.add(target);
        });
    }

    private void changeRunResultOfList(List<Target> list, Result from, Result to) {
        synchronized (changeRunResult) {
            for (Target t: list ) {
                changeRunResult(from, to, t);
            }
        }
    }

    public ProgressData getProgressData() {
        return progressData;
    }

}
