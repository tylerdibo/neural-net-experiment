package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Genome{
    
    static int innovationNumber = 1;

    public List<Neuron> inputNeurons;
    public List<Neuron> hiddenNeurons;
    public List<Neuron> outputNeurons;
    public List<Connection> links;
    
    int id;
    
    public Genome(){
        inputNeurons = new ArrayList<Neuron>();
        outputNeurons = new ArrayList<Neuron>();
        hiddenNeurons = new ArrayList<Neuron>();
        links = new ArrayList<Connection>();
    }
    
    public List<Double> calculate(){
        List<Double> outputValues = new ArrayList<Double>();
        for(Neuron n : outputNeurons){
            n.calculate();
            outputValues.add(n.getActivatedValue());
        }
        return outputValues;
    }
    
        //TODO: compare with other innovations to check if matching
    public void mAddNeuron(List<Innovation> innovs){
        //take connection and put neuron between
        
        Connection conn = null;
        
        int tries = 0;
        boolean found = false;
        
        if (links.size() < 15){ //TODO: recheck this
            for(Connection c : links){
                if(c.in.type == Neuron.NeuronTypes.BIAS || !c.active || (Math.random() < 0.3)){
                    continue;
                }
                conn = c;
                found = true;
            }
        }else{
            while(tries < 20){
                Connection c = links.get(ThreadLocalRandom.current().nextInt(links.size()));
                if((c.in.type != Neuron.NeuronTypes.BIAS) || !c.active){
                    conn = c;
                    found = true;
                    break;
                }
                
                tries++;
            }
        }
        if(!found)
            return;

        conn.active = false;

        boolean sameInnov = false;
        for(Innovation i : innovs){
            if((i.type == Innovation.InnovType.NEWNODE) && (i.inNodeId == conn.in.getId()) && (i.outNodeId == conn.out.getId()) && (i.oldLinkInnov == conn.innovationNum)){
                sameInnov = true;
                Neuron newNeuron = new Neuron(innovationNumber);
                conn.out.addConnection(newNeuron, conn.weight, innovationNumber); //TODO: more accurate random function?
                newNeuron.addConnection(conn.in, 1.0, innovationNumber); //TODO: maybe make addConnection return the Connection then add that to a list here
                break;
            }
        }
        if(!sameInnov) {
            Neuron newNeuron = new Neuron(innovationNumber);
            conn.out.addConnection(newNeuron, conn.weight, innovationNumber); //TODO: more accurate random function?
            newNeuron.addConnection(conn.in, 1.0, innovationNumber);

        }
    }

    public void mAddConnection(Neuron n1, Neuron n2){
        //add a new connection between two random neurons
        n2.addConnection(n1, Math.random(), innovationNumber); //TODO: determine which is on an inferior layer
    }

    public void mConnectionWeights(Connection conn){
        //modify the weight of a connection
        conn.weight = Math.random();
    }

}