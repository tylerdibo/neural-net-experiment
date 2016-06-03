package dibattista.tyler.coop.ffnet;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class Population{

    private final static Logger LOGGER = Logger.getLogger(Population.class.getName());

    static final double COMPAT_THRESHOLD = 3.0;
    static final int DROPOFF_AGE = 15;
    public static final int POP_SIZE = 150; //150
    static final int BABIES_STOLEN = 0;
    
    public List<Organism> organisms;
    public List<Innovation> innovations;
    public ArrayList<Species> species;
    
    int currentNodeId;
    double currentInnovNum;
    int lastSpecies;
    double highestFitness;
    int highestLastChanged;
    
    public Population(Genome g, int size){
        organisms = new ArrayList<Organism>();
        innovations = new ArrayList<Innovation>();
        species = new ArrayList<Species>();
        highestFitness = 0.0;
        highestLastChanged = 0;

        spawn(g, size);
    }
    
    public void spawn(Genome g, int size){
        Genome newGenome = null;
        Organism newOrganism;
        for(int i = 1; i <= size; i++){
            newGenome = g.deepClone(i);
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

                if(comparison != null){
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
            totalExpected += s.expectedOffspring; //TODO this isn't right
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

        ArrayList<Species> sortedSpecies = (ArrayList<Species>) species.clone();
        Collections.sort(sortedSpecies, new Comparator<Species>() {
            @Override
            public int compare(Species o1, Species o2) {
                return Double.compare(o2.organisms.get(0).originalFitness, o1.organisms.get(0).originalFitness);
            }
        });
        
        Organism bestOrg = sortedSpecies.get(0).organisms.get(0);
        System.out.println("best fitness " + bestOrg.originalFitness);
        Species bestSpec = sortedSpecies.get(0);
        
        if(bestOrg.originalFitness > highestFitness){
            highestFitness = bestOrg.originalFitness;
            highestLastChanged = 0;
        }else{
            highestLastChanged++;
        }
        
        if(highestLastChanged >= DROPOFF_AGE+5){
            highestLastChanged = 0;
            int halfPop = POP_SIZE / 2;

            for(Species s : sortedSpecies){
                s.expectedOffspring = 0;
            }

            bestSpec.ageOfLastImprovement = bestSpec.age;
            if(sortedSpecies.size() == 1){
                bestOrg.superChampOffspring = halfPop;
                bestSpec.expectedOffspring = halfPop;
            }else{
                bestOrg.superChampOffspring = POP_SIZE;
                bestSpec.expectedOffspring = halfPop;

                Species species2 = sortedSpecies.get(1);
                species2.organisms.get(0).superChampOffspring = POP_SIZE - halfPop;
                species2.expectedOffspring = POP_SIZE - halfPop;
                species2.ageOfLastImprovement = species2.age;
            }
        }else if(BABIES_STOLEN > 0){ //transfer babies of weaker species to stronger ones
            int stolenBabies = 0;
            ListIterator<Species> speciesIter = sortedSpecies.listIterator(sortedSpecies.size());
            Species s;
            while(speciesIter.hasPrevious() && stolenBabies < BABIES_STOLEN){
                s = speciesIter.previous();
                if(s.age > 5 && s.expectedOffspring > 2){
                    if((s.expectedOffspring-1) >= BABIES_STOLEN - stolenBabies){
                        s.expectedOffspring -= BABIES_STOLEN - stolenBabies;
                        stolenBabies = BABIES_STOLEN;
                    }else{
                        stolenBabies += s.expectedOffspring-1;
                        s.expectedOffspring = 1;
                    }
                }
            }

            int fifthStolen = BABIES_STOLEN / 5;
            int tenthStolen = BABIES_STOLEN / 10;
            speciesIter = sortedSpecies.listIterator();
            do{
                s = speciesIter.next();
            }while(s.lastImproved() > DROPOFF_AGE && speciesIter.hasNext());

            if(stolenBabies >= fifthStolen && speciesIter.hasNext()){
                s.organisms.get(0).superChampOffspring = fifthStolen;
                s.expectedOffspring += fifthStolen;
                stolenBabies -= fifthStolen;
            }

            do{
                s = speciesIter.next();
            }while(s.lastImproved() > DROPOFF_AGE && speciesIter.hasNext());

            if(stolenBabies >= fifthStolen && speciesIter.hasNext()){
                s.organisms.get(0).superChampOffspring = fifthStolen;
                s.expectedOffspring += fifthStolen;
                stolenBabies -= fifthStolen;
            }
            do{
                s = speciesIter.next();
            }while(s.lastImproved() > DROPOFF_AGE && speciesIter.hasNext());

            if(stolenBabies >= tenthStolen && speciesIter.hasNext()){
                s.organisms.get(0).superChampOffspring = tenthStolen;
                s.expectedOffspring += tenthStolen;
                stolenBabies -= tenthStolen;
            }

            do{
                s = speciesIter.next();
            }while(s.lastImproved() > DROPOFF_AGE && speciesIter.hasNext());

            while(stolenBabies > 0 && speciesIter.hasNext()){
                if(ThreadLocalRandom.current().nextDouble() > 0.1){
                    if(stolenBabies > 3){
                        s.organisms.get(0).superChampOffspring = 3;
                        s.expectedOffspring += 3;
                        stolenBabies -= 3;
                    }else{
                        s.organisms.get(0).superChampOffspring = stolenBabies;
                        s.expectedOffspring += stolenBabies;
                        stolenBabies = 0;
                    }
                }

                do{
                    s = speciesIter.next();
                }while(s.lastImproved() > DROPOFF_AGE && speciesIter.hasNext());
            }

            if(stolenBabies > 0){
                s = sortedSpecies.get(0);
                s.organisms.get(0).superChampOffspring += stolenBabies;
                s.expectedOffspring += stolenBabies;
                stolenBabies = 0;
            }
        }

        //Kill marked organisms
        Organism currentOrg;
        for(Iterator<Organism> o = organisms.iterator(); o.hasNext(); ){
            currentOrg = o.next();
            if(currentOrg.eliminate){
                currentOrg.species.organisms.remove(currentOrg);
                o.remove();
            }
        }

        //reproduction
        ArrayList<Species> oldSpecies = (ArrayList<Species>)species.clone();
        for(Species s : oldSpecies){
            s.reproduce(gen, this, sortedSpecies);
        }

        for(Organism o : organisms){
            o.species.organisms.remove(o);
        }
        organisms.clear();

        int orgCount = 0;
        Iterator<Species> iterSpec = species.iterator();
        Species s;
        while(iterSpec.hasNext()){
            s = iterSpec.next();
            if(s.organisms.isEmpty()){
                iterSpec.remove();
            }else{
                if(s.novel){
                    s.novel = false;
                }else{
                    s.age++;
                }

                for(Organism o : s.organisms){
                    o.genome.id = orgCount++;
                    organisms.add(o);
                }
            }
        }

        LOGGER.info("Epoch complete");

        innovations.clear();
    }
}