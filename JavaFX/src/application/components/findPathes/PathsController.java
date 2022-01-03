package application.components.findPathes;

import application.components.app.AppController;
import application.general.Controller;
import component.target.TargetsRelationType;
import dto.PathsDTO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Set;

public class PathsController implements Controller {

    private AppController appController;

    @FXML
    private Label labelFrom;

    @FXML
    private Label labelTo;

    @FXML
    private CheckBox checkBoxDependsOn;

    @FXML
    private CheckBox checkBoxRequiredFor;

    @FXML
    private TextFlow textFlowPaths;

    @FXML
    private Text textPaths;

    @FXML
    private ChoiceBox<String> choiseBoxFrom;

    @FXML
    private ChoiceBox<String> choiseBoxTo;

    @FXML
    void checkBoxDependsOnChoosen(ActionEvent event) {checkBoxRequiredFor.setSelected(false); }

    @FXML
    void checkBoxRequiredForChoosen(ActionEvent event) {checkBoxDependsOn.setSelected(false); }

    @FXML
    void onFindPathClicked(ActionEvent event) {
        textFlowPaths.getChildren().clear();
        TargetsRelationType relationType = checkBoxDependsOn.isSelected() == true ? TargetsRelationType.DependsOn : TargetsRelationType.RequiredFor;
        PathsDTO paths = appController.getFoundPaths(choiseBoxFrom.getValue(),choiseBoxTo.getValue(),relationType);
        textFlowPaths.getChildren().add(new Text(paths.toString().substring(paths.toString().indexOf(":")+1)));
    }

    public void Init(){
        initTargetsToChoose();
        labelFrom.setStyle("-fx-font-weight: bold");
        choiseBoxFrom.setOnAction((event -> {
            labelTo.setStyle("-fx-font-weight: bold");
            labelFrom.setStyle("-fx-font-weight: regular");
        }));
        choiseBoxTo.setOnAction(event -> {
            labelFrom.setStyle("-fx-font-weight: bold");
            labelTo.setStyle("-fx-font-weight: regular");
        });
    }

    private void initTargetsToChoose() {
        Set<String> targets = appController.getTargetsListByName();
        ObservableList<String> fromList = choiseBoxFrom.getItems();
        fromList.addAll(targets);
        choiseBoxTo.getItems().addAll(fromList);
    }

    public void setAppController(Controller c){
        appController = (AppController)c;
    }
}
