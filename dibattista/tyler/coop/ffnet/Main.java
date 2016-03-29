package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;

public class Main {

    //this is a neural network simulating an xor gate

    public static void main(String[] inputs) {

        long startTime = System.nanoTime(); //store starting time to determine total running time

        //initialize input layer neurons
        List<Neuron> inputNeurons = new ArrayList<Neuron>();
        for(int i = 0; i < inputs.length; i++) {
            Neuron neuron = new Neuron(0);
            neuron.setActivatedValue(Double.parseDouble((inputs[i])));
            neuron.activated = true;
            inputNeurons.add(neuron);
        }


        Neuron biasInputNeuron = new Neuron(0); //initialize bias neuron on input layer
        biasInputNeuron.setActivatedValue(1d); //set the neuron to 1
        biasInputNeuron.activated = true;

        //initialize hidden layer of neurons
        List<Neuron> hiddenNeurons = new ArrayList<Neuron>();
        hiddenNeurons.add(new Neuron(0));
        hiddenNeurons.add(new Neuron(0));


        Neuron biasHiddenNeuron = new Neuron(0); //initialize bias neuron on hidden layer
        biasHiddenNeuron.setActivatedValue(1d); //set the neuron to 1
        biasHiddenNeuron.activated = true;

        //initialize output neuron
        Neuron outputNeuron = new Neuron(0);

        /*//add connections from input layer to first hidden neuron
        inputNeurons.get(0).addConnection(hiddenNeurons.get(0), 20d);
        inputNeurons.get(1).addConnection(hiddenNeurons.get(0), 20d);
        biasInputNeuron.addConnection(hiddenNeurons.get(0), -10d);

        //add connections from input layer to second hidden neuron
        inputNeurons.get(0).addConnection(hiddenNeurons.get(1), 20d);
        inputNeurons.get(1).addConnection(hiddenNeurons.get(1), 20d);
        biasInputNeuron.addConnection(hiddenNeurons.get(1), -30d);

        //add connections from hidden layer to output neuron
        hiddenNeurons.get(0).addConnection(outputNeuron, 20d);
        hiddenNeurons.get(1).addConnection(outputNeuron, -20d);
        biasHiddenNeuron.addConnection(outputNeuron, -10d);*/

        hiddenNeurons.get(0).addConnection(inputNeurons.get(0), 20d);
        hiddenNeurons.get(0).addConnection(inputNeurons.get(1), 20d);
        hiddenNeurons.get(0).addConnection(biasInputNeuron, -10d);

        hiddenNeurons.get(1).addConnection(inputNeurons.get(0), 20d);
        hiddenNeurons.get(1).addConnection(inputNeurons.get(1), 20d);
        hiddenNeurons.get(1).addConnection(biasInputNeuron, -30d);

        outputNeuron.addConnection(hiddenNeurons.get(0), 20);
        outputNeuron.addConnection(hiddenNeurons.get(1), -20d);
        outputNeuron.addConnection(biasHiddenNeuron, -10d);

        outputNeuron.calculate();

        //print neuron values
        System.out.println("Input 1: " + inputNeurons.get(0).getActivatedValue());
        System.out.println("Input 1: " + inputNeurons.get(1).getActivatedValue());

        System.out.println("Hidden 1: " + hiddenNeurons.get(0).getActivatedValue());
        System.out.println("Hidden 2: " + hiddenNeurons.get(1).getActivatedValue());

        System.out.println("Output: " + outputNeuron.getActivatedValue());

        System.out.println("Time elapsed in ns: " + (System.nanoTime()-startTime));
    }
}
