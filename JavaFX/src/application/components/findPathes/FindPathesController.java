package application.components.findPathes;

import application.Controller;
import application.components.app.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

import java.util.List;

public class FindPathesController {
    AppController appController;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    @FXML
    private Label labelFrom;

    @FXML
    private Label labelFromTarget;

    @FXML
    private Label labelTo;

    @FXML
    private Label labelToTarget;

    @FXML
    private CheckBox checkBoxDependsOn;

    @FXML
    private CheckBox checkBoxRequiredFor;

    @FXML
    private TextFlow textFlowPathes;


    @FXML
    private VBox vboxTargets;


    @FXML
    void OnFromTargetSelected(InputMethodEvent event) {

    }

    @FXML
    void checkBoxDependsOnChoosen(ActionEvent event) {
        checkBoxRequiredFor.setSelected(false);
    }

    @FXML
    void checkBoxRequiredForChoosen(ActionEvent event) {
        checkBoxDependsOn.setSelected(false);
    }

    @FXML
    public void initialize() {
      initTargetsButtons();
    }

    private void initTargetsButtons() {
        List<String> targetsList = appController.getTargetsList();

        targetsList.forEach(((targetName)->{
            Button b = new Button(targetName);
            vboxTargets.getChildren().add(b);
        }));
    }

    @FXML
    void onFindPathClicked(ActionEvent event) {
        //
    }

//    private void fixGridSizeIfNeeded(List<String> targetsList) {
//        int targetsNum=targetsList.size();
//        if(gridPaneTargets.getRowConstraints().size()*gridPaneTargets.getColumnConstraints().size()<targetsList.size()){
//            while(gridPaneTargets.getColumnConstraints().size() < targetsNum/3){
//
//            }
//        }
//    }

}
