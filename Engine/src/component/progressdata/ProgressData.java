package component.progressdata;

import component.target.FinishResult;
import component.target.Result;
import component.target.RunResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class ProgressData {
    private ObservableList<String> success;
    private ObservableList<String> frozen;
    private ObservableList<String> waiting;

    public ProgressData() {
        success = FXCollections.observableArrayList();
        frozen = FXCollections.observableArrayList();
        waiting = FXCollections.observableArrayList();
    }

    public ObservableList<String> getFrozen() {
        return frozen;
    }

    public ObservableList<String> getWaiting() {
        return waiting;
    }

    public ObservableList<String> getSuccess() {
        return success;
    }

    //  add getters for all lists

    public void initOne(String name) {
        frozen.add(name);
    }

    public void move(Result from, Result to, String name) {

        switch ((RunResult) from) {
            case FROZEN:
                remove(frozen, name);
                break;
            case WAITING:
                remove(waiting, name);
                break;
            case INPROCESS:
                // add
                break;
        }
        if (to instanceof RunResult) {
            switch ((RunResult) to) {
                case SKIPPED:
                    // add
                    break;
                case WAITING:
                    waiting.add(name);
                    break;
                case INPROCESS:
                    // add
                    break;
            }
        } else {
            switch ((FinishResult) to) {
                case SUCCESS:
                    success.add(name);
                    break;
                case WARNING:
                    // add
                    break;
                case FAILURE:
                    // add
                    break;
            }
        }
    }

    private void remove(ObservableList<String> list, String name) {
        int index = list.indexOf(name);
        list.remove(index);
    }

}
