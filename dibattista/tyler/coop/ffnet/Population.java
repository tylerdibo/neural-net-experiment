package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;

public class Population{
    
    public List<Organism> organisms;
    public List<Innovation> innovations;
    public List<Species> species;
    
    int currentNodeId;
    double currentInnovNum;
    
    public Population(){
        organisms = new ArrayList<Organism>();
        innovations = new ArrayList<Innovation>();
        species = new ArrayList<Species>();
    }
    
    public void spawn(Genome g, int size){
        Genome newGenome = null;
        Organism newOrganism;
        for(int i = 1; i <= size; i++){
            newGenome = g.clone();
            newGenome.mConnectionWeights(1.0, 1.0, false);
            
            newOrganism = new Organism(0.0, newGenome, 1);
            organisms.add(newOrganism);
        }
        
        currentNodeId = newGenome.getLastNodeId();
        currentInnovNum = newGenome.getLastInnovNum();
    }
    
    public void speciate(){
        for(Organism o : organisms){
            
        }
    }
    
}