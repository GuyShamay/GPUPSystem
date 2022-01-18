package application.general;

public interface Controller {
     default void setParentController(Controller c){};
     default void init(){};
}
