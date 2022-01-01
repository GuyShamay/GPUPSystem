package gpup.engine;

import gpup.component.task.ProcessingType;
import gpup.component.target.TargetsRelationType;
import gpup.dto.*;
import gpup.exception.TargetExistException;
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

    void setProcessingType(ProcessingType processingStartStatus);

    void runTask(Consumer<GPUPConsumerDTO> consumer) throws InterruptedException, IOException;

    PathsDTO findPaths(String src, String dest, TargetsRelationType type);

    boolean isFirstTaskRun();

    CircuitDTO findCircuit(String targetName);

    List<TargetInfoDTO> getTargetsInfo();







   Set<String> getTargetsNamesList();
}
