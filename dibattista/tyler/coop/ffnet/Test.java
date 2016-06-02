package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Test{
    
    public static void main(String[] args){
        TestMating();
    }

    static void Test1(String[] args){
        long startTime = System.nanoTime();
        
        double innovationNumber = 2.0;
        int nodeId = 2;
        
        Genome geno = new Genome();
        Neuron in = new Neuron(Neuron.NeuronTypes.INPUT, 0);
        geno.addNeuron(in);
        
        Neuron out = new Neuron(Neuron.NeuronTypes.OUTPUT, 1);
        geno.addNeuron(out);
        
        List<Innovation> innovs = new ArrayList<Innovation>();

        for(int i = 0; i < 5; i++){
            geno.mAddConnection(innovs, innovationNumber++, 20);
            geno.mAddNeuron(innovs, innovationNumber++, nodeId++);
        }
        
        //geno.mConnectionWeights(2.0, 0.5, false);
        double[] inputs = new double[4];
        for(int i = 0; i < args.length; i++){
            inputs[i] = Double.parseDouble(args[i]);
        }
        geno.loadSensors(inputs);
        System.out.println("OUTPUT: " + geno.calculate().get(0));
        
        System.out.println("Time elapsed in ns: " + (System.nanoTime()-startTime));
    }

    static void Test2(){
        long startTime = System.nanoTime();

        int[] one = {0, 0, 0, 0, 0};
        int[] two = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] three = {0, 0};
        int[] four = {0};

        int total = one.length + two.length + three.length + four.length;
        int twoChance = one.length + two.length;
        int threeChance = one.length + two.length + three.length;

        int oneHits = 0, twoHits = 0, threeHits = 0, fourHits = 0;

        for(int i = 0; i < 1000; i++){
            double random = Math.random();
            if(random < ((double)one.length / (double)total)){
                oneHits++;
                one[ThreadLocalRandom.current().nextInt(one.length)]++;
            }else if(random < ((double)twoChance / (double)total)){
                twoHits++;
                two[ThreadLocalRandom.current().nextInt(two.length)]++;
            }else if(random < ((double)threeChance / (double)total)){
                threeHits++;
                three[ThreadLocalRandom.current().nextInt(three.length)]++;
            }else{
                fourHits++;
                four[ThreadLocalRandom.current().nextInt(four.length)]++;
            }
        }
        int k = 0;

        System.out.println("Time elapsed in ns: " + (System.nanoTime() - startTime));
        System.out.println(one);
    }
    
    static void Test3(){
        long startTime = System.nanoTime();
        
        int[] all = new int[20];
        
        for(int i = 0; i < 1000; i++){
            all[ThreadLocalRandom.current().nextInt(all.length)]++;
        }
        
        System.out.println("Time elapsed in ns: " + (System.nanoTime() - startTime));
        System.out.println(all[6]);
    }
    
    static void Test4(){
        long startTime = System.nanoTime();
        
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double highestFitness = 0;
        
        for(int i = 0; i<10000; i++){
            highestFitness = Math.max(random.nextDouble(), highestFitness);
        }
        
        System.out.println("Time elapsed in ns: " + (System.nanoTime() - startTime));
    }

    static void Test5(){
        long startTime = System.nanoTime();

        ThreadLocalRandom random = ThreadLocalRandom.current();
        double rand;
        double highestFitness = 0;

        for(int i = 0; i<10000; i++){
            rand = random.nextDouble();
            if(rand > highestFitness){
                highestFitness = rand;
            }
        }

        System.out.println("Time elapsed in ns: " + (System.nanoTime() - startTime));
    }

    static void TestMating(){
        int numTiles = 9;
        Genome g1 = new Genome();
        Genome g2 = new Genome();
        for(int i = 0; i < numTiles; i++) {
            g1.addNeuron(new Neuron(Neuron.NeuronTypes.INPUT, i));
            g2.addNeuron(new Neuron(Neuron.NeuronTypes.INPUT, i));
        }

        Connection conn;
        Neuron n;
        for(int i = 0; i < numTiles; i++) {
            n = new Neuron(Neuron.NeuronTypes.OUTPUT, i+numTiles);
            g1.addNeuron(n);
            conn = new Connection(g1.inputNeurons.get(i), n, 1.0, 0.0, false, 1.0);
            n.addConnection(conn);
            g1.links.add(conn);
        }
        for(int i = 0; i < numTiles; i++) {
            n = new Neuron(Neuron.NeuronTypes.OUTPUT, i+numTiles);
            g2.addNeuron(n);
            conn = new Connection(g2.inputNeurons.get(i), n, 1.0, 0.0, false, 1.0);
            n.addConnection(conn);
            g2.links.add(conn);
        }

        if(g1.allNeurons.size() != 18){
            System.out.println("g1 not complete");
        }
        if(g2.allNeurons.size() != 18){
            System.out.println("g2 not complete");
        }

        Genome gSing = g1.mateSinglepoint(g2, 3);
        Genome gMult = g1.mateMultipoint(g2, 4, 100, 100);
        Genome gAvg = g1.mateMultipointAvg(g2, 5, 100, 100);

        if(gSing.allNeurons.size() != 18){
            System.out.println("gSing not complete, size: " + gSing.allNeurons.size());
        }
        if(gMult.allNeurons.size() != 18){
            System.out.println("gMult not complete, size: " + gMult.allNeurons.size());
        }
        if(gAvg.allNeurons.size() != 18){
            System.out.println("gAvg not complete, size: " + gAvg.allNeurons.size());
        }

        System.out.println("g1 = " + g1.allNeurons.size());
        System.out.println("g2 = " + g2.allNeurons.size());
        System.out.println("gSing = " + gSing.allNeurons.size());
        System.out.println("gMult = " + gMult.allNeurons.size());
        System.out.println("gAvg = " + gAvg.allNeurons.size());
    }
    
}