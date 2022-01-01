package gpup.component.serialset;

import gpup.component.target.Target;

import java.util.ArrayList;
import java.util.List;

public class SerialSet {
    private final String name;

    private List<Target> targets;

    private boolean isLocked;
    public SerialSet(String name) {
        this.name = name;
        isLocked = false;
    }

    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

    public List<Target> getTargets() {
        return targets;
    }
}
