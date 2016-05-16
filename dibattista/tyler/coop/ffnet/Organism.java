package dibattista.tyler.coop.ffnet;

public class Organism{
    
    double fitness;
    double originalFitness;
    double expectedOffspring;
    Genome genome;
    int generation;
    Species species;
    
    public Organism(double fitness, Genome g, int gen){
        genome = g;
        generation = gen;
        this.fitness = fitness;
        originalFitness = fitness;
        species = null;
        expectedOffspring = 0.0;
    }
    
}