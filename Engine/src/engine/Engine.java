package engine;

import component.target.TargetsRelationType;
import component.task.ProcessingType;
import component.task.config.TaskConfig;
import dto.*;
import exception.ElementExistException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

public interface Engine {
    void buildGraphFromXml(String path) throws JAXBException, FileNotFoundException, ElementExistException;

    void buildGraphFromXml(File file) throws JAXBException, FileNotFoundException, ElementExistException;

    TargetDTO getTargetInfo(String name);

    TargetGraphDTO getGraphInfo();

    boolean isInitialized();

    void initTask(TaskConfig taskConfig);

    void initTaskGPUP1(int targetProcessingTimeMs, int taskProcessingTimeType, float successProb, float successWithWarningsProb, ProcessingType status);

    void setProcessingType(ProcessingType processingStartStatus);

    PathsDTO findPaths(String src, String dest, TargetsRelationType type);

    boolean isFirstTaskRun();

    CircuitDTO findCircuit(String targetName);

    List<TargetInfoDTO> getTargetsInfo();

    Set<String> getTargetsNamesList();

    List<SerialSetDTO> getSerialSetInfo();

    List<TargetInfoDTO> getTargetsByRelation(String target, TargetsRelationType relationType);

    int getMaxParallelism();

    void runTaskGPUP2() throws InterruptedException;

    void resume();

    void pause();

    boolean isRunPaused();

    void increaseThreadsNum(Integer newVal);

    boolean isCircuit();
}
