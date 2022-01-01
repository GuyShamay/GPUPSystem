package gpup.dto;

import gpup.component.target.TargetType;
import gpup.component.targetgraph.TargetGraph;

public class TargetGraphDTO {
    private final int leaves;
    private final int independent;
    private final int middle;
    private final int root;
    private final int count;

    public TargetGraphDTO(TargetGraph targetGraph) {
        leaves = targetGraph.getSpecificTypeOfTargetsNum(TargetType.Leaf);
        independent = targetGraph.getSpecificTypeOfTargetsNum(TargetType.Independent);
        middle = targetGraph.getSpecificTypeOfTargetsNum(TargetType.Middle);
        root = targetGraph.getSpecificTypeOfTargetsNum(TargetType.Root);
        count = targetGraph.count();
    }

    @Override
    public String toString() {
return "There are "+count +" Target in the system.\n" +
        "Independent Targets: .... " + independent +
        "\nRoot Targets: ........... " + root +
        "\nMiddle Targets: ......... " + middle +
        "\nLeaf Targets: ........... " + leaves;
    }
}
