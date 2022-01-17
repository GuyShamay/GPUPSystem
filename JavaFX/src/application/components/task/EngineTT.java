package application.components.task;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;

public class EngineTT {

    ListTT listTT;

    public Task<Boolean> getCurrTask() {
        return theTask;
    }

    Task<Boolean> theTask;

    public EngineTT() {
        listTT = new ListTT();
    }

    public void initTask() {
        listTT.init();
        theTask = new TaskTT(listTT);
    }

    public void runTaskEngine() {
        new Thread(theTask).start();
    }

    public ObservableList<String> getList(String type) {
        switch (type) {
            case "success":
                return listTT.getProgressData().getSuccess();
            case "frozen":
                return listTT.getProgressData().getFrozen();
            case "waiting":
                return listTT.getProgressData().getWaiting();
        }
        return null;
    }
}
