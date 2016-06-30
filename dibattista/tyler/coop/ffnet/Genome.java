package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class Genome{

    static final int MAX_ADD_NEURON_TRIES = 20;
    static final int SMALL_GENOME_LIMIT = 15;
    static final double ADD_NEURON_OLD_BIAS = 0.3;
    static final double LOOP_RECUR_CHANCE = 0.5;
    static final double RECUR_CHANCE = 0.0;
    static final double MAX_NEW_CONNECTION_WEIGHT = 1.0;
    static final double DISJOINT_COEFF = 1.0;
    static final double EXCESS_COEFF = 1.0;
    static final double MUTDIFF_COEFF = 0.4;

    private final static Logger LOGGER = Logger.getLogger(Genome.class.getName());

    public int count = 0;

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
    }
    
    public Genome(int id, List<Neuron> nList, List<Connection> cList){
        this.id = id;
        inputNeurons = new ArrayList<Neuron>();
        outputNeurons = new ArrayList<Neuron>();
        hiddenNeurons = new ArrayList<Neuron>();
        
        nonInputNeurons = new ArrayList<Neuron>();
        allNeurons = new ArrayList<Neuron>();
        
        links = cList;
        
        for(Neuron n : nList){
            addNeuron(n);
        }
    }

    public ArrayList<Double> calculate(){
        ArrayList<Double> outputValues = new ArrayList<Double>();
        for(Neuron n : hiddenNeurons){
            n.calculate();
        }
        for(Neuron n : outputNeurons){
            n.calculate();
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
    
    public void mAddNeuron(List<Innovation> innovs, double currentInnov, int nodeId){
        //take connection and put neuron between
        
        Connection conn = null;
        
        int tries = 0;
        boolean found = false;
        
        if (links.size() < SMALL_GENOME_LIMIT){
            for(Connection c : links){
                if(c.in.type != Neuron.NeuronTypes.BIAS && c.active && (ThreadLocalRandom.current().nextDouble() > ADD_NEURON_OLD_BIAS)){
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
        if(!found){
            //LOGGER.info("Open connection not found");
            return;   
        }

        conn.active = false;

        for(Innovation i : innovs){
            if((i.type == Innovation.InnovType.NEWNODE) && (i.inNodeId == conn.in.getId()) && (i.outNodeId == conn.out.getId()) && (i.oldLinkInnov == conn.innovationNum)){
                Neuron newNeuron = new Neuron(Neuron.NeuronTypes.HIDDEN, nodeId);

                Connection connNewToOut = new Connection(newNeuron, conn.out, conn.weight, i.innovNum2, false, 0);
                links.add(connNewToOut);
                conn.out.addConnection(connNewToOut);

                Connection connInToNew = new Connection(conn.in, newNeuron, 1.0, i.innovNum1, false, 0);
                links.add(connInToNew);
                newNeuron.addConnection(connInToNew);
                
                addNeuron(newNeuron);
                
                //LOGGER.info("New neuron added in connection number " + conn.innovationNum + " matching Innovation " + i.innovNum1);
                return;
            }
        }
        Neuron newNeuron = new Neuron(Neuron.NeuronTypes.HIDDEN, nodeId);

        Connection connNewToOut = new Connection(newNeuron, conn.out, conn.weight, currentInnov, false, 0);
        links.add(connNewToOut);
        conn.out.addConnection(connNewToOut);

        Connection connInToNew = new Connection(conn.in, newNeuron, 1.0, currentInnov+1.0, false, 0);
        links.add(connInToNew);
        newNeuron.addConnection(connInToNew);
        
        currentInnov += 2.0;

        addNeuron(newNeuron);

        innovs.add(new Innovation(conn.in.getId(), conn.out.getId(), currentInnov - 2.0, currentInnov - 1.0, newNeuron.getId(), conn.innovationNum));
        
        //LOGGER.info("New neuron added in connection number " + conn.innovationNum);
    }

    public void mAddConnection(List<Innovation> innovs, double currentInnov, int tries){
        //add a new connection between two random neurons
        Neuron node1 = null;
        Neuron node2 = null;
        boolean recur;
        boolean recurConfirm;
        boolean found = false;

        int thresh;
        if(inputNeurons.size() > 150){
            thresh = (allNeurons.size() * 8); //should be (allNeurons.size()*allNeurons.size())
        }else{
            thresh = (allNeurons.size() * allNeurons.size());
        }
        recur = ThreadLocalRandom.current().nextDouble() < RECUR_CHANCE;
        
        if(recur){
            recurLoop:
            for(int i = 0; i < tries; i++){
                if(ThreadLocalRandom.current().nextDouble() < LOOP_RECUR_CHANCE){
                    node1 = nonInputNeurons.get(ThreadLocalRandom.current().nextInt(nonInputNeurons.size()));
                    node2 = node1;
                }else{
                    node1 = allNeurons.get(ThreadLocalRandom.current().nextInt(allNeurons.size()));
                    node2 = nonInputNeurons.get(ThreadLocalRandom.current().nextInt(nonInputNeurons.size()));
                }
                
                if(node2.type != Neuron.NeuronTypes.INPUT){
                    for(Connection c : links){
                        if(c.in == node1 && c.out == node2 && c.isRecurrent){
                            continue recurLoop;
                        }
                    }
                    count = 0;
                    recurConfirm = isRecur(node1, node2, thresh);
                    if(recurConfirm) {
                        found = true;
                        break;
                    }
                }
            }
        }else{
            nonRecurLoop:
            for(int i = 0; i < tries; i++){
                node1 = allNeurons.get(ThreadLocalRandom.current().nextInt(allNeurons.size()));
                node2 = nonInputNeurons.get(ThreadLocalRandom.current().nextInt(nonInputNeurons.size()));
            
                if(node2.type != Neuron.NeuronTypes.INPUT){
                    for(Connection c : links){
                        if(c.in == node1 && c.out == node2 && !c.isRecurrent){
                            continue nonRecurLoop;
                        }
                    }
                    //check if network contains infinite loop
                    count = 0;
                    recurConfirm = isRecur(node1, node2, thresh);
                    if(!recurConfirm) {
                        found = true;
                        break;
                    }
                }
            }
        }
        
        if (!found){
            //LOGGER.info("New connection not found.");
            return;
        }
        
        double newWeight;
        Connection conn;
        for(Innovation i : innovs){
            if(i.type == Innovation.InnovType.NEWLINK && i.inNodeId == node1.getId() && i.outNodeId == node2.getId() && i.recur == recur){
                conn = new Connection(node1, node2, i.newLinkWeight, i.innovNum1, recur, 0);
                node2.addConnection(conn);
                links.add(conn);
                //LOGGER.info("New connection added between " + node1.getId() + " and " + node2.getId() + " isRecurrent = " + recur + " matching Innovation " + i.innovNum1);
                return;
            }
        }

        newWeight = ThreadLocalRandom.current().nextDouble(-MAX_NEW_CONNECTION_WEIGHT, MAX_NEW_CONNECTION_WEIGHT);
        conn = new Connection(node1, node2, newWeight, currentInnov, recur, newWeight);
        node2.addConnection(conn);

        innovs.add(new Innovation(node1.getId(), node2.getId(), currentInnov, newWeight, recur));
        
        links.add(conn);
        //LOGGER.info("New connection added between " + node1.getId() + " and " + node2.getId() + " isRecurrent = " + recur);
    }

    public void mConnectionWeights(double power, double rate, boolean coldGauss){
        //modify the weight of a connection
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
            
            //LOGGER.finer("Connection " + c.innovationNum + " mutated to weight " + c.weight);
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
            
            conn.active = isolated;
        }
    }
    
    public void mReenableFirst(){
        for(Connection c : links){
            if(!c.active){
                c.active = true;
                return;
            }
        }
    }
    
    public Genome mateMultipoint(Genome g, int newGenomeId, double fitness1, double fitness2){
        Genome baby;
        boolean p1Better;
        if(fitness1 > fitness2)
            p1Better = true;
        else if(fitness1 == fitness2){
            p1Better = links.size() < g.links.size();
        }else
            p1Better = false;

        boolean skip;
        boolean disable = false;
        double p1Innov, p2Innov;
        Iterator<Connection> p1Iter = links.iterator();
        Iterator<Connection> p2Iter = g.links.iterator();
        Connection p1conn = p1Iter.next();
        Connection p2conn = p2Iter.next();
        Connection chosenGene, newGene;
        List<Neuron> newNeurons = new ArrayList<Neuron>();
        List<Connection> newLinks = new ArrayList<Connection>();
        Neuron in, out;
        Neuron newIn = null;
        Neuron newOut = null;

        boolean p1HasNext = true, p2HasNext = true;
        boolean advanceP1 = false, advanceP2 = false;

        while(p1Iter.hasNext() || p2Iter.hasNext()){
            p1HasNext = p1Iter.hasNext();
            p2HasNext = p2Iter.hasNext();

            if(advanceP1){
                if(p1HasNext)
                    p1conn = p1Iter.next();
                advanceP1 = false;
            }
            if(advanceP2){
                if(p2HasNext)
                    p2conn = p2Iter.next();
                advanceP2 = false;
            }

            skip = false;
            
            if(!p1HasNext){
                chosenGene = p2conn;
                advanceP2 = true;
                if(p1Better)
                    skip = true;
            }else if(!p2HasNext){
                chosenGene = p1conn;
                advanceP1 = true;
                if(!p1Better)
                    skip = true;
            }else{
                p1Innov = p1conn.innovationNum;
                p2Innov = p2conn.innovationNum;

                if(p1Innov == p2Innov){
                    if(ThreadLocalRandom.current().nextBoolean()){
                        chosenGene = p1conn;
                    }else{
                        chosenGene = p2conn;
                    }

                    if(!p1conn.active || !p2conn.active){
                        if(ThreadLocalRandom.current().nextDouble() > 0.75)
                            disable = true;
                    }

                    advanceP1 = true;
                    advanceP2 = true;
                }else if(p1Innov < p2Innov){
                    chosenGene = p1conn;
                    advanceP1 = true;
                    if(!p1Better)
                        skip = true;
                }else{
                    chosenGene = p2conn;
                    advanceP2 = true;
                    if(p1Better)
                        skip = true;
                }
            }
            
            for(Connection c : newLinks){
                if((c.in.getId() == chosenGene.in.getId()) &&
                    (c.out.getId() == chosenGene.out.getId()) &&
                    (c.isRecurrent == chosenGene.isRecurrent)){
                        skip = true;
                        break;
                }
            }
                
            if(!skip){
                in = chosenGene.in;
                out = chosenGene.out;
                
                boolean inFound = false;
                boolean outFound = false;
                for(Neuron n : newNeurons){
                    if(n.getId() == in.getId() && !inFound){
                        inFound = true;
                        newIn = n;
                    }
                    if(n.getId() == out.getId() && !outFound){
                        outFound = true;
                        newOut = n;
                    }
                    if(inFound && outFound)
                        break;
                }
                if(in.getId() < out.getId()){
                    if(!inFound){
                        newIn = new Neuron(in);
                        newNeurons.add(newIn);
                    }
                    if(!outFound){
                        newOut = new Neuron(out);
                        newNeurons.add(newOut);
                    }
                }else{
                    if(!outFound){
                        newOut = new Neuron(out);
                        newNeurons.add(newOut);
                    }
                    if(!inFound){
                        newIn = new Neuron(in);
                        newNeurons.add(newIn); //TODO: fix ordering of list
                    }
                }
                
                newGene = new Connection(chosenGene, newIn, newOut);
                if(disable){
                    newGene.active = false;
                    disable = false;
                }
                newOut.addConnection(newGene);
                newLinks.add(newGene);
            }
        }
        
        baby = new Genome(newGenomeId, newNeurons, newLinks);

        return baby;
    }

    public Genome mateMultipointAvg(Genome g, int newGenomeId, double fitness1, double fitness2){
        Genome baby;
        boolean p1Better;
        if(fitness1 > fitness2)
            p1Better = true;
        else if(fitness1 == fitness2){
            p1Better = links.size() < g.links.size();
        }else
            p1Better = false;

        Connection avgConn = new Connection(null, null, 0.0, 0.0, false, 0.0);

        boolean skip;
        boolean disable = false;
        double p1Innov, p2Innov;
        Iterator<Connection> p1Iter = links.iterator();
        Iterator<Connection> p2Iter = g.links.iterator();
        Connection p1conn = p1Iter.next();
        Connection p2conn = p2Iter.next();
        Connection chosenGene, newGene;
        List<Neuron> newNeurons = new ArrayList<Neuron>();
        List<Connection> newLinks = new ArrayList<Connection>();
        Neuron in, out;
        Neuron newIn = null;
        Neuron newOut = null;

        boolean p1HasNext, p2HasNext;
        boolean advanceP1 = false, advanceP2 = false;

        while(p1Iter.hasNext() || p2Iter.hasNext()){
            p1HasNext = p1Iter.hasNext();
            p2HasNext = p2Iter.hasNext();

            if(advanceP1){
                if(p1HasNext)
                    p1conn = p1Iter.next();
                advanceP1 = false;
            }
            if(advanceP2){
                if(p2HasNext)
                    p2conn = p2Iter.next();
                advanceP2 = false;
            }

            avgConn.active = true;

            skip = false;

            if(!p1HasNext && !p2HasNext){
                continue;
            }else if(!p1HasNext){
                chosenGene = p2conn;
                advanceP2 = true;
                if(p1Better)
                    skip = true;
            }else if(!p2HasNext){
                chosenGene = p1conn;
                advanceP1 = true;
                if(!p1Better)
                    skip = true;
            }else{
                p1Innov = p1conn.innovationNum;
                p2Innov = p2conn.innovationNum;

                if(p1Innov == p2Innov){
                    avgConn.weight = (p1conn.weight+p2conn.weight) / 2.0;

                    if(ThreadLocalRandom.current().nextBoolean()) //TODO: maybe have a ThreadLocalRandom variable
                        avgConn.in = p1conn.in;
                    else
                        avgConn.in = p2conn.in;

                    if(ThreadLocalRandom.current().nextBoolean())
                        avgConn.out = p1conn.out;
                    else
                        avgConn.out = p2conn.out;

                    if(ThreadLocalRandom.current().nextBoolean())
                        avgConn.isRecurrent = p1conn.isRecurrent;
                    else
                        avgConn.isRecurrent = p2conn.isRecurrent;

                    avgConn.innovationNum = p1conn.innovationNum;
                    avgConn.mutationNum = (p1conn.mutationNum+p2conn.mutationNum) / 2.0;

                    if(!p1conn.active || !p2conn.active){
                        if(ThreadLocalRandom.current().nextDouble() < 0.75)
                            avgConn.active = false;
                    }

                    chosenGene = avgConn;

                    advanceP1 = true;
                    advanceP2 = true;
                }else if(p1Innov < p2Innov){
                    chosenGene = p1conn;
                    advanceP1 = true;
                    if(!p1Better)
                        skip = true;
                }else{
                    chosenGene = p2conn;
                    advanceP2 = true;
                    if(p1Better)
                        skip = true;
                }
            }

            for(Connection c : newLinks){
                if((c.in.getId() == chosenGene.in.getId()) &&
                        (c.out.getId() == chosenGene.out.getId()) &&
                        (c.isRecurrent == chosenGene.isRecurrent)){
                    skip = true;
                    break;
                }
            }

            if(!skip){
                in = chosenGene.in;
                out = chosenGene.out;

                boolean inFound = false;
                boolean outFound = false;
                for(Neuron n : newNeurons){
                    if(n.getId() == in.getId() && !inFound){
                        inFound = true;
                        newIn = n;
                    }
                    if(n.getId() == out.getId() && !outFound){
                        outFound = true;
                        newOut = n;
                    }
                    if(inFound && outFound)
                        break;
                }
                if(in.getId() < out.getId()){
                    if(!inFound){
                        newIn = new Neuron(in);
                        newNeurons.add(newIn);
                    }
                    if(!outFound){
                        newOut = new Neuron(out);
                        newNeurons.add(newOut);
                    }
                }else{
                    if(!outFound){
                        newOut = new Neuron(out);
                        newNeurons.add(newOut);
                    }
                    if(!inFound){
                        newIn = new Neuron(in);
                        newNeurons.add(newIn); //TODO: fix ordering of list
                    }
                }

                newGene = new Connection(chosenGene, newIn, newOut);
                newOut.addConnection(newGene);
                newLinks.add(newGene);
            }
        }

        baby = new Genome(newGenomeId, newNeurons, newLinks);

        return baby;
    }

    public Genome mateSinglepoint(Genome g, int newGenomeId){
        Genome baby;
        Connection avgConn = new Connection(null, null, 0.0, 0.0, false, 0.0);
        Connection chosenGene = null;
        Connection newGene;

        Iterator<Connection> p1Iter;
        Iterator<Connection> p2Iter;
        boolean skip = false;
        boolean disable = false;
        double p1Innov, p2Innov;
        int crossPoint;
        int geneCount = 0;
        List<Neuron> newNeurons = new ArrayList<Neuron>();
        List<Connection> newLinks = new ArrayList<Connection>();
        Neuron in, out;
        Neuron newIn = null;
        Neuron newOut = null;

        if(links.size() < g.links.size()){
            crossPoint = ThreadLocalRandom.current().nextInt(links.size());
            p1Iter = links.iterator();
            p2Iter = g.links.iterator();
        }else{
            crossPoint = ThreadLocalRandom.current().nextInt(g.links.size());
            p1Iter = g.links.iterator();
            p2Iter = links.iterator();
        }
        Connection p1conn = p1Iter.next();
        Connection p2conn = p2Iter.next();

        boolean p1HasNext = true, p2HasNext = true;

        while(p1HasNext || p2HasNext){
            p1HasNext = p1Iter.hasNext();
            p2HasNext = p2Iter.hasNext();

            avgConn.active = true;

            if(!p1HasNext){
                chosenGene = p2conn;
                if(p2HasNext)
                    p2conn = p2Iter.next();
            }else if(!p2HasNext){
                chosenGene = p1conn;
                if(p1HasNext)
                    p1conn = p1Iter.next();
            }else{
                p1Innov = p1conn.innovationNum;
                p2Innov = p2conn.innovationNum;

                if(p1Innov == p2Innov){
                    if(geneCount < crossPoint){
                        chosenGene = p1conn;
                    }else if(geneCount > crossPoint){
                        chosenGene = p2conn;
                    }else{
                        avgConn.weight = (p1conn.weight+p2conn.weight) / 2.0;

                        if(ThreadLocalRandom.current().nextBoolean())
                            avgConn.in = p1conn.in;
                        else
                            avgConn.in = p2conn.in;

                        if(ThreadLocalRandom.current().nextBoolean())
                            avgConn.out = p1conn.out;
                        else
                            avgConn.out = p2conn.out;

                        if(ThreadLocalRandom.current().nextBoolean())
                            avgConn.isRecurrent = p1conn.isRecurrent;
                        else
                            avgConn.isRecurrent = p2conn.isRecurrent;

                        avgConn.innovationNum = p1conn.innovationNum;
                        avgConn.mutationNum = (p1conn.mutationNum+p2conn.mutationNum) /2.0;

                        if(!p1conn.active || !p2conn.active){
                            avgConn.active = false;
                        }
                        chosenGene = avgConn;
                    }

                    p1conn = p1Iter.next();
                    p2conn = p2Iter.next();
                    geneCount++;
                }else if(p1Innov < p2Innov){
                    if(geneCount < crossPoint){
                        chosenGene = p1conn;
                        p1conn = p1Iter.next();
                        geneCount++;
                    }else{
                        chosenGene = p2conn;
                        p2conn = p2Iter.next();
                    }
                }else{
                    p2conn = p2Iter.next();
                    skip = true;
                }
            }

            for(Connection c : newLinks){
                if((c.in.getId() == chosenGene.in.getId()) &&
                        (c.out.getId() == chosenGene.out.getId()) &&
                        (c.isRecurrent == chosenGene.isRecurrent)){
                    skip = true;
                    break;
                }
            }

            if(!skip){
                in = chosenGene.in;
                out = chosenGene.out;

                boolean inFound = false;
                boolean outFound = false;
                for(Neuron n : newNeurons){
                    if(n.getId() == in.getId() && !inFound){
                        inFound = true;
                        newIn = n;
                    }
                    if(n.getId() == out.getId() && !outFound){
                        outFound = true;
                        newOut = n;
                    }
                    if(inFound && outFound)
                        break;
                }
                if(in.getId() < out.getId()){
                    if(!inFound){
                        newIn = new Neuron(in);
                        newNeurons.add(newIn);
                    }
                    if(!outFound){
                        newOut = new Neuron(out);
                        newNeurons.add(newOut);
                    }
                }else{
                    if(!outFound){
                        newOut = new Neuron(out);
                        newNeurons.add(newOut);
                    }
                    if(!inFound){
                        newIn = new Neuron(in);
                        newNeurons.add(newIn); //TODO: fix ordering of list
                    }
                }

                newGene = new Connection(chosenGene, newIn, newOut);
                newOut.addConnection(newGene);
                newLinks.add(newGene);
            }

            skip = false;

        }

        baby = new Genome(newGenomeId, newNeurons, newLinks);

        return baby;
    }
    
    public boolean isRecur(Neuron potIn, Neuron potOut, int thresh){
        count++;
        if(count > thresh){
            return false;
        }
        
        if(potIn == potOut){
            return true;
        }else{
            for(Connection c : potIn.connections){
                if(!c.isRecurrent){
                    if(isRecur(c.in, potOut, thresh))
                        return true;
                }
            }
            return false;
        }
    }
    
    public Genome deepClone(int newGenomeId){
        Genome newGenome;
        List<Neuron> neuronsDup = new ArrayList<Neuron>();
        List<Connection> linksDup = new ArrayList<Connection>();
        Neuron newNeuron;
        for(Neuron n : allNeurons){
            newNeuron = new Neuron(n);
            n.dup = newNeuron;
            neuronsDup.add(newNeuron);
        }

        Neuron in, out;
        Connection newLink;
        for(Connection c : links){
            in = c.in.dup;
            out = c.out.dup;

            newLink = new Connection(c, in, out);
            out.addConnection(newLink);
            linksDup.add(newLink);
        }

        newGenome = new Genome(newGenomeId, neuronsDup, linksDup);
        return newGenome;
    }
    
    public double compatibility(Genome g){
        double p1Innov, p2Innov;
        double numExcess;
        double numMatching = 0.0;
        double mutDiffTotal = 0.0;
        double numDisjoint = 0.0;

        Iterator<Connection> p1Iter = links.iterator();
        Iterator<Connection> p2Iter = g.links.iterator();
        Connection p1conn = p1Iter.next();
        Connection p2conn = p2Iter.next();

        numExcess = Math.abs(g.links.size() - links.size());
        while(p1Iter.hasNext() && p2Iter.hasNext()){
            p1Innov = p1conn.innovationNum;
            p2Innov = p2conn.innovationNum;

            if(p1Innov == p2Innov) {
                numMatching += 1.0;
                mutDiffTotal += Math.abs(p1conn.mutationNum - p2conn.mutationNum);

                p1conn = p1Iter.next();
                p2conn = p2Iter.next();
            }else if(p1Innov < p2Innov) {
                p1conn = p1Iter.next();
                numDisjoint += 1.0;
            }else{
                p2conn = p2Iter.next();
                numDisjoint += 1.0;
            }
        }

        return (DISJOINT_COEFF*numDisjoint + EXCESS_COEFF*numExcess + MUTDIFF_COEFF*(mutDiffTotal/numMatching));
    }
    
    public int getLastNodeId(){
        return (allNeurons.get(allNeurons.size() - 1).getId()) + 1;
    }
    
    public double getLastInnovNum(){
        if(links.isEmpty())
            return 0.0;
        else
            return (links.get(links.size() - 1).innovationNum) + 1.0;
    }

    public void addNeuron(Neuron neuron){ //TODO: sort neurons
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