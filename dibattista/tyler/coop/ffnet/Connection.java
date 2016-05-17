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
    double innovationNum;
    double mutationNum; //TODO: make sure nothing skips this

    public Connection(Neuron in, Neuron out, double weight, double innovation, boolean recurrent, double mutation){
        this.in = in;
        this.out = out;
        this.weight = weight;
        innovationNum = innovation;
        mutationNum = mutation;
        active = true;
        timeDelay = false;
        isRecurrent = recurrent;
    }
    
    public Connection(Connection c, Neuron in, Neuron out){
        this.in = in;
        this.out = out;
        weight = c.weight;
        innovationNum = c.innovationNum;
        mutationNum = c.mutationNum;
        active = c.active;
        timeDelay = false;
        isRecurrent = c.isRecurrent;
        
    }

}