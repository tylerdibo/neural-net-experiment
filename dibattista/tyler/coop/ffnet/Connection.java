package dibattista.tyler.coop.ffnet;

/**
 * Created by Tyler on 14/03/2016.
 */
public class Connection {

    Neuron in;
    Neuron out;
    double weight;
    boolean active;
    int innovation;

    public Connection(Neuron in, Neuron out, double weight, int innovation){
        this.in = in;
        this.out = out;
        this.weight = weight;
        this.innovation = innovation;
        active = true;
    }

}