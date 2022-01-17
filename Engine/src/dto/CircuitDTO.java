package dto;

import java.util.ArrayList;
import java.util.List;

public class CircuitDTO {
    public List<String> getCircuit() {
        return circuit;
    }

    private List<String> circuit;

    @Override
    public String toString() {
        String str="";

        if(circuit!=null){
            for (String s : circuit) {
                str += s + " --> ";
            }
            str = str.substring(0,str.length()-4);
        }
        else
            str=null;

        return str;
    }

    public CircuitDTO(List<String> circuit) {
        this.circuit = circuit;
    }

}
