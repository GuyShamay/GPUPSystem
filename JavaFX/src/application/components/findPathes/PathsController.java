package application.components.findPathes;

import application.components.app.AppController;
import application.general.Controller;
import component.target.TargetsRelationType;
import dto.PathsDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.NoSuchElementException;

public class PathsController implements Controller {

    private AppController appController;

    @FXML
    private CheckBox checkBoxDependsOn;

    @FXML
    private CheckBox checkBoxRequiredFor;

    @FXML
    private ComboBox<String> comboBoxFrom;

    @FXML
    private ComboBox<String> comboBoxTo;

    @FXML
    private VBox vboxPath;

    @FXML
    private Label labelMessage;

    @FXML
    void checkBoxDependsOnChosen(ActionEvent event) {
        checkBoxRequiredFor.setSelected(!checkBoxDependsOn.isSelected());
    }

    @FXML
    void checkBoxRequiredForChosen(ActionEvent event) {
        checkBoxDependsOn.setSelected(!checkBoxRequiredFor.isSelected());
    }

    @FXML
    public void initialize() {

    }

    @FXML
    void buttonFindPathClicked(ActionEvent event) {
        labelMessage.setText("");
        vboxPath.getChildren().clear();
        if (checkBoxDependsOn.isSelected() || checkBoxRequiredFor.isSelected()) {
            if (isValidFromToChoose()) {
                TargetsRelationType relationType = checkBoxDependsOn.isSelected() ? TargetsRelationType.DependsOn : TargetsRelationType.RequiredFor;
                try {
                    PathsDTO paths = appController.getFoundPaths(comboBoxFrom.getValue(), comboBoxTo.getValue(), relationType);
                    List<String> list = paths.getPaths();
                    for (String s : list) {
                        Label label = new Label(s);
                        label.getStyleClass().add("lblItem");
                        vboxPath.getChildren().add(label);
                    }
                } catch (NoSuchElementException ex) {
                    labelMessage.setText("There isn't a path from target " + comboBoxFrom.getValue() + " to target " + comboBoxTo.getValue());
                } catch (RuntimeException e) {
                    labelMessage.setText(e.getMessage());
                }
            } else {
                labelMessage.setText("Please select target (From, To)");
            }
        } else {
            labelMessage.setText("Please select relation");
        }
    }

    public static void addPathsToVBox(List<String> list, VBox vbox) {

    }

    private boolean isValidFromToChoose() {
        return (!comboBoxFrom.getSelectionModel().isEmpty() &&
                !comboBoxTo.getSelectionModel().isEmpty());
    }

    public void init() {
        initTargetsToChoose();
    }

    private void initTargetsToChoose() {
        appController.fillComboBoxWithTargets(comboBoxTo);
        appController.fillComboBoxWithTargets(comboBoxFrom);
    }

    @Override
    public void setParentController(Controller c) {
        appController = (AppController) c;
    }
}
