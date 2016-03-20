package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 20/03/2016.
 */
public class NeatMain {

    static int innovationNumber = 0;

    public static final float CONNECTION_CHANCE = 0.5f;

    public static void main(String[] inputs) {

        long startTime = System.nanoTime(); //store starting time to determine total running time

        //initialize input layer neurons
        List<Neuron> inputNeurons = new ArrayList<Neuron>();
        for (int i = 0; i < inputs.length; i++) {
            Neuron neuron = new Neuron();
            neuron.setActivatedValue(Double.parseDouble((inputs[i])));
            neuron.activated = true;
            inputNeurons.add(neuron);
        }



        System.out.println("Time elapsed in ns: " + (System.nanoTime()-startTime));

    }

    public void mutate(){
        innovationNumber++;
    }
}
