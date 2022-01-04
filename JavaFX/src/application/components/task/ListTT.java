package application.components.task;

import component.target.RunResult;
import component.task.Task;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ListTT {
    public List<TT> getList() {
        return list;
    }

    List<TT> list;
    private SimpleIntegerProperty counterFinish;

    public ListTT() {
        list = new ArrayList<>();
        list.add(new TT("A"));
        list.add(new TT("B"));
        list.add(new TT("C"));
        list.add(new TT("D"));
        list.add(new TT("E"));
        list.add(new TT("F"));
        list.add(new TT("G"));

        counterFinish = new SimpleIntegerProperty();
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


    public static void RUN() {
        ListTT listTT = new ListTT();

        listTT.runTask();
    }

    public void runTask() {
        Random random = new Random();
        list.forEach(tt -> {
            Task(tt, random.nextFloat());
            if (tt.getRunResult().equals(RunResult.FINISHED)) {
                counterFinish.set(counterFinish.getValue() + 1);
            }
        });
    }

    public static void Task(TT tt, float p) {
        try {
            Thread.sleep(2000);
            tt.setRunResult(p);
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
}
