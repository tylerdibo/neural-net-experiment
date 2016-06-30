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
    static final int GENERATIONS = 2000;
    static final int RUNS_PER_ORGANISM = 1;

    public Population pop;
    Visualization viz;
    int numTiles, rows, columns;

    public NeatAI(int rows, int columns){
        Genome g = new Genome();
        numTiles = rows*columns;
        this.rows = rows;
        this.columns = columns;

        int nodeID = 0;
        Neuron bias = new Neuron(Neuron.NeuronTypes.INPUT, nodeID);
        g.addNeuron(bias);
        nodeID++;
        for(int i = 0; i < numTiles; i++) {
            g.addNeuron(new Neuron(Neuron.NeuronTypes.INPUT, nodeID));
            nodeID++;
        }
        Connection conn, biasConn;
        Neuron n;
        for(int i = 0; i < numTiles; i++) {
            n = new Neuron(Neuron.NeuronTypes.OUTPUT, nodeID);
            nodeID++;
            g.addNeuron(n);
            /*for(int ii = 0; ii < numTiles; ii++) {
                conn = new Connection(g.inputNeurons.get(ii), n, 1.0, ii + (i*numTiles) + (numTiles * 2), false, 1.0);
                n.addConnection(conn);
                g.links.add(conn);
            }*/
            conn = new Connection(g.inputNeurons.get(i+1), n, 1.0, nodeID + numTiles, false, 1.0);
            n.addConnection(conn);
            g.links.add(conn);
            biasConn = new Connection(bias, n, 1.0, nodeID + numTiles*2, false, 1.0);
            n.addConnection(biasConn);
            g.links.add(biasConn);
        }

        pop = new Population(g, Population.POP_SIZE);
        //viz = new Visualization(pop, rows, columns);
    }

    @Override
    public void solve(GameState game) {
        double[] inputs = new double[numTiles+1];
        int[] flags = new int[40];
        int tileCount;
        int highestId, lowestId, outputId;
        double highest, lowest;
        Genome genome;
        ArrayList<Double> outputs;
        double totalGenFitness;
        double averageOf10 = 0;
        int iteration;
        long timer;

        for(int gen = 1; gen <= GENERATIONS; gen++) {
            timer = System.nanoTime();
            totalGenFitness = 0.0;
            for (int run = 0; run < RUNS_PER_ORGANISM; run++) {
                for (Organism o : pop.organisms) {
                    iteration = 0;
                    genome = o.genome;
                    /*viz.setOrganism(o);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    while (game.getState() == GameState.State.IN_PROGRESS) {
                        inputs[0] = 1.0;
                        tileCount = 1;
                        for (int y = 0; y < rows; y++) {
                            for (int x = 0; x < columns; x++) {
                                inputs[tileCount++] = (double) game.getCell(y, x);
                            }
                        }

                        genome.loadSensors(inputs);

                        outputs = genome.calculate();

                        highest = 0.0;
                        highestId = -1;
                        lowest = 1.0;
                        lowestId = -1;
                        outputId = 0;
                        for (Double d : outputs) {
                            if (d > highest && inputs[outputId+1] == -1) {
                                highest = d;
                                highestId = outputId;
                            } else if (d < lowest && inputs[outputId+1] == -1) {
                                lowest = d;
                                lowestId = outputId;
                            }
                            outputId++;
                        }

                        game.pick(highestId / rows, highestId % rows);
                        if(iteration >= 40)
                            game.flag(flags[iteration%40] / rows, flags[iteration%40] % rows);
                        if (lowestId == -1)
                            System.out.println(lowestId);
                        else
                            game.flag(lowestId / rows, lowestId % rows);
                        flags[iteration % 40] = lowestId;

                    /*System.out.print(game);
                    System.out.println(" Click at row: " + (highestId / rows) + " column: " + (highestId % rows));*/

                        iteration++;
                    }

                    if(game.getState() == GameState.State.WIN){
                        System.out.println("*******************************WIN*****************************");
                    }

                    o.genome.reset();

                    o.fitness += (double) cellsCleared(inputs) *10;

                    //System.out.println("fitness: " + o.fitness);

                    //measure fitness with percentage of games won then average number of cells cleared?
                    // or just average number of cells cleared

                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }*/

                    game.restart(false);
                }
            }

            for(Organism o : pop.organisms){
                totalGenFitness += o.fitness;
                /*if(o.genome.links.size() != 6561){
                    return;
                }*/
            }

            System.out.println("Gen " + gen + " average fitness " + (totalGenFitness / pop.organisms.size() / RUNS_PER_ORGANISM));
            //System.out.print(totalGenFitness / pop.organisms.size() / RUNS_PER_ORGANISM + " ");
            averageOf10 += (totalGenFitness / pop.organisms.size() / RUNS_PER_ORGANISM);
            if (gen % 10 == 0) {
                System.out.println("Average of last 10 " + (averageOf10 / 10));
                averageOf10 = 0;
            }

            if (gen == (GENERATIONS - 1)) {
                game = Windows7GameState.createIntermediateGame();
                continue;
            }

            epoch(gen);

            game.restart(true);

            //System.out.println(System.nanoTime() - timer);
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
