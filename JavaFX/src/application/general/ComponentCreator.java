package application.general;

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;

public class ComponentCreator {

    public static Component createComponent(URL url) {
        Component component = new Component();
        try {
            if(url==null){
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" );}
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(url);
            component.setPane(fxmlLoader.load(url.openStream()));
            component.setController((Controller) fxmlLoader.getController());
        } catch (IOException e) {
            System.out.println(e);
        }

        return component;
    }
}
