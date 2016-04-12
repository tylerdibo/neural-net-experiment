import dibattista.tyler.coop.ffnet.Connection;
import dibattista.tyler.coop.ffnet.Genome;
import dibattista.tyler.coop.ffnet.Innovation;
import dibattista.tyler.coop.ffnet.Neuron;

import java.util.ArrayList;
import java.util.List;

public class Test{
    
    public static void main(String[] args){
        Genome geno = new Genome();
        Connection conn = new Connection(new Neuron(0), new Neuron(1), 2.0, 2);
        geno.links.add(conn);
        List<Innovation> innovs = new ArrayList<Innovation>();

        for(int i = 0; i < 10; i++){
            geno.mAddNeuron(innovs);
        }

    }
    
}