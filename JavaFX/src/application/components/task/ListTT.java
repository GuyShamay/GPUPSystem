package application.components.task;

import component.progressdata.ProgressData;
import component.target.FinishResult;
import component.target.RunResult;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ListTT { // "target graph"
    public List<TT> getList() {
        return list;
    }

    List<TT> list;
    private SimpleIntegerProperty counterFinish;
    private ProgressData progressData;

    public ListTT() {
        list = new ArrayList<>();
        list.add(new TT("A"));
        list.add(new TT("B"));
        list.add(new TT("C"));
        list.add(new TT("D"));
        list.add(new TT("E"));
        list.add(new TT("F"));
        list.add(new TT("G"));
        progressData = new ProgressData();

        counterFinish = new SimpleIntegerProperty();
    }

    public void init() {
        list.forEach(tt -> progressData.initToFrozen(tt.getName())); // all in frozen
    }

    public int countFinish() {
        AtomicInteger count = new AtomicInteger();
        list.forEach(tt -> {
            if (tt.getRunResult().equals(RunResult.FINISHED)) {
                count.getAndIncrement();
            }
        });
        return count.get();
    }

    public int size() {
        return list.size();
    }


    public void doSomething(TT tt, float p) {
        try {
            Thread.sleep(3000);
            if (p < 0.2) {
                tt.setRunResult(RunResult.WAITING);
                Platform.runLater(() -> progressData.move(RunResult.FROZEN, RunResult.WAITING, tt.getName()));

                System.out.println(tt.getName() + "Waiting");
            } else {
                tt.setRunResult(RunResult.FINISHED);

                Platform.runLater(() -> progressData.move(RunResult.FROZEN, FinishResult.SUCCESS, tt.getName()));
                System.out.println(tt.getName() + "Finish");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getCounterFinish() {
        return counterFinish.get();
    }

    public SimpleIntegerProperty counterFinishProperty() {
        return counterFinish;
    }

    public void setCounterFinish(int counterFinish) {
        this.counterFinish.set(counterFinish);
    }

    public ProgressData getProgressData() {
        return progressData;
    }
}
