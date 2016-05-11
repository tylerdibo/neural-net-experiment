package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 14/03/2016.
 */
public class Neuron {

    private int id;
    private double value;
    public double activatedValue;
    public double lastActivatedValue, lastActivatedValue2;
    public int activationCount;

    public enum NeuronTypes{
        INPUT, HIDDEN, OUTPUT, BIAS
    }

    public NeuronTypes type;
    
    List<Connection> connections;
    boolean activated = false;

    //initialize a neuron with a list of connections and default value of 0
    public Neuron(NeuronTypes type, int innovationNumber){
        this.type = type;
        connections = new ArrayList<Connection>();
        value = 0.0;
        activatedValue = 0.0;
        lastActivatedValue = 0.0;
        lastActivatedValue2 = 0.0;
        id = innovationNumber;
        activationCount = 0;
    }

    //create a connection starting from this neuron to another one with a certain weight
    public void addConnection(Neuron input, double weight, int innovationNumber){
        connections.add(new Connection(input, this, weight, innovationNumber));
    }

    public void addConnection(Connection conn){
        connections.add(conn);
    }

    //multiply the value by the weight to add to the connected neurons
    public void calculate(Neuron caller){
        if (!activated) {
            for(Connection c : connections){
                double a;
                if(c.in == caller){ //TODO: make sure this works
                    a = (c.in.lastActivatedValue * c.weight);
                }else {
                    c.in.calculate(this);
                    a = (c.in.activatedValue * c.weight);
                }
                value += a;
            }
            activate(); // activate
        }
    }

    //sigmoid function
    public void activate(){
        lastActivatedValue2 = lastActivatedValue;
        lastActivatedValue = activatedValue;
        activatedValue = (1/(1+Math.exp(-value)));
        activationCount++;
    }
    
    public void reset(){
        if(type != NeuronTypes.INPUT){
            if(activationCount > 0){
                activationCount = 0;
                value = 0.0;
                activatedValue = 0.0;
                lastActivatedValue = 0.0;
                lastActivatedValue2 = 0.0;
                activated = false;
            }
            for(Connection c : connections){
                if(c.in.activationCount > 0)
                    c.in.reset();
            }
        }else{
            activationCount = 0;
            value = 0.0;
            activatedValue = 0.0;
            lastActivatedValue = 0.0;
            lastActivatedValue2 = 0.0;
            activated = false;
        }
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
    
    public int getId(){
        return id;
    }

}
