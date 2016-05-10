package dibattista.tyler.coop.ffnet;

public class Organism{
    
    double fitness;
    Genome genome;
    int generation;
    
    public Organism(Genome g, int gen){
        genome = g;
        generation = gen;
    }
    
}