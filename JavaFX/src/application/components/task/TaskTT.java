package application.components.task;

import component.target.RunResult;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;

import java.util.List;
import java.util.Random;

public class TaskTT extends Task<Boolean> {

    ListTT list;
    private SimpleIntegerProperty progress;

    public TaskTT(ListTT list) {
        progress = new SimpleIntegerProperty();
        this.list = list;
    }

    @Override
    protected Boolean call() throws Exception {
        Random random = new Random();
        updateMessage("Starting TaskTT");
        updateProgress(0, list.size());

       /* list.counterFinishProperty().addListener(((observable, oldValue, newValue) -> {
            updateProgress(0, newValue.intValue());

        }));
        list.runTask();*/

        list.getList().forEach(tt -> {
            ListTT.Task(tt, random.nextFloat());
            if(tt.getRunResult().equals(RunResult.FINISHED)){
                updateProgress(list.countFinish(), list.size());
            }
        });
        updateMessage("Finish");
        return true;
    }
}
