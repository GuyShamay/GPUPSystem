package application.general;

public interface Controller {
     default void setAppController(Controller c){};
     default void Init(){};
}
