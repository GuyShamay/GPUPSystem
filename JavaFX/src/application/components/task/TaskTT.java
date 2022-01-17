package application.components.task;

import component.target.FinishResult;
import component.target.RunResult;
import component.target.Target;
import component.task.results.TaskResults;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskTT extends Task<Boolean> {

    ListTT list;

    public Task<Boolean> getTaskResults() {
        return taskResults;
    }

    TaskResults taskResults;

    public TaskTT(ListTT list) {
        this.list = list;

//        creating static data
//        List<Target> ts = new ArrayList<>();
//        Target t1 = new Target("A");
//        t1.setFinishResult(FinishResult.SUCCESS);
//        ts.add(t1);
//        Target t2 = new Target("A");
//        ts.add(t2);
//        Target t3 = new Target("A");
//        t3.setFinishResult(FinishResult.SUCCESS);
//        ts.add(t3);
        // taskResults = new TaskResults(ts);
    }

    @Override
    protected Boolean call() throws Exception {
        Random random = new Random();
        updateMessage("Starting TaskTT");
        updateProgress(0, list.size());


        list.getList().forEach(tt -> {
            list.doSomething(tt, random.nextFloat());
            if (tt.getRunResult().equals(RunResult.FINISHED)) {
                updateProgress(list.countFinish(), list.size());
            }
        });
        updateMessage("Finish");
        //new Thread(taskResults).start();
        return true;
    }
}
