import dibattista.tyler.coop.ffnet.Connection;
import dibattista.tyler.coop.ffnet.Genome;
import dibattista.tyler.coop.ffnet.Innovation;
import dibattista.tyler.coop.ffnet.Neuron;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Test{
    
    public static void main(String[] args){
        Test2();
    }

    static void Test1(){
        Genome geno = new Genome();
        Connection conn = new Connection(new Neuron(0), new Neuron(1), 2.0, 2);
        geno.links.add(conn);
        List<Innovation> innovs = new ArrayList<Innovation>();

        for(int i = 0; i < 10; i++){
            geno.mAddNeuron(innovs);
        }
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

        for(int i = 0; i < 10000; i++){
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

        System.out.println(System.nanoTime() - startTime);
    }
    
}