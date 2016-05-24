package dibattista.tyler.coop.ffnet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Tyler on 15/05/2016.
 */
public class Species {

    static final int DROPOFF_AGE = 15;
    static final double AGE_SIGNIFICANCE = 1.0;
    static final double MUTATE_ADD_LINK_PROB = 0.05;
    static final double MUTATE_ADD_NODE_PROB = 0.03;
    static final double MUTATE_LINK_WEIGHTS_PROB = 0.9;
    static final double MUTATE_TOGGLE_ENABLE_PROB = 0.01;
    static final double MUTATE_GENE_REENABLE_PROB = 0.001;
    static final double WEIGHT_MUT_POWER = 2.5;
    static final double MUTATE_ONLY_PROB = 0.25;
    static final double INTERSPECIES_MATE_RATE = 0.001;
    static final double MATE_MULTIPOINT_PROB = 0.6;
    static final double MATE_MULTIPOINT_AVG_PROB = 0.4;
    static final double MATE_SINGLEPOINT_PROB = 0.0;
    static final double MATE_ONLY_PROB = 0.2;
    static final double SURVIVAL_THRESH = 0.2;
    static final int NEWLINK_TRIES = 20;

    int id, age, ageOfLastImprovement;
    double avgFitness, maxFitness, maxFitnessEver;
    int expectedOffspring;
    List<Organism> organisms;
    boolean novel;

    public Species(int id, boolean novel){
        organisms = new ArrayList<Organism>();
        this.id = id;
        this.novel = novel;
        age = 1;
        ageOfLastImprovement = 0;
        expectedOffspring = 0;
    }
    
    public Species(int id){
        this(id, false);
    }
    
