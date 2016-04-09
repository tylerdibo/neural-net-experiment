package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;

public class Genome{
    
    static int innovationNumber = 1;
    
    List<Neuron> inputNeurons;
    List<Neuron> outputNeurons;
    List<Connection> links;
    
    int id;
    
    public Genome(){
        inputNeurons = new ArrayList<Neuron>();
        outputNeurons = new ArrayList<Neuron>();
    }
    
    public List<Double> calculate(){
        List<Double> outputValues = new ArrayList<Double>();
        for(int i = 0; i < outputNeurons.size(); i++){
            outputNeurons.get(i).calculate();
            outputValues.add(outputNeurons.get(i).getActivatedValue());
        }
        return outputValues;
    }
    
        //TODO: compare with other innovations to check if matching
    public void mAddNeuron(){
        //take connection and put neuron between
        
        Connection conn;
        
        int tries = 0;
        
        if (links.size() < 15){
            for(Connection c : links){
                if(c.in == bias || !c.active || (Math.random() < 0.3)){
                    continue;
                }
                conn = c;
            }
        }else{
            while(tries < 20){
                Connection c = links.get(ThreadLocalRandom.current().nextInt(links.size()));
                if(!(c.in == bias || c.active)){
                    conn = c;
                    break;
                }
                
                tries++;
            }
        }
        
        conn.active = false;

        Neuron newNeuron = new Neuron(innovationNumber);
        conn.out.addConnection(newNeuron, Math.random(), innovationNumber); //TODO: more accurate random function?
        newNeuron.addConnection(conn.in, Math.random(), innovationNumber);
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