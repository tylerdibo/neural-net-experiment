package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Tyler on 30/03/2016.
 */
public final class NeuronList {

    private static List<Neuron> allNeurons;

    private NeuronList(){
        allNeurons = new ArrayList<Neuron>();
    }

    public static void addNeuron(int innov, Neuron neuron){
        if(neuron.getId() != innov)
            return;
        allNeurons.add(neuron);
    }
    
    public static void addNeuron(Neuron.NeuronTypes type, int innov){
        allNeurons.add(new Neuron(type, innov));
    }
    
    public static Neuron getNeuron(int id){
        return allNeurons.get(id);
    }
    
    public static int getTotal(){
        return allNeurons.size();
    }
    
    public static Neuron getRandom(){
        return allNeurons.get(ThreadLocalRandom.current().nextInt(allNeurons.size()));
    }

}