    public void reproduce(int gen, Population pop, List<Species> sortedSpecies){
        Organism mom, dad, baby;
        Organism theChamp = organisms.get(0);
        Genome babyGenome;
        boolean mutStructBaby, mateBaby;
        boolean outsideSpecies;
        boolean champDone = false;
        int poolSize = organisms.size();
        for(int i = 0; i < expectedOffspring; i++){
            mutStructBaby = false;
            mateBaby = false;
            outsideSpecies = false;

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
                
                if(theChamp.superChampOffspring == 1){
                    if(theChamp.popChamp){
                        baby.popChampChild = true;
                        baby.highFit = mom.originalFitness;
                    }
                }
                
                theChamp.superChampOffspring--;
            }else if(!champDone && expectedOffspring > 5){
                mom = theChamp;
                babyGenome = mom.genome.deepClone(i);
                baby = new Organism(0.0, babyGenome, gen);
                champDone = true;
            }else if(ThreadLocalRandom.current().nextDouble() < MUTATE_ONLY_PROB || poolSize == 1){
                mom = organisms.get(ThreadLocalRandom.current().nextInt(organisms.size()));
                babyGenome = mom.genome.deepClone(i);
                
                if(ThreadLocalRandom.current().nextDouble() < MUTATE_ADD_NODE_PROB){
                    babyGenome.mAddNeuron(pop.innovations, pop.currentInnovNum, pop.currentNodeId);
                    mutStructBaby = true;
                }else if(ThreadLocalRandom.current().nextDouble() < MUTATE_ADD_LINK_PROB){
                    babyGenome.mAddConnection(pop.innovations, pop.currentInnovNum, NEWLINK_TRIES);
                    mutStructBaby = true;
                }else{ //if no structual mutation, do others
                    if(ThreadLocalRandom.current().nextDouble() < MUTATE_LINK_WEIGHTS_PROB){
                        babyGenome.mConnectionWeights(WEIGHT_MUT_POWER, 1.0, false);
                    }
                    if(ThreadLocalRandom.current().nextDouble() < MUTATE_TOGGLE_ENABLE_PROB){
                        babyGenome.mToggleEnable(1);
                    }
                    if(ThreadLocalRandom.current().nextDouble() < MUTATE_GENE_REENABLE_PROB){
                        babyGenome.mReenableFirst();
                    }
                }
                
                baby = new Organism(0.0, babyGenome, gen);
            }else{
                mom = organisms.get(ThreadLocalRandom.current().nextInt(organisms.size()));
                if(ThreadLocalRandom.current().nextDouble() > INTERSPECIES_MATE_RATE){
                    dad = organisms.get(ThreadLocalRandom.current().nextInt(organisms.size()));
                }else{
                    Species randSpecies = this;
                    int giveUp = 0;
                    double randMult;
                    int randSpeciesNum;
                    while(randSpecies == this && giveUp < 5){
                        randMult = ThreadLocalRandom.current().nextGaussian() / 4;
                        if(randMult > 1.0)
                            randMult = 1.0;
                        
                        randSpeciesNum = (int) Math.round(randMult*(sortedSpecies.size()-1.0));
                        randSpecies = sortedSpecies.get(randSpeciesNum);
                        giveUp++;
                    }
                    
                    dad = randSpecies.organisms.get(0);
                    outsideSpecies = true;
                }
                
                if(ThreadLocalRandom.current().nextDouble() < MATE_MULTIPOINT_PROB){
                    babyGenome = mom.genome.mateMultipoint(dad.genome, i, mom.originalFitness, dad.originalFitness);
                }else if(ThreadLocalRandom.current().nextDouble() < (MATE_MULTIPOINT_AVG_PROB / (MATE_MULTIPOINT_AVG_PROB+MATE_SINGLEPOINT_PROB))){
                    babyGenome = mom.genome.mateMultipointAvg(dad.genome, i, mom.originalFitness, dad.originalFitness);
                }else{
                    babyGenome = mom.genome.mateSinglepoint(dad.genome, i);
                }
                
                mateBaby = true;
                
                if(ThreadLocalRandom.current().nextDouble() > MATE_ONLY_PROB || 
                        dad.genome.id == mom.genome.id || 
                        dad.genome.compatibility(mom.genome) == 0.0){
                    if(ThreadLocalRandom.current().nextDouble() < MUTATE_ADD_NODE_PROB){
                        babyGenome.mAddNeuron(pop.innovations, pop.currentInnovNum, pop.currentNodeId);
                        mutStructBaby = true;
                    }else if(ThreadLocalRandom.current().nextDouble() < MUTATE_ADD_LINK_PROB){
                        babyGenome.mAddConnection(pop.innovations, pop.currentInnovNum, NEWLINK_TRIES);
                        mutStructBaby = true;
                    }else{
                        if(ThreadLocalRandom.current().nextDouble() < MUTATE_LINK_WEIGHTS_PROB){
                            babyGenome.mConnectionWeights(WEIGHT_MUT_POWER, 1.0, false);
                        }
                        if(ThreadLocalRandom.current().nextDouble() < MUTATE_TOGGLE_ENABLE_PROB){
                            babyGenome.mToggleEnable(1);
                        }
                        if(ThreadLocalRandom.current().nextDouble() < MUTATE_GENE_REENABLE_PROB){
                            babyGenome.mReenableFirst();
                        }
                    }
                }
                
                baby = new Organism(0.0, babyGenome, gen);
            }
            
            baby.mutStructBaby = mutStructBaby;
            baby.mateBaby = mateBaby;
            
            boolean foundSpecies = false;
            Species newSpecies;
            if(pop.species.isEmpty()){
                newSpecies = new Species(++(pop.lastSpecies), true);
                pop.species.add(newSpecies);
                newSpecies.addOrganism(baby);
                baby.species = newSpecies;
            }else{
                Organism compOrg;
                for(Species s : pop.species){
                    compOrg = s.organisms.get(0);
                    
                    if(compOrg == null){
                        continue;
                    }else if(baby.genome.compatibility(compOrg.genome) < Population.COMPAT_THRESHOLD){
                        s.addOrganism(baby);
                        baby.species = s;
                        foundSpecies = true;
                        break;
                    }
                }
                
                if(!foundSpecies){
                    newSpecies = new Species(++(pop.lastSpecies), true);
                    pop.species.add(newSpecies);
                    newSpecies.addOrganism(baby);
                    baby.species = newSpecies;
                }
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

            o.fitness = o.fitness / organisms.size();
        }

        Collections.sort(organisms, new Comparator<Organism>() {
            @Override
            public int compare(Organism o1, Organism o2) {
                return Double.compare(o1.fitness, o2.fitness);
            }
        });

        Organism champ = organisms.get(0);
        if(champ.originalFitness > maxFitnessEver){
            ageOfLastImprovement = age;
            maxFitnessEver = champ.originalFitness;
        }

        int numParents = (int) Math.floor((SURVIVAL_THRESH * (double) organisms.size()) + 1.0);

        champ.champion = true;
        int count = 1;
        for(Organism o : organisms){
            if(count > numParents){
                o.eliminate = true;
            }
            count++;
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

    public int lastImproved(){
        return age - ageOfLastImprovement;
    }

}
