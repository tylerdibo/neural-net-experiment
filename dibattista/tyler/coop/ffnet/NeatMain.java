package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Tyler on 20/03/2016.
 */
public class NeatMain {

    

    public static final double ADD_CONNECTION_CHANCE = 0.1d;
    public static final double ADD_NODE_CHANCE = 0.1d;

    public static void main(String[] inputs) {

        long startTime = System.nanoTime(); //store starting time to determine total running time

        //initialize input layer neurons
        List<Neuron> inputNeurons = new ArrayList<Neuron>();
        for (int i = 0; i < inputs.length; i++) {
            Neuron neuron = new Neuron(Neuron.NeuronTypes.INPUT, 0); //innovationNumber
            neuron.setActivatedValue(Double.parseDouble((inputs[i])));
            neuron.activated = true;
            inputNeurons.add(neuron);
        }


        

        System.out.println("Time elapsed in ns: " + (System.nanoTime()-startTime));

    }

    //TODO: function to decide which mutation and everything else
    /*public void mutate(){
        innovationNumber++;
        double random = Math.random();
        if(random < ADD_CONNECTION_CHANCE){
            mAddConnection(NeuronList.getRandom(), NeuronList.getRandom());
        }else if(random < ADD_CONNECTION_CHANCE + ADD_NODE_CHANCE){
            mAddNeuron(NeuronList.getRandom());
        }else{
            mConnectionWeights();
        }
    }*/


}
