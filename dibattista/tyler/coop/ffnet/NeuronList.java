package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 30/03/2016.
 */
public final class NeuronList {

    private static List<Neuron> allNeurons;

    private NeuronList(){
        allNeurons = new ArrayList<Neuron>();
    }

    public static void addNeuron(int innov){
        allNeurons.add(new Neuron(innov));
    }
    
    public static Neuron getNeuron(int id){
        return allNeurons.get(id);
    }
    
    public static int getTotal(){
        return allNeurons.size();
    }
    
    public static Neuron getRandom(){
        return allNeurons.get(Random.nextInt(allNeurons.size()));
    }

}
