package application.components.findCircuit;

import application.components.app.AppController;
import application.components.findPathes.PathsController;
import application.general.Controller;
import dto.CircuitDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.NoSuchElementException;

public class findCircuitController implements Controller {
    private AppController appController;

    @FXML
    private ComboBox<String> comboBoxTargets;
    @FXML
    private HBox hboxCircuit;
    @FXML
    private Label labelMessage;

    @Override
    public void setParentController(Controller c) {
        appController = (AppController) c;
    }

    @FXML
    void buttonFindCircleClicked(ActionEvent event) {
        labelMessage.setText("");
        hboxCircuit.getChildren().clear();
        if (allDetailsIn()) {
            CircuitDTO circuitDTO = appController.findCircuit(comboBoxTargets.getValue());
            try {
                List<String> list = circuitDTO.getCircuit();
                for (int i = 0; i < list.size(); i++) {
                    Label label;
                    if (i == list.size() - 1) {
                        label = new Label(list.get(i));
                    } else {
                        label = new Label(list.get(i) + " -> ");
                    }
                    label.getStyleClass().add("lblItem");
                    hboxCircuit.getChildren().add(label);
                }
            } catch (NoSuchElementException ex) {
                labelMessage.setText("Target " + comboBoxTargets.getSelectionModel().getSelectedItem() + " isn't part of a circuit.");
            }
        } else {
            labelMessage.setText("Please select a target");
        }
    }

    private boolean allDetailsIn() {
        return !comboBoxTargets.getSelectionModel().isEmpty();
    }

    public void init() {
        appController.fillComboBoxWithTargets(comboBoxTargets);
    }
}
