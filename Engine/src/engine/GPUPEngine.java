package engine;

import component.target.*;
import component.targetgraph.TargetGraph;
import component.task.ProcessingType;
import component.task.Task;
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
import java.util.function.Consumer;


// IMPLEMENT INTERFACE
public class GPUPEngine implements Engine {
    private TargetGraph targetGraph;
    private Task task; // can it run several tasks?
    private ProcessingType processingType;
    private int maxParallelism;

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
    public void initTask(int targetProcessingTimeMs, int taskProcessingTimeType, float successProb, float successWithWarningsProb, ProcessingType status) {
        ProcessingTimeType procTimeType = taskProcessingTimeType == 1 ? ProcessingTimeType.Random : ProcessingTimeType.Permanent;
        task = new SimulationTask(targetGraph.getName(), procTimeType, successProb, successWithWarningsProb, targetProcessingTimeMs);
        targetGraph.buildTransposeGraph();
    }

    @Override
    public void setProcessingType(ProcessingType status) {
        this.processingType = status;
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

    public Set<String> getTargetsNamesList(){
        return targetGraph.getTargetsNamesList();
    }

    @Override
    public List<SerialSetDTO> getSerialSetInfo() {
return targetGraph.getSerialSetInfo();    }

    public List<TargetInfoDTO> getTargetsByRelation (String targetName, TargetsRelationType relationType){
        List<Target> targetsList = targetGraph.getTargetsByRelation(targetName,relationType);
        List<TargetInfoDTO> targetsDTOList = new ArrayList<>();
        targetsList.forEach((target -> {targetsDTOList.add(new TargetInfoDTO(target));}));
        return targetsDTOList;
    }
}
