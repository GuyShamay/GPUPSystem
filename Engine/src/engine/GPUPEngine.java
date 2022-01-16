package engine;

import component.target.*;
import component.targetgraph.TargetGraph;
import component.task.ProcessingType;
import component.task.Task;
import component.task.config.SimulationConfig;
import component.task.config.TaskConfig;
import dto.*;
import exception.TargetExistException;
import component.task.simulation.ProcessingTimeType;
import component.task.simulation.SimulationTask;
import jaxb.generated.v2.GPUPDescriptor;
import jaxb.parser.GPUPParser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;


// IMPLEMENT INTERFACE
public class GPUPEngine implements Engine {
    private TargetGraph targetGraph;
    private Task task; // can it run several tasks?
    private ProcessingType processingType;
    private int maxParallelism;
    private TargetGraph subTargetGraph;
   private Map<String, Lock> locks;
    private Map<String, Condition> condList;
    ExecutorService threadExecutor;
    private boolean runPaused;
    private final Object condition = new ReentrantLock();
    private boolean doneRunning = false;

    private void loadXmlToTargetGraph(String path) throws FileNotFoundException, JAXBException, TargetExistException {
        final String PACKAGE_NAME = "jaxb.generated";
        GPUPDescriptor gpupDescriptor;
        InputStream inputStream = new FileInputStream(path);
        JAXBContext jc = JAXBContext.newInstance(PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        gpupDescriptor = (GPUPDescriptor) u.unmarshal(inputStream);
        targetGraph = GPUPParser.parseTargetGraph(gpupDescriptor);
    }

    @Override
    public void buildGraphFromXml(String path) throws JAXBException, FileNotFoundException, TargetExistException {
        loadXmlToTargetGraph(path);
    }

    @Override
    public void buildGraphFromXml(File file) throws JAXBException, FileNotFoundException, TargetExistException {
        loadFileToGraph(file);
    }

    private void loadFileToGraph(File file) throws FileNotFoundException, JAXBException {
        final String PACKAGE_NAME = "jaxb.generated.v2";
        GPUPDescriptor gpupDescriptor;

        InputStream inputStream = new FileInputStream(file);
        JAXBContext jc = JAXBContext.newInstance(PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        gpupDescriptor = (GPUPDescriptor) u.unmarshal(inputStream);
        targetGraph = GPUPParser.parseTargetGraph(gpupDescriptor);
        maxParallelism = GPUPParser.getMaxParallelism(gpupDescriptor);
    }

    @Override
    public TargetDTO getTargetInfo(String name) {
        if (targetGraph.isTargetExist(name)) {
            return targetGraph.getTargetInfo(name);
        }
        throw new NoSuchElementException("There is not a target named: " + name + "\n");
    }

    @Override
    public TargetGraphDTO getGraphInfo() {
        return new TargetGraphDTO(targetGraph);
    }

    @Override
    public boolean isInitialized() {
        return targetGraph != null;
    }

    public int getTargetsCount() {
        return targetGraph.count();
    }

    public int getSpecificTypeOfTargetsNum(TargetType targetType) {
        return targetGraph.getSpecificTypeOfTargetsNum(targetType);
    }

    @Override
    public void initTaskGPUP1(int targetProcessingTimeMs, int taskProcessingTimeType, float successProb, float successWithWarningsProb, ProcessingType status) {
        ProcessingTimeType procTimeType = taskProcessingTimeType == 1 ? ProcessingTimeType.Random : ProcessingTimeType.Permanent;
        task = new SimulationTask(targetGraph.getName(), procTimeType, successProb, successWithWarningsProb, targetProcessingTimeMs);
        targetGraph.buildTransposeGraph();
    }

    public void initTask(TaskConfig taskConfig) {
        subTargetGraph = createSubTargetGraph(taskConfig);
        subTargetGraph.updateTargetsTypes();
        subTargetGraph.buildTransposeGraph();
        processingType = taskConfig.getProcessingType();

        switch (taskConfig.getTaskType()) {
            case Simulation:
                task = new SimulationTask((SimulationConfig) taskConfig.getConfig(),taskConfig.getThreadsParallelism());
                break;
            case Compilation:
                break;
        }
        runPaused=false;
        doneRunning=false;
        initGraphForRun();
    }

    private void initGraphForRun() {
        correctProcessingType();
        subTargetGraph.prepareGraphFromProcType(processingType);
        task.updateRelevantTargets(subTargetGraph.getWaitingAndFrozen());
        subTargetGraph.clearJustOpenAndSkippedLists();
    }

    private void correctProcessingType() {
        if ((subTargetGraph.allTargetsFinished() || !subTargetGraph.allTargetsHaveRunResult()) && processingType == ProcessingType.Incremental) {
            System.out.println("Task will run 'From Scrach'.");
            processingType = ProcessingType.FromScratch;
        }
    }

    private TargetGraph createSubTargetGraph(TaskConfig taskConfig) {
        if (taskConfig.isAllTargets()) subTargetGraph = targetGraph;
        else if (taskConfig.getCustomTargets() != null) {
            subTargetGraph = targetGraph.buildSubGraph(taskConfig.getCustomTargets());
        } else {
            List<String> res = getWhatIfSubTargetsList(taskConfig.getWhatIfTarget(), taskConfig.getWhatIfRelation());
            subTargetGraph = targetGraph.buildSubGraph(res);
        }
        return subTargetGraph;
    }

    private List<String> getWhatIfSubTargetsList(String whatIfTarget, TargetsRelationType whatIfRelation) {
        List<String> res = new ArrayList<>();
        targetGraph.getTargetsByRelation(whatIfTarget, whatIfRelation).forEach(target -> {
            res.add(target.getName());
        });
        return res;
    }

    @Override
    public void setProcessingType(ProcessingType status) {
        this.processingType = status;
    }

    public void runTaskGPUP2() throws InterruptedException {
        Instant totalStart, totalEnd, start, end;
//        Thread runThread = new Thread(()->{
//            try {
//                run();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
        totalStart = Instant.now();
        run();
//        runThread.start();
//        runThread.join();
        totalEnd = Instant.now();
        Duration totalRunDuration = Duration.between(totalStart, totalEnd);
        StatisticsDTO statisticsDTO = calcStatistics(totalRunDuration);
        System.out.println(statisticsDTO);
    }

    private void run() throws InterruptedException {
        List<Target> waitingList = subTargetGraph.getAllWaitingTargets();
        List<Future<?>> futures = new ArrayList<Future<?>>();
        threadExecutor = Executors.newFixedThreadPool(task.getParallelism());

        do {
            while (!waitingList.isEmpty()) {
                while(runPaused){
                    handlePause(futures,waitingList);
                }
                Target currentTarget = waitingList.remove(0);
                while(currentTarget.isLock()){
                    Thread.yield();
                }
                if (!currentTarget.isLock()) {
                    subTargetGraph.lockSerialSetOf(currentTarget);
                    Runnable r = () -> {
                        try {
                            runTarget(waitingList, currentTarget);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    };
                    Future<?> f = threadExecutor.submit(r);
                    futures.add(f);
                }
            }
        } while (!AllThreadsDone(futures));

        doneRunning=true;
        threadExecutor.shutdownNow();
    }

    @Override
    public void resume(){
        if(!doneRunning){
            runPaused = false;
            synchronized (condition){
                System.out.println("im waking run");
                condition.notify();
            }
        }
    }

    @Override
    public void pause() {
        if(!doneRunning)
             runPaused=true;
    }

    @Override
    public boolean isRunPaused() {
        return runPaused;
    }

    private void handlePause(List<Future<?>> futures, List<Target> waitingList) throws InterruptedException {
        cancelAllFutures(futures);
        ((ThreadPoolExecutor)threadExecutor).setCorePoolSize(task.getParallelism());
        updateWaitingList(waitingList);
            try{
            synchronized (condition){
                System.out.println("im going to sleep");
                condition.wait();
            }}catch (Exception e){
                System.out.println("wait or syncronised failled");
            }
            System.out.println("i waked up");
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
        Instant start, end;
        start = Instant.now();
        currentTarget.setRunResult(RunResult.INPROCESS);

        task.updateProcessingTime();
        currentTarget.setFinishResult(task.run());

        updateGraphAfterTaskResult(waitingList, currentTarget);
        end = Instant.now();
        currentTarget.setTaskRunDuration(Duration.between(start, end));

        //Temporary
        GPUPConsumerDTO consumerDTO = new ProcessedTargetDTO(currentTarget);
        consumerDTO.setTaskOutput(new SimulationOutputDTO(task.getProcessingTime()));
        // Writing to console

        print(consumerDTO);
        subTargetGraph.unlockSerialSetOf(currentTarget);
    }

    private synchronized void printLock(Target c) {
        System.out.println(c.getName() + " release The Lock");
    }

    private synchronized void print(GPUPConsumerDTO consumerDTO) {
        System.out.println(consumerDTO);
        System.out.println(Thread.currentThread().getId());
    }

    private synchronized void updateGraphAfterTaskResult(List<Target> waitingList, Target currentTarget) {
        currentTarget.setRunResult(RunResult.FINISHED);
        if (currentTarget.getFinishResult().equals(FinishResult.FAILURE)) {
            subTargetGraph.dfsTravelToUpdateSkippedList(currentTarget);
            subTargetGraph.updateTargetAdjAfterFinishWithFailure(currentTarget);
        } else {
            subTargetGraph.updateTargetAdjAfterFinishWithoutFailure(waitingList, currentTarget);
        }
    }



    @Override
    public void runTask(Consumer<GPUPConsumerDTO> consumer) throws InterruptedException, IOException {
        Instant totalStart, totalEnd, start, end;
        List<Target> waitingList;

        targetGraph.prepareGraphFromProcType(processingType);
        task.updateRelevantTargets(targetGraph.getWaitingAndFrozen());
        targetGraph.clearJustOpenAndSkippedLists();
        waitingList = targetGraph.getAllWaitingTargets();

        totalStart = Instant.now();
        String dirPath = createDirectoryName();
        createTaskDirectory(dirPath);
        task.setDirectoryPath(dirPath);

        if (waitingList.isEmpty() && processingType.equals(ProcessingType.Incremental)) {
            throw new RuntimeException("The graph already had been processed completely, there is no need for 'Incremental' action");
        }

        while (!waitingList.isEmpty()) {
            start = Instant.now();
            Target currentTarget = waitingList.remove(0);
            currentTarget.setRunResult(RunResult.INPROCESS);

            task.updateProcessingTime();
            currentTarget.setFinishResult(task.run());
            currentTarget.setRunResult(RunResult.FINISHED);

            if (currentTarget.getFinishResult() == FinishResult.FAILURE) {
                targetGraph.dfsTravelToUpdateSkippedList(currentTarget);
                targetGraph.updateTargetAdjAfterFinishWithFailure(currentTarget);
            } else {
                targetGraph.updateTargetAdjAfterFinishWithoutFailure(waitingList, currentTarget);
            }
            end = Instant.now();
            currentTarget.setTaskRunDuration(Duration.between(start, end));

            GPUPConsumerDTO consumerDTO = new ProcessedTargetDTO(currentTarget);
            consumerDTO.setTaskOutput(new SimulationOutputDTO(task.getProcessingTime()));
            //Writing to file
            writeTargetToFile(start, end, consumerDTO, task.getDirectoryPath());
            // Writing to console
            consumer.accept(consumerDTO);
        }

        totalEnd = Instant.now();
        Duration totalRunDuration = Duration.between(totalStart, totalEnd);
        StatisticsDTO statisticsDTO = calcStatistics(totalRunDuration);
        consumer.accept(statisticsDTO);
    }

    @Override
    public PathsDTO findPaths(String src, String dest, TargetsRelationType type) {
        if (!src.equals(dest)) {
            if (targetGraph.isTargetExist(src) && targetGraph.isTargetExist(dest)) {
                return new PathsDTO(targetGraph.findPaths(src, type, dest), src, dest, type);
            } else {
                throw new NoSuchElementException("The required targets aren't exist");
            }
        } else {
            throw new RuntimeException("The target you entered are the same.");
        }
    }

    private String createDirectoryName() {
        String path = targetGraph.getWorkingDirectory() + "/";
        String creationTime = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(Calendar.getInstance().getTime());
        path = path + targetGraph.getName() + " - " + creationTime;
        return path;

    }

    public void createTaskDirectory(String path) {
        File taskDirectory = new File(path);
        if (!taskDirectory.exists()) {
            if (!taskDirectory.mkdir()) {
                throw new RuntimeException("Failure with creating the Task's Directory");
            }
        }
    }

    @Override
    public boolean isFirstTaskRun() {
        return task == null;
    }

    @Override
    public CircuitDTO findCircuit(String targetName) {
        if (targetGraph.isTargetExist(targetName)) {
            return new CircuitDTO(targetGraph.findCircuit(targetName));
        } else {
            throw new NoSuchElementException("Target " + targetName + " doesn't exist.");
        }
    }

    @Override
    public List<TargetInfoDTO> getTargetsInfo() {
        return targetGraph.getTargetsInfo();
    }

    private void writeTargetToFile(Instant start, Instant end, GPUPConsumerDTO target, String path) throws IOException {
        String fileName = target.getName() + ".log";
        try (Writer out = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(path + "/" + fileName)))) {

            // write to file:
            out.write(start.toString());
            out.write(target.toString());
            out.write(end.toString());
        }
    }

    private StatisticsDTO calcStatistics(Duration totalRunDuration) {
        return new StatisticsDTO(totalRunDuration, task.getTargetsRunInfo());
    }

    public Set<String> getTargetsNamesList() {
        return targetGraph.getTargetsNamesList();
    }

    @Override
    public List<SerialSetDTO> getSerialSetInfo() {
        return targetGraph.getSerialSetInfo();
    }

    public List<TargetInfoDTO> getTargetsByRelation(String targetName, TargetsRelationType relationType) {
        List<Target> targetsList = targetGraph.getTargetsByRelation(targetName, relationType);
        List<TargetInfoDTO> targetsDTOList = new ArrayList<>();
        targetsList.forEach((target -> {
            targetsDTOList.add(new TargetInfoDTO(target));
        }));
        return targetsDTOList;
    }

    @Override
    public int getMaxParallelism() {
        return maxParallelism;
    }
}
