package application.components.task;

import javafx.concurrent.Task;

public class EngineTT {

    ListTT listTT;

    public Task<Boolean> getCurrTask() {
        return currTask;
    }

    Task<Boolean> currTask;

    public EngineTT() {
        listTT = new ListTT();
        currTask = new TaskTT(listTT);
    }

    public void runTaskEngine() {
        new Thread(currTask).start();
    }
}
