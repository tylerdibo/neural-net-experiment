package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Population{

    static final double COMPAT_THRESHOLD = 3.0;
    
    public List<Organism> organisms;
    public List<Innovation> innovations;
    public ArrayList<Species> species;
    
    int currentNodeId;
    double currentInnovNum;
    int lastSpecies;
    
    public Population(Genome g, int size){
        organisms = new ArrayList<Organism>();
        innovations = new ArrayList<Innovation>();
        species = new ArrayList<Species>();

        spawn(g, size);
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

        speciate();
    }
    
    public void speciate(){
        Organism comparison = null;
        Species newSpecies;

        int speciesCount = 0;

        for(Organism o : organisms){
            if(species.isEmpty()){
                newSpecies = new Species(++speciesCount);
                species.add(newSpecies);
                newSpecies.addOrganism(o);
                o.species = newSpecies;
            }else {
                for (Species s : species) {
                    comparison = s.organisms.get(0);
                    if (o.genome.compatibility(comparison.genome) < COMPAT_THRESHOLD) {
                        s.addOrganism(o);
                        o.species = s;
                        comparison = null; //mark that a matching species has been found
                        break;
                    }
                }

                if(comparison == null){
                    newSpecies = new Species(++speciesCount);
                    species.add(newSpecies);
                    newSpecies.addOrganism(o);
                    o.species = newSpecies;
                }
            }
        }

        lastSpecies = speciesCount;
    }

    public void epoch(int gen){
        double total = 0.0;
        double averageFitness;
        int totalOrganisms = organisms.size();

        for(Species s : species){
            s.adjustFitness();
        }

        for(Organism o : organisms){
            total += o.fitness;
        }
        averageFitness = total / totalOrganisms;

        for(Organism o : organisms){
            o.expectedOffspring = o.fitness / averageFitness;
        }

        double skim = 0.0;
        int totalExpected = 0;
        for(Species s : species){
            skim = s.countOffspring(skim);
            totalExpected += s.expectedOffspring;
        }

        if(totalExpected < totalOrganisms){
            int maxExpected = 0;
            int finalExpected = 0;
            Species bestSpecies = species.get(0);
            for(Species s : species){
                if(s.expectedOffspring >= maxExpected) {
                    maxExpected = s.expectedOffspring;
                    bestSpecies = s;
                }
                finalExpected += s.expectedOffspring;
            }

            bestSpecies.expectedOffspring++;
            finalExpected++;

            if(finalExpected < totalOrganisms){
                for(Species s : species){
                    s.expectedOffspring = 0;
                }
                bestSpecies.expectedOffspring = totalOrganisms;
            }
        }

        List<Species> sortedSpecies = (List<Species>) species.clone();
        Collections.sort(sortedSpecies, new Comparator<Species>() {
            @Override
            public int compare(Species o1, Species o2) {
                return Double.compare(o1.organisms.get(0).originalFitness, o2.organisms.get(0).originalFitness);
            }
        });

        for(Species s : sortedSpecies){

        }
    }
    
}