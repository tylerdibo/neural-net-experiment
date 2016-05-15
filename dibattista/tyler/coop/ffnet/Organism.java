package dibattista.tyler.coop.ffnet;

public class Organism{
    
    double fitness;
    Genome genome;
    int generation;
    
    public Organism(double fitness, Genome g, int gen){
        genome = g;
        generation = gen;
        this.fitness = fitness;
    }
    
}