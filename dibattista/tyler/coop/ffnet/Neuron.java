package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 14/03/2016.
 */
public class Neuron {

    private double value;
    private double activatedValue;
    List<Connection> connections;
    boolean activated = false;

    //initialize a neuron with a list of connections and default value of 0
    public Neuron(){
        connections = new ArrayList<Connection>();
        value = 0;
    }

    //create a connection starting from this neuron to another one with a certain weight
    public void addConnection(Neuron input, double weight){
        connections.add(new Connection(input, this, weight));
    }

    //multiply the value by the weight to add to the connected neurons
    public void calculate(){
        for(Connection c : connections){
            c.in.calculate();
            double a = (c.in.activatedValue * c.weight);
            value += a;
        }
        if (!activated) {
            activate(); // activate
        }
    }

    //sigmoid function
    public void activate(){
        activatedValue = (1/(1+Math.exp(-value)));
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setActivatedValue(double activatedValue) {
        this.activatedValue = activatedValue;
    }

    public double getActivatedValue() {
        return activatedValue;
    }

}
