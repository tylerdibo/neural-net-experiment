package dibattista.tyler.coop.ffnet;

/**
 * Created by Tyler on 14/03/2016.
 */
public class Connection {

    Neuron in;
    Neuron out;
    double weight;
    boolean active;
    boolean isRecurrent;
    boolean timeDelay;
    int innovationNum;
    double mutationNum;

    public Connection(Neuron in, Neuron out, double weight, int innovation){
        this.in = in;
        this.out = out;
        this.weight = weight;
        innovationNum = innovation;
        active = true;
    }

}