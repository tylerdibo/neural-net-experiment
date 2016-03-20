package dibattista.tyler.coop.ffnet;

/**
 * Created by Tyler on 14/03/2016.
 */
public class Connection {

    Neuron in;
    Neuron out;
    double weight;

    public Connection(Neuron in, Neuron out, double weight){
        this.in = in;
        this.out = out;
        this.weight = weight;
    }

}