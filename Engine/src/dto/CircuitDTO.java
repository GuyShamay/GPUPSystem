package dto;

import java.util.ArrayList;
import java.util.List;

public class CircuitDTO {
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
            str=" No Circle Includes Your Target";

        return str;
    }

    public CircuitDTO(List<String> circuit) {
        this.circuit = circuit;
    }

}
