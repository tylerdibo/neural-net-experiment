package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.io.Serializable;

public class Genome implements Serializable{

    static final int MAX_ADD_NEURON_TRIES = 20;
    static final int SMALL_GENOME_LIMIT = 15;
    static final double ADD_NEURON_OLD_BIAS = 0.3;
    static final double LOOP_RECUR_CHANCE = 0.5;
    static final double MAX_NEW_CONNECTION_WEIGHT = 10.0;
    
    private final static Logger LOGGER = Logger.getLogger(Genome.class.getName());

    private int innovationNumber;

    public List<Neuron> inputNeurons;
    public List<Neuron> hiddenNeurons;
    public List<Neuron> outputNeurons;
    
    public List<Neuron> nonInputNeurons;
    public List<Neuron> allNeurons;
    
    public List<Connection> links;
    
    int id;
    
    public Genome(){
        inputNeurons = new ArrayList<Neuron>();
        outputNeurons = new ArrayList<Neuron>();
        hiddenNeurons = new ArrayList<Neuron>();
        
        nonInputNeurons = new ArrayList<Neuron>();
        allNeurons = new ArrayList<Neuron>();
        
        links = new ArrayList<Connection>();
        innovationNumber = 1;
    }

    public List<Double> calculate(){
        List<Double> outputValues = new ArrayList<Double>();
        for(Neuron n : outputNeurons){
            n.calculate(null);
            outputValues.add(n.getActivatedValue());
        }
        return outputValues;
    }
    
    public void loadSensors(double[] values){
        int i = 0;
        for(Neuron n : inputNeurons){
            if(n.type == Neuron.NeuronTypes.INPUT){
                n.lastActivatedValue2 = n.lastActivatedValue;
                n.lastActivatedValue = n.activatedValue;
                
                n.activationCount++;
                n.activatedValue = values[i];
                
                i++;
            }else{
                LOGGER.warning("Non input neuron in the input array.");
            }
        }
    }
    
    public void reset(){
        for(Neuron n : outputNeurons){
            n.reset();
        }
    }
    
