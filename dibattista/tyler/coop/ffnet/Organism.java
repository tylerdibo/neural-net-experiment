package dibattista.tyler.coop.ffnet;

public class Organism{
    
    public double fitness;
    double originalFitness;
    double expectedOffspring;
    double highFit;
    public Genome genome;
    int generation;
    int superChampOffspring;
    Species species;
    boolean popChamp, popChampChild, champion;
    boolean mutStructBaby, mateBaby;
    boolean eliminate;
    
    public Organism(double fitness, Genome g, int gen){
        genome = g;
        generation = gen;
        this.fitness = fitness;
        originalFitness = fitness;
        species = null;
        expectedOffspring = 0.0;
        superChampOffspring = 0;
        popChamp = false;
        popChampChild = false;
        highFit = 0.0;
        mutStructBaby = false;
        mateBaby = false;
        eliminate = false;
        champion = false;
    }
    
}