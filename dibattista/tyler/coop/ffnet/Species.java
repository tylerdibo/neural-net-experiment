package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Tyler on 15/05/2016.
 */
public class Species {

    static final int DROPOFF_AGE = 15;
    static final double AGE_SIGNIFICANCE = 1.0;
    static final double MUTATE_ADD_LINK_PROB = 0.05;
    static final double WEIGHT_MUT_POWER = 2.5;
    static final int NEWLINK_TRIES = 20;

    int id, age, ageOfLastImprovement;
    double avgFitness, maxFitness, maxFitnessEver;
    int expectedOffspring;
    List<Organism> organisms;

    public Species(int id){
        organisms = new ArrayList<Organism>();
        this.id = id;
        age = 1;
        ageOfLastImprovement = 0;
        expectedOffspring = 0;
    }
    
    public void reproduce(int gen, Population pop, List<Species> sortedSpecies){
        Organism mom, dad, baby;
        Organism theChamp = organisms.get(0);
        Genome babyGenome;
        boolean mutStructBaby, mateBaby;
        boolean outside;
        for(int i = 0; i < expectedOffspring; i++){
            mutStructBaby = false;
            mateBaby = false;
            outside = false;

            if(theChamp.superChampOffspring > 0){
                mom = theChamp;
                babyGenome = mom.genome.deepClone(i);

                if(theChamp.superChampOffspring > 1){
                    if(ThreadLocalRandom.current().nextDouble() < 0.8 || MUTATE_ADD_LINK_PROB == 0.0){
                        babyGenome.mConnectionWeights(WEIGHT_MUT_POWER, 1.0, false);
                    }else{
                        babyGenome.mAddConnection(pop.innovations, pop.currentInnovNum, NEWLINK_TRIES);
                        mutStructBaby = true;
                    }
                }

                baby = new Organism(0.0, babyGenome, gen);
            }
        }
    }

    public void adjustFitness(){
        int ageDebt = (age - ageOfLastImprovement + 1) - DROPOFF_AGE;

        for(Organism o : organisms){
            o.originalFitness = o.fitness;

            if(ageDebt >= 1){
                o.fitness = o.fitness/100;
            }

            if(age <= 10)
                o.fitness = o.fitness * AGE_SIGNIFICANCE;

            if(o.fitness < 0.0)
                o.fitness = 0.0001;
        }
    }

    public double countOffspring(double skim){
        int expOffIntPart;
        double expOffFracPart;
        double skimIntPart;

        for(Organism o : organisms){
            expOffIntPart = (int) Math.floor(o.expectedOffspring);
            expOffFracPart = o.expectedOffspring % 1.0;

            expectedOffspring += expOffIntPart;
            skim += expOffFracPart;

            if(skim > 1.0){
                skimIntPart = Math.floor(skim);
                expectedOffspring += (int) skimIntPart;
                skim -= skimIntPart;
            }
        }

        return skim;
    }

    public void addOrganism(Organism o){
        organisms.add(o);
    }

}
