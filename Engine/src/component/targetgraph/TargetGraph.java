package component.targetgraph;

import component.serialset.SerialSet;
import component.target.*;
import component.target.*;
import component.task.ProcessingType;
import dto.SerialSetDTO;
import dto.TargetDTO;
import dto.TargetInfoDTO;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class TargetGraph implements DirectableGraph, GraphActions {

    private final String name;
    private String workingDirectory;
    private Map<String, List<Target>> dependsOnGraph;
    private Map<String, Target> targetMap;
    private Map<String, List<Target>> gTranspose;

    private Map<String, SerialSet> serialSets;

    public TargetGraph(String name) {
        this.name = name;
        dependsOnGraph = new HashMap<>();
        targetMap = new HashMap<>();
    }

    public SerialSet getSerialSet(String name) {
        return serialSets.get(name);
    }

    public void setSerialSets(Map<String, SerialSet> serialSets) {
        this.serialSets = serialSets;
        serialSets.forEach((s, serialSet) -> {
            serialSet.getTargets().forEach(Target::incrementSerialSetCounter);
        });
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getName() {
        return name;
    }

    public void buildGraph(Map<String, Target> targetList) {
        targetList.values().forEach(target -> {
            dependsOnGraph.put(target.getName(), new LinkedList<>());
            targetMap.put(target.getName(), target);
        });
        targetMap.values().forEach(target -> target.getDependsOnList().forEach(target1 -> addEdge(target.getName(), target1)));
        updateLeavesAndIndependentsToWaiting();
    }

    @Override
    // This function return the count of targets
    public int count() {
        return targetMap.size();
    }

    @Override
    public void addEdge(String source, Target destination) {
        if (dependsOnGraph.containsKey(source)) {

            //if the dest isn't in the graph already. so we add it
            if (!dependsOnGraph.containsKey(destination.getName()))
                addTarget(destination);

            // adding the destination to the adjacency list of the source
            dependsOnGraph.get(source).add(destination);
        }
    }

    @Override
    public void addTarget(Target target) {
        dependsOnGraph.put(target.getName(), new LinkedList<>());
        targetMap.put(target.getName(), target);
    }

    @Override
    public boolean isDependOn(String s, String d) {
        return dependsOnGraph.get(s).stream().anyMatch(target -> target.getName().equals(d));
    }

    @Override
    public boolean isTargetExist(String t) {
        return targetMap.containsKey(t);
    }

    @Override
    public int getEdgesCount() {
        AtomicInteger count = new AtomicInteger();
        dependsOnGraph.values().forEach(list -> list.forEach(target -> count.getAndIncrement()));
        return count.get();
    }

    @Override
    public List<String> findPaths(String src, TargetsRelationType type, String dest) {
        List<String> paths = new ArrayList<>();
        Map<String, Boolean> isVisited = new HashMap<>();
        List<String> pathList = new ArrayList<>();

        targetMap.forEach((s, target) -> isVisited.put(s, false));

        if (type.equals(TargetsRelationType.RequiredFor)) {
            String tmp = src;
            src = dest;
            dest = tmp;
        }

        pathList.add(src);
        recFindPath(src, dest, isVisited, pathList, paths);
        if (type.equals(TargetsRelationType.RequiredFor)) {
            reversePathsList(paths);
        }

        return paths;
    }

    private void reversePathsList(List<String> paths) {

        StringBuilder str = new StringBuilder();

        for (int i = 0; i < paths.size(); i++) {
            str.append(paths.get(i), 1, paths.get(i).length() - 1);
            str.reverse();
            paths.remove(i);
            paths.add(i, str.toString());
            str.setLength(0);
        }
    }

    private void recFindPath(String src, String dest, Map<String, Boolean> isVisited, List<String> localPath, List<String> paths) {
        if (src.equals(dest)) {
            paths.add(localPath.toString());
            return;
        }
        isVisited.put(src, true);
        for (Target t : dependsOnGraph.get(src)) {

            if (!isVisited.get(t.getName())) {
                localPath.add(t.getName());
                recFindPath(t.getName(), dest, isVisited, localPath, paths);
                localPath.remove(t.getName());
            }
        }
        isVisited.put(src, false);
    }

    public int getSpecificTypeOfTargetsNum(TargetType targetType) {
        return (int) targetMap.values()
                .stream()
                .filter(target -> target.getType() == targetType)
                .count();
    }

    public TargetDTO getTargetInfo(String name) {
        if (targetMap.containsKey(name)) {
            return new TargetDTO(targetMap.get(name));
        } else {
            throw new NoSuchElementException("There is not a target named: " + name);
        }
    }

    @Override
    public void buildTransposeGraph() {
        if (gTranspose == null) {
            gTranspose = new HashMap<>();
            if (dependsOnGraph.size() != 0) {
                targetMap.forEach(((s, target) -> gTranspose.put(s, new ArrayList<>())));
                dependsOnGraph.forEach((s, targets) -> targets.forEach(target -> gTranspose.get(target.getName()).add(targetMap.get(s))));
            }
        }
    }

    @Override
    public List<String> findCircuit(String src) {
        Map<Target, Boolean> isVisited = new HashMap<>();
        List<String> circuitList = new ArrayList<>();

        targetMap.forEach((s, target) -> isVisited.put(target, false));
        if (recDfsFindCircuitWithGivenTarget(isVisited, circuitList, targetMap.get(src), targetMap.get(src), false)) {
            return circuitList;
        }

        return null;
    }

    public List<Target> getAllWaitingTargets() {
        List<Target> resList = new ArrayList<>();
        targetMap.forEach(((s, target) -> {
            if (target.getRunResult().equals(RunResult.WAITING)) {
                resList.add(target);
            }
        }));
        return resList;
    }

    public void updateLeavesAndIndependentsToWaiting() {
        targetMap.forEach(((s, target) -> {
            if (target.getType() == TargetType.Leaf || target.getType() == TargetType.Independent) {
                target.setRunResult(RunResult.WAITING);
            }
        }));
    }

    public void updateTargetAdjAfterFinishWithoutFailure(List<Target> waitingList, Target currentTarget) {
        gTranspose.get(currentTarget.getName()).forEach(target -> {
            if (target.isAllAdjFinished())
                currentTarget.addToJustOpenedList(target);
            if (target.isAllAdjFinishedWithoutFailure()) {
                target.setRunResult(RunResult.WAITING);
                if (!waitingList.contains(target)) {
                    waitingList.add(target);
                }
            }
        });
    }

    public void updateTargetAdjAfterFinishWithFailure(Target currentTarget) {
        gTranspose.get(currentTarget.getName()).forEach(target -> {
            // target.setRunResult(RunResult.SKIPPED);
            if (target.isAllAdjFinished()) {
                currentTarget.addToJustOpenedList(target);
            }
        });
    }

    public void dfsTravelToUpdateSkippedList(Target currentTarget) {

        Map<Target, Boolean> isVisited = new HashMap<>();
        targetMap.forEach(((s, target) -> isVisited.put(target, false)));
        List<Target> skippedList = currentTarget.getSkippedList();
        recDfsUpdateSkippedList(isVisited, skippedList, currentTarget);
        skippedList.remove(currentTarget);
        skippedList.forEach((target -> target.setRunResult(RunResult.SKIPPED)));
    }

    private void recDfsUpdateSkippedList(Map<Target, Boolean> isVisited, List<Target> skippedList, Target
            currentTarget) {
        skippedList.add(currentTarget);

        for (Target t : gTranspose.get(currentTarget.getName())) {
            if (!isVisited.get(t)) {
                recDfsUpdateSkippedList(isVisited, skippedList, t);
            }
        }
        isVisited.replace(currentTarget, false, true);
    }

    private void updateTargetsFromScratch() {
        targetMap.forEach(((s, target) -> {
            target.setFinishResult(null);
            target.setRunResult(RunResult.FROZEN);
        }));
    }

    private void updateTargetIncremental() {
        targetMap.forEach(((s, target) -> {
            if (target.getRunResult().equals(RunResult.FINISHED)) {
                if (target.getFinishResult().equals(FinishResult.FAILURE)) {
                    target.setFinishResult(null);
                    target.setRunResult(RunResult.WAITING);
                }
            }
            if (target.getRunResult().equals(RunResult.SKIPPED))
                target.setRunResult(RunResult.FROZEN);
        }));
    }

    public void prepareGraphFromProcType(ProcessingType processingType) {
        switch (processingType) {
            case FromScratch:
                updateTargetsFromScratch();
                updateLeavesAndIndependentsToWaiting();
                break;
            case Incremental:
                updateTargetIncremental();
                break;
        }
    }

    public void clearJustOpenAndSkippedLists() {
        targetMap.forEach(((s, target) -> target.clearHelpingLists()));
    }

    private Boolean recDfsFindCircuitWithGivenTarget(Map<Target, Boolean> isVisited, List<String> circuitList, Target currentTarget, Target src, Boolean foundCirc) {
        circuitList.add(currentTarget.getName());
        isVisited.replace(currentTarget, false, true);

        for (Target t : dependsOnGraph.get(currentTarget.getName())) {
            if (t.equals(src)) {
                circuitList.add(src.getName());
                return true;
            }
            if (!isVisited.get(t)) {
                return recDfsFindCircuitWithGivenTarget(isVisited, circuitList, t, src, foundCirc);
            }
        }
        circuitList.remove(currentTarget.getName());
        return false;
    }

    public List<Target> getWaitingAndFrozen() {
        List<Target> waitingFrozen = new ArrayList<>();
        targetMap.forEach(((s, target) -> {
            if (target.getRunResult().equals(RunResult.WAITING) || target.getRunResult().equals(RunResult.FROZEN)) {
                waitingFrozen.add(target);
            }
        }));

        return waitingFrozen;
    }

    public List<TargetInfoDTO> getTargetsInfo() {
        List<TargetInfoDTO> list = new ArrayList<>();
        targetMap.forEach((s, target) -> {
            list.add(new TargetInfoDTO(target));
        });
        return list;
    }

    public Set<String> getTargetsNamesList() {
        return targetMap.keySet();
    }

    public List<SerialSetDTO> getSerialSetInfo() {
        List<SerialSetDTO> list = new ArrayList<>();
        serialSets.forEach((s, serialSet) -> {
            list.add(new SerialSetDTO(serialSet));
        });
        return list;
    }
}


