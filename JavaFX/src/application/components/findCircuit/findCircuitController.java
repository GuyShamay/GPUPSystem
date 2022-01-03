package application.components.findCircuit;

import application.components.app.AppController;
import application.general.Controller;
import dto.CircuitDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class findCircuitController implements Controller {
        private AppController appController;

        @FXML
        private ComboBox<String> comboBoxTargets;

        @FXML
        private Label labelPartOfCircuit;

        @FXML
        private Label labelCircuit;

        @Override
        public void setAppController(Controller c) {
               appController = (AppController) c;
        }

        @FXML
        void buttonFindCircleClicked(ActionEvent event) {
                String msg="";
                if(allDetailsIn()){
                        msg = getCircuitToShow();
                        labelPartOfCircuit.setText("Target " + (msg==null ? "NOT " : "IS ")+ "Belong To Circuit");
                } else {
                        msg= "Missing Details";
                }

                labelCircuit.setText(msg);
        }

        private boolean allDetailsIn() {
                return !comboBoxTargets.getSelectionModel().isEmpty();
        }

        private String getCircuitToShow() {
                CircuitDTO circuitDTO = appController.findCircuit(comboBoxTargets.getValue());
                String circuit = circuitDTO.toString();
                return circuit;
        }

        public void Init(){
                appController.fillComboBoxWithTargets(comboBoxTargets);
        }
}