    public void mAddNeuron(List<Innovation> innovs, double currentInnov){
        //take connection and put neuron between
        
        Connection conn = null;
        
        int tries = 0;
        boolean found = false;
        
        if (links.size() < SMALL_GENOME_LIMIT){
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

        for(Innovation i : innovs){ //TODO: maybe create the connections after, and just log when its the same innov
            if((i.type == Innovation.InnovType.NEWNODE) && (i.inNodeId == conn.in.getId()) && (i.outNodeId == conn.out.getId()) && (i.oldLinkInnov == conn.innovationNum)){
                Neuron newNeuron = new Neuron(Neuron.NeuronTypes.HIDDEN, innovationNumber);

                Connection connNewToOut = new Connection(newNeuron, conn.out, conn.weight, i.innovNum2);
                links.add(connNewToOut);
                conn.out.addConnection(connNewToOut);

                Connection connInToNew = new Connection(conn.in, newNeuron, 1.0, i.innovNum1);
                links.add(connInToNew);
                newNeuron.addConnection(connInToNew);
                
                addNeuron(newNeuron);
                return;
            }
        }
        Neuron newNeuron = new Neuron(Neuron.NeuronTypes.HIDDEN, innovationNumber);

        Connection connNewToOut = new Connection(newNeuron, conn.out, conn.weight, currentInnov);
        links.add(connNewToOut);
        conn.out.addConnection(connNewToOut);

        Connection connInToNew = new Connection(conn.in, newNeuron, 1.0, innovationNumber);
        links.add(connInToNew);
        newNeuron.addConnection(connInToNew);

        addNeuron(newNeuron);

        innovs.add(new Innovation(conn.in.getId(), conn.out.getId(), currentInnov - 2.0, currentInnov - 1.0, newNeuron.getId(), conn.innovationNum));
        
        LOGGER.info("New neuron added in connection number " + conn.innovationNum);
    }

    public void mAddConnection(List<Innovation> innovs, double currentInnov, int tries){
        //add a new connection between two random neurons
        Neuron node1 = null;
        Neuron node2 = null;
        boolean recur;
        boolean found = false;
        
        recur = ThreadLocalRandom.current().nextBoolean();
        
        if(recur){
            recurLoop:
            for(int i = 0; i < tries; i++){
                if(ThreadLocalRandom.current().nextDouble() > LOOP_RECUR_CHANCE){
                    node1 = nonInputNeurons.get(ThreadLocalRandom.current().nextInt(nonInputNeurons.size()));
                    node2 = node1;
                }else{
                    node1 = allNeurons.get(ThreadLocalRandom.current().nextInt(nonInputNeurons.size()));
                    node2 = nonInputNeurons.get(ThreadLocalRandom.current().nextInt(nonInputNeurons.size()));
                }
                
                if(node2.type != Neuron.NeuronTypes.INPUT){
                    for(Connection c : links){
                        if(c.in == node1 && c.out == node2 && c.isRecurrent){
                            continue recurLoop;
                        }
                    }
                    //check if network contains infinite loop
                    found = true;
                    break;
                }
            }
        }else{
            nonRecurLoop:
            for(int i = 0; i < tries; i++){
                node1 = nonInputNeurons.get(ThreadLocalRandom.current().nextInt(nonInputNeurons.size()));
                node2 = nonInputNeurons.get(ThreadLocalRandom.current().nextInt(nonInputNeurons.size()));
            
                if(node2.type != Neuron.NeuronTypes.INPUT){
                    for(Connection c : links){
                        if(c.in == node1 && c.out == node2 && !c.isRecurrent){
                            continue nonRecurLoop;
                        }
                    }
                    //check if network contains infinite loop
                    found = true;
                    break;
                }
            }
        }
        
        if (!found){
            return;
        }
        
        double newWeight;
        Connection conn;
        for(Innovation i : innovs){
            if(i.type == Innovation.InnovType.NEWLINK && i.inNodeId == node1.getId() && i.outNodeId == node2.getId() && i.recur == recur){
                conn = new Connection(node1, node2, i.newLinkWeight, i.innovNum1);
                links.add(conn);
                LOGGER.info("New connection added between " + node1.getId() + " and " + node2.getId() + " matching Innovation " + i.innovNum1);
                return;
            }
        }

        newWeight = ThreadLocalRandom.current().nextDouble(-MAX_NEW_CONNECTION_WEIGHT, MAX_NEW_CONNECTION_WEIGHT); //TODO: double check this
        conn = new Connection(node1, node2, newWeight, currentInnov);

        innovs.add(new Innovation(node1.getId(), node2.getId(), currentInnov, newWeight));
        
        links.add(conn);
        LOGGER.info("New connection added between " + node1.getId() + " and " + node2.getId());
    }

    public void mConnectionWeights(double power, double rate, boolean coldGauss){
        //modify the weight of a connection
        //conn.weight = Math.random();
        boolean severe = ThreadLocalRandom.current().nextBoolean();
        double gaussPoint, coldGaussPoint;
        double endPart = (double)links.size()*0.8;
        
        int i = 0;
        for(Connection c : links){
            //Determine probabilities, a lot of values to mess with
            if(severe){
                gaussPoint = 0.3;
                coldGaussPoint = 0.1;
            }else if((links.size() >= 10) && ((double)i > endPart)){
                gaussPoint = 0.5;
                coldGaussPoint = 0.3;
            }else{
                if(ThreadLocalRandom.current().nextBoolean()){
                    gaussPoint = 1.0 - rate;
                    coldGaussPoint = 0.9 - rate;
                }else{
                    gaussPoint = 1.0 - rate;
                    coldGaussPoint = 1.0 - rate;
                }
            }
            
            double randNum = ThreadLocalRandom.current().nextDouble(-power, power);
            if(coldGauss){
                c.weight = randNum;
            }else{
                double choice = ThreadLocalRandom.current().nextDouble();
                if(choice > gaussPoint)
                    c.weight += randNum;
                else if(choice > coldGaussPoint)
                    c.weight = randNum;
            }
            
            c.mutationNum = c.weight;
            
            i++;
            
            LOGGER.finer("Connection " + c.innovationNum + " mutated to weight " + c.weight);
        }
    }
    
    public void mToggleEnable(int times){
        Connection conn;
        for(int i = 0; i < times; i++){
            conn = links.get(ThreadLocalRandom.current().nextInt(links.size()));
            boolean isolated = true;
            
            if(conn.active){
                for(Connection check : links){
                    if((check.in == conn.in) && check.active && (check.innovationNum != conn.innovationNum)){
                        isolated = false;
                        break;
                    }
                }
            }
            
            if(isolated){
                conn.active = true;
            }else{
                conn.active = false;
            }
        }
    }
    
    public void mReenableFirst(){
        for(Connection c : links){
            if(c.active = false){
                c.active = true;
                return;
            }
        }
    }
    
    public Genome mateMultipoint(Genome g, int newGenomeId, double fitness1, double fitness2){
        Genome baby = null;
        boolean p1Better;
        if(fitness1 > fitness2)
            p1Better = true;
        else if(fitness1 == fitness2){
            if(links.size() < g.links.size())
                p1Better = true;
            else
                p1Better = false;
        }else
            p1Better = false;

        boolean skip;

        return baby;
    }
    
    /*public Genome clone(){
        //ByteArrayOutputStream bos = new ByteArrayOutputStream();
    }*/

    public void addNeuron(Neuron neuron){
        switch(neuron.type){
            case INPUT:
                inputNeurons.add(neuron);
                allNeurons.add(neuron);
                break;
            case HIDDEN:
                hiddenNeurons.add(neuron);
                nonInputNeurons.add(neuron);
                allNeurons.add(neuron);
                break;
            case OUTPUT:
                outputNeurons.add(neuron);
                nonInputNeurons.add(neuron);
                allNeurons.add(neuron);
                break;
            case BIAS:
                allNeurons.add(neuron);
                break;
        }
    }

}