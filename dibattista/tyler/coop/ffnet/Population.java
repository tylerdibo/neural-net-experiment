package dibattista.tyler.coop.ffnet;

public class Population{
    
    public List<Organism> organisms;
    public List<Innovation> innovations;
    
    public Population(){
        organisms = new ArrayList<Organism>();
        innovations = new ArrayList<Innovation>();
    }
    
}