package dibattista.tyler.coop.ffnet;

public class Organism{
    
    double fitness;
    double originalFitness;
    double expectedOffspring;
    double highFit;
    Genome genome;
    int generation;
    int superChampOffspring;
    Species species;
    boolean popChamp, popChampChild;
    boolean mutStructBaby, mateBaby;
    
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
    }
    
}