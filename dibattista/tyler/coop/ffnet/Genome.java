package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Genome{

    static final int MAX_ADD_NEURON_TRIES = 20;
    static final double ADD_NEURON_OLD_BIAS = 0.3;
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
    
    public void mAddNeuron(List<Innovation> innovs){
        //take connection and put neuron between
        
        Connection conn = null;
        
        int tries = 0;
        boolean found = false;
        
        if (links.size() < 15){
            for(Connection c : links){
                if(c.in.type != Neuron.NeuronTypes.BIAS && c.active && (Math.random() > ADD_NEURON_OLD_BIAS)) { //TODO: more accurate random function?
                    conn = c;
                    found = true;
                    break;
                }
            }
        }else{
            while(tries < MAX_ADD_NEURON_TRIES){
                Connection c = links.get(ThreadLocalRandom.current().nextInt(links.size()));
                if((c.in.type != Neuron.NeuronTypes.BIAS) && c.active){
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
        for(Innovation i : innovs){ //TODO: maybe create the connections after, and just log when its the same innov
            if((i.type == Innovation.InnovType.NEWNODE) && (i.inNodeId == conn.in.getId()) && (i.outNodeId == conn.out.getId()) && (i.oldLinkInnov == conn.innovationNum)){
                sameInnov = true;

                Neuron newNeuron = new Neuron(innovationNumber);

                Connection connNewToOut = new Connection(newNeuron, conn.out, conn.weight, i.innovNum2);
                links.add(connNewToOut);
                conn.out.addConnection(connNewToOut);

                Connection connInToNew = new Connection(conn.in, newNeuron, 1.0, i.innovNum1);
                links.add(connInToNew);
                newNeuron.addConnection(connInToNew);
                
                hiddenNeurons.add(newNeuron);
                break;
            }
        }
        if(!sameInnov) {
            Neuron newNeuron = new Neuron(innovationNumber);
            
            Connection connNewToOut = new Connection(newNeuron, conn.out, conn.weight, innovationNumber);
            links.add(connNewToOut);
            conn.out.addConnection(connNewToOut);

            Connection connInToNew = new Connection(conn.in, newNeuron, 1.0, innovationNumber);
            links.add(connInToNew);
            newNeuron.addConnection(connInToNew);
            
            hiddenNeurons.add(newNeuron);
        }
    }

    public void mAddConnection(List<Innovation> innovs, int tries){
        //add a new connection between two random neurons
        int nodeId1, nodeId2;
        
        for(int i = 0; i < tries; i++){
            nodeId1 = ThreadLocalRandom.current().nextInt(hiddenNeurons.size());
        }
        
        n2.addConnection(n1, Math.random(), innovationNumber); //TODO: determine which is on an inferior layer
    }

    public void mConnectionWeights(Connection conn){
        //modify the weight of a connection
        conn.weight = Math.random();
    }

}