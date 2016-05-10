package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;

public class Population{
    
    public List<Organism> organisms;
    public List<Innovation> innovations;
    
    public Population(){
        organisms = new ArrayList<Organism>();
        innovations = new ArrayList<Innovation>();
    }
    
    public void spawn(Genome g, int size){
        for(int i = 1; i <= size; i++){
            
        }
    }
    
}