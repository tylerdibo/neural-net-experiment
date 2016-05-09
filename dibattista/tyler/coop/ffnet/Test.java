package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Test{
    
    public static void main(String[] args){
        Test1();
    }

    static void Test1(){
        long startTime = System.nanoTime();
        
        double innovationNumber = 0.0;
        
        Genome geno = new Genome();
        Neuron n1 = new Neuron(Neuron.NeuronTypes.HIDDEN, 0);
        geno.addNeuron(n1);
        Neuron n2 = new Neuron(Neuron.NeuronTypes.HIDDEN, 1);
        geno.addNeuron(n2);
        Connection conn = new Connection(n1, n2, 2.0, 2);
        geno.links.add(conn);
        List<Innovation> innovs = new ArrayList<Innovation>();

        for(int i = 0; i < 10; i++){
            geno.mAddNeuron(innovs, innovationNumber);
            
            geno.mAddConnection(innovs, innovationNumber, 20);
        }
        
        geno.mConnectionWeights(2.0, 0.5, false);
        
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
        
        
        
        System.out.println("Time elapsed in ns: " + (System.nanoTime() - startTime));
    }
    
}