package dto;

import component.target.Target;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TargetInfoDTO {


    private final SimpleIntegerProperty dependsOn;
    private final SimpleIntegerProperty requiredFor;
    private final SimpleIntegerProperty serialSets;
    private final SimpleStringProperty name;
    private final SimpleStringProperty data;
    private final SimpleStringProperty type;
//    private final int dependsOn;
//    private final int requiredFor;
//    private final int serialSets;
//    private final String name;
//    private final String data;
//    private final String type;

    public TargetInfoDTO(Target target) {
        data = new SimpleStringProperty(target.getUserData());
        name = new SimpleStringProperty(target.getName());
        type = new SimpleStringProperty(target.getType().toString());
        dependsOn = new SimpleIntegerProperty(target.getDependsOnList().size());
        requiredFor = new SimpleIntegerProperty(target.getRequiredForList().size());
        serialSets = new SimpleIntegerProperty(target.getSerialSetCounter());

//        data = target.getUserData();
//        name = target.getName();
//        type = target.getType().toString();
//        dependsOn = target.getDependsOnList().size();
//        requiredFor = target.getRequiredForList().size();
//        serialSets = target.getSerialSetCounter();
    }

    public int getDependsOn() {
        return dependsOn.get();
    }

    public SimpleIntegerProperty dependsOnProperty() {
        return dependsOn;
    }

    public int getRequiredFor() {
        return requiredFor.get();
    }

    public SimpleIntegerProperty requiredForProperty() {
        return requiredFor;
    }

    public int getSerialSets() {
        return serialSets.get();
    }

    public SimpleIntegerProperty serialSetsProperty() {
        return serialSets;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getData() {
        return data.get();
    }

    public SimpleStringProperty dataProperty() {
        return data;
    }

    public String getType() {
        return type.get();
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }


}
