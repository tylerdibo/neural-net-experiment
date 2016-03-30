package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Tyler on 20/03/2016.
 */
public class NeatMain {

    static int innovationNumber = 0;

    public static final float CONNECTION_CHANCE = 0.1d;

    public static void main(String[] inputs) {

        long startTime = System.nanoTime(); //store starting time to determine total running time

        //initialize input layer neurons
        List<Neuron> inputNeurons = new ArrayList<Neuron>();
        for (int i = 0; i < inputs.length; i++) {
            Neuron neuron = new Neuron(innovationNumber);
            neuron.setActivatedValue(Double.parseDouble((inputs[i])));
            neuron.activated = true;
            inputNeurons.add(neuron);
        }



        System.out.println("Time elapsed in ns: " + (System.nanoTime()-startTime));

    }

    //TODO: function to decide which mutation and everything else
    public void mutate(){
        innovationNumber++;
        double random = Math.random();
        if(random < CONNECTION_CHANCE){
            mAddConnection();
        }
    }

    //TODO: compare with other innovations to check if matching
    public void mAddNeuron(Connection conn){
        //take connection and put neuron between
        conn.active = false;

        Neuron newNeuron = new Neuron(innovationNumber);
        conn.out.addConnection(newNeuron, Math.random()); //TODO: more accurate random function?
        newNeuron.addConnection(conn.in, Math.random());
    }

    public void mAddConnection(Neuron n1, Neuron n2){
        //add a new connection between two random neurons
        n2.addConnection(n1, Math.random()); //TODO: determine which is on an inferior layer
    }

    public void mConnectionWeights(Connection conn){
        //modify the weight of a connection
        conn.weight = Math.random();
    }
}
