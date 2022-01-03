package main.app;

import component.task.ProcessingType;
import component.target.TargetsRelationType;
import dto.CircuitDTO;
import dto.PathsDTO;
import dto.TargetDTO;
import engine.Engine;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

public class GPUPApp {

    private Engine engine;

    public void init(Engine engine) {
        this.engine = engine;
    }

    public void run() {
        UserInput userInput = UserInput.INIT;
        GPUPConsoleIO.welcome();

        while (userInput != UserInput.QUIT) {
            userInput = GPUPConsoleIO.mainMenu(); // show system menu & getting user input
            if (engine.isInitialized() || userInput.equals(UserInput.LOAD) || userInput.equals(UserInput.QUIT)) {
                switch (userInput) {
                    case LOAD:
                        loadGPUPSystem();
                        break;
                    case GRAPHINFO:
                        showTargetsGraphInfo();
                        break;
                    case TARGETINFO:
                        showTargetInfo();
                        break;
                    case PATH:
                        findPaths();
                        break;
                    case TASK:
                        runTask();
                        break;
                    case CIRCUIT:
                        findCircuit();
                        break;
                    case QUIT:
                        userInput = GPUPConsoleIO.exit();
                        break;
                }
            } else {
                GPUPConsoleIO.unloadedSystem();
            }
            if (!userInput.equals(UserInput.QUIT)) {
                GPUPConsoleIO.continueApp();
            }
        }
        GPUPConsoleIO.printMsg("Goodbye!");
    }

    private void findCircuit() {
        GPUPConsoleIO.printMsg("Please enter a target name to see if it is part of a Circuit: ");
        String name = GPUPConsoleIO.getStringInput("name");
        if (!GPUPConsoleIO.isQuit(name)) {
            try {
                CircuitDTO circuit = engine.findCircuit(name);
                GPUPConsoleIO.printMsg(circuit.toString());
            } catch (NoSuchElementException e) {
                GPUPConsoleIO.printMsg(e.getMessage());
            }
        }
    }

    private void findPaths() {
        GPUPConsoleIO.printMsg("Please enter 2 targets by name\nFirst target (from):");
        String src = GPUPConsoleIO.getStringInput("name");
        if (!GPUPConsoleIO.isQuit(src)) {
            GPUPConsoleIO.printMsg("Second target (To):");
            String dest = GPUPConsoleIO.getStringInput("name");
            if (!GPUPConsoleIO.isQuit(dest)) {
                TargetsRelationType type = getRelationType();
                if (type != null) {
                    try {
                        PathsDTO paths = engine.findPaths(src, dest, type);
                        GPUPConsoleIO.printMsg(paths.toString());
                    } catch (RuntimeException e) {
                        GPUPConsoleIO.printMsg(e.getMessage());
                    }
                }
            }
        }
    }

    private TargetsRelationType getRelationType() {
        TargetsRelationType type;
        int choice = GPUPConsoleIO.getTwoOptionsResult("Dependency between the targets", "Depend On", "Required For");
        switch (choice) {
            case 0:
                return null;
            case 1:
                type = TargetsRelationType.DependsOn;
                break;
            case 2:
                type = TargetsRelationType.RequiredFor;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + choice);
        }
        return type;
    }

    private void loadGPUPSystem() {
        String path = GPUPConsoleIO.getXmlPath();
        if (path == null) {
            GPUPConsoleIO.failedLoadSystem("The file path contains invalid letters.");
            return;
        }
        if (!GPUPConsoleIO.isQuit(path)) {
            try {
                if (!Files.exists(Paths.get(path))) {
                    GPUPConsoleIO.failedLoadSystem("The File you try to load is not exist.");
                    return;
                } else if (!Files.probeContentType(Paths.get(path)).equals("text/xml")) {
                    GPUPConsoleIO.failedLoadSystem("The File you entered is not an xml file.");
                    return;
                } else {
                    engine.buildGraphFromXml(path);
                    GPUPConsoleIO.successLoading();
                }
            } catch (JAXBException ex) {
                GPUPConsoleIO.failedLoadSystem("The xml file you try to load is not valid for this GPUP system, try again.");
            } catch (FileNotFoundException ex) {
                GPUPConsoleIO.failedLoadSystem("Wrong file path - the file you entered is not exist, try again.");
            } catch (Exception e) {
                GPUPConsoleIO.failedLoadSystem(e.getMessage());
            }
        }
    }

    private void showTargetInfo() {
        boolean targetExist = false;
        do {
            GPUPConsoleIO.targetNameRequest();
            String name = GPUPConsoleIO.getStringInput("name");
            if (GPUPConsoleIO.isQuit(name)) {
                break;
            }
            try {
                TargetDTO targetDTO = engine.getTargetInfo(name);
                GPUPConsoleIO.printMsg(targetDTO.toString());
                targetExist = true;
            } catch (NoSuchElementException e) {
                GPUPConsoleIO.printMsg(e.getMessage());
            }
        } while (!targetExist);
    }

    private void showTargetsGraphInfo() {
        GPUPConsoleIO.printMsg(engine.getGraphInfo().toString());
    }

    private void runTask() {
        int targetProcessingTime = GPUPConsoleIO.getProcessingTime();
        int taskProcessingTimeType = GPUPConsoleIO.getTwoOptionsResult("Do you want Processing Time will be :", "Random (limited by the processing time you entered)", "Excactly the processing time you entered");
        float successProb = GPUPConsoleIO.getProbability("Enter the probability of single target run task, to complete with SUCCESS");
        float successWithWarningsProb = GPUPConsoleIO.getProbability("Assuming the task completed with SUCCUES, Enter the probability it will be WITH WARNINGS");
        int howToStart = GPUPConsoleIO.getTwoOptionsResult("Do you want to start processing :", "From Scratch", "Incremental (only non-succeeded targets)");
        ProcessingType procStartStatus = howToStart == 1 ? ProcessingType.FromScratch : ProcessingType.Incremental;

        if ((procStartStatus.equals(ProcessingType.Incremental) && engine.isFirstTaskRun())) {
            GPUPConsoleIO.printMsg("The task will run 'From Scratch' even though you choose 'Incremental'.\nBecause this is the first run.\n");
            procStartStatus = ProcessingType.FromScratch;
        }

        engine.initTaskGPUP1(targetProcessingTime, taskProcessingTimeType, successProb, successWithWarningsProb, procStartStatus);
        engine.setProcessingType(procStartStatus);

        try {
            engine.runTask(new ConsoleConsumer());

        } catch (InterruptedException e) {
            GPUPConsoleIO.printMsg("There have been a problem with 'Thread.sleep' function.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            GPUPConsoleIO.printMsg(e.getMessage());
        }
    }
}

