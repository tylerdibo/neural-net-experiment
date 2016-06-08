package dibattista.tyler.coop.ffnet.minesweeper;

import dibattista.tyler.coop.ffnet.*;
import minesweeper.ai.games.GameState;
import minesweeper.ai.games.Windows7GameState;
import minesweeper.ai.players.AIPlayer;

import java.util.ArrayList;

/**
 * Created by Tyler on 22/05/2016.
 */
public class NeatAI implements AIPlayer{
    //TODO: should implement file writing methods to be able to pause the program
    static final int GENERATIONS = 100000;

    public Population pop;
    int numTiles, rows, columns;

    public NeatAI(int rows, int columns){
        Genome g = new Genome();
        numTiles = rows*columns;
        this.rows = rows;
        this.columns = columns;

        for(int i = 0; i < numTiles; i++) {
            g.addNeuron(new Neuron(Neuron.NeuronTypes.INPUT, i));
        }
        Connection conn;
        Neuron n;
        for(int i = 0; i < numTiles; i++) {
            n = new Neuron(Neuron.NeuronTypes.OUTPUT, i+numTiles);
            g.addNeuron(n);
            conn = new Connection(g.inputNeurons.get((i+1) % numTiles), n, 1.0, i + (numTiles*2), false, 1.0);
            n.addConnection(conn);
            g.links.add(conn);
        }

        pop = new Population(g, Population.POP_SIZE);
    }

    @Override
    public void solve(GameState game) {
        double[] inputs = new double[numTiles];
        int tileCount;
        int highestId, outputId;
        double highest;
        Genome genome;
        ArrayList<Double> outputs;
        double totalGenFitness;
        double averageOf10 = 0;

        int iterCount = 0;
        for(int gen = 1; gen <= GENERATIONS; gen++){
            totalGenFitness = 0.0;
            for(Organism o : pop.organisms) {
                genome = o.genome;
                while (game.getState() == GameState.State.IN_PROGRESS) {
                    tileCount = 0;
                    for (int y = 0; y < columns; y++) {
                        for (int x = 0; x < rows; x++) {
                            inputs[tileCount++] = (double) game.getCell(y, x);
                        }
                    }

                    genome.loadSensors(inputs);

                    outputs = genome.calculate();

                    highest = 0.0;
                    highestId = -1;
                    for (Double d : outputs) {
                        outputId = outputs.indexOf(d);
                        if (d > highest && inputs[outputId] == -1) {
                            highest = d;
                            highestId = outputId;
                        }
                    }

                    //if(highestId < 0){
                        //System.out.println("No open output found.");
                    //}else{
                    game.pick(highestId / rows, highestId % rows);
                    //}

                    /*System.out.print(game);
                    System.out.println(" Click at row: " + (highestId / rows) + " column: " + (highestId % rows));*/
                }

                o.genome.reset();

                o.fitness = (double) cellsCleared(inputs) * 10;
                totalGenFitness += o.fitness;

                //System.out.println(game + " " + (++iterCount) + " ended, fitness: " + o.fitness);

                //measure fitness with percentage of games won then average number of cells cleared?
                // or just average number of cells cleared

                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }*/

                game.restart(false);
            }

            System.out.println("Gen " + gen + " average fitness " + (totalGenFitness / pop.organisms.size()));
            averageOf10 += (totalGenFitness / pop.organisms.size());
            if(gen % 10 == 0){
                System.out.println("Average of last 10 " + (averageOf10 / 10));
                averageOf10 = 0;
            }

            if(gen == (GENERATIONS - 1)){
                game = Windows7GameState.createIntermediateGame();
                continue;
            }
            
            epoch(gen);

            game.restart(true);
        }
    }
    
    public void epoch(int gen){
        for(Species s : pop.species){
            s.getAvgFitness();
            s.getMaxFitness();
        }

        pop.epoch(gen);
    }

    private int cellsCleared(double[] inputs){
        int cellCount = 0;
        for(int j = 0; j < inputs.length; j++){
            if(inputs[j] != -1){
                cellCount++;
            }
        }

        return cellCount;
    }
}
