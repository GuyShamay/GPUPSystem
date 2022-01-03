package engine;

import component.target.TargetsRelationType;
import component.task.ProcessingType;
import dto.*;
import exception.TargetExistException;
import dto.*;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface Engine {
    void buildGraphFromXml(String path) throws JAXBException, FileNotFoundException, TargetExistException;
    void buildGraphFromXml(File file) throws JAXBException, FileNotFoundException, TargetExistException;

    TargetDTO getTargetInfo(String name);

    TargetGraphDTO getGraphInfo();

    boolean isInitialized();

    void initTask(int targetProcessingTimeMs, int taskProcessingTimeType, float successProb, float successWithWarningsProb, ProcessingType status);

    void initTaskGPUP1(int targetProcessingTimeMs, int taskProcessingTimeType, float successProb, float successWithWarningsProb, ProcessingType status);

    void setProcessingType(ProcessingType processingStartStatus);

    void runTask(Consumer<GPUPConsumerDTO> consumer) throws InterruptedException, IOException;

    PathsDTO findPaths(String src, String dest, TargetsRelationType type);

    boolean isFirstTaskRun();

    CircuitDTO findCircuit(String targetName);

    List<TargetInfoDTO> getTargetsInfo();

   Set<String> getTargetsNamesList();

    List<SerialSetDTO> getSerialSetInfo();

    List<TargetInfoDTO> getTargetsByRelation(String target,TargetsRelationType relationType);

    int getMaxParallelism();
}
