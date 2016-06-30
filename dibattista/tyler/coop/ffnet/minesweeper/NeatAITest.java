package dibattista.tyler.coop.ffnet.minesweeper;

import dibattista.tyler.coop.ffnet.*;
import minesweeper.ai.games.GameState;
import minesweeper.ai.games.Windows7GameState;
import minesweeper.ai.players.AIPlayer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Tyler on 22/05/2016.
 */
public class NeatAITest implements AIPlayer{
    //TODO: should implement file writing methods to be able to pause the program
    static final int GENERATIONS = 2000;

    public Population pop;
    Visualization viz;
    int numTiles, rows, columns;

    public NeatAITest(int rows, int columns){
        Genome g = new Genome();
        numTiles = rows*columns;
        this.rows = rows;
        this.columns = columns;

        int nodeID = 0;
        for(int i = 0; i < 9; i++) {
            g.addNeuron(new Neuron(Neuron.NeuronTypes.INPUT, nodeID));
            nodeID++;
        }
        Connection conn;
        Neuron out = new Neuron(Neuron.NeuronTypes.OUTPUT, 9);
        g.addNeuron(out);
        for(int i = 0; i < 9; i++){
            conn = new Connection(g.inputNeurons.get(i), out, 1.0, i + 10, false, 1.0);
            out.addConnection(conn);
            g.links.add(conn);
        }

        pop = new Population(g, Population.POP_SIZE);
        //viz = new Visualization(pop, rows, columns);
    }

    @Override
    public void solve(GameState game) {
        double[][] inputs = new double[columns+2][rows+2];
        double[][] actualInputs = new double[columns][rows];
        int[][] flags = new int[40][];
        int tileCount;
        int iteration;
        int highestId, outputId;
        double highest, lowest;
        Genome genome;
        ArrayList<Double> outputs;
        double totalGenFitness;
        double averageOf10 = 0;
        long timer;

        for(int gen = 1; gen <= GENERATIONS; gen++){
            timer = System.nanoTime();
            totalGenFitness = 0.0;
            for(Organism o : pop.organisms) {
                genome = o.genome;
                iteration = 0;
                while (game.getState() == GameState.State.IN_PROGRESS) {
                    for (int y = 0; y < rows; y++) {
                        for (int x = 0; x < columns; x++) {
                            inputs[y+1][x+1] = (double) game.getCell(y, x);
                        }
                    }

                    double[][] totalOutputs = new double[rows][columns];

                    //create 3x3 input box to loop the genome through
                    double[] inputBox = new double[9];
                    for(int r = 1; r < rows+1; r++){
                        for(int c = 1; c < columns+1; c++){
                            inputBox[0] = inputs[r-1][c-1];
                            inputBox[1] = inputs[r-1][c];
                            inputBox[2] = inputs[r-1][c+1];
                            inputBox[3] = inputs[r][c-1];
                            inputBox[4] = inputs[r][c];
                            inputBox[5] = inputs[r][c+1];
                            inputBox[6] = inputs[r+1][c-1];
                            inputBox[7] = inputs[r+1][c];
                            inputBox[8] = inputs[r+1][c+1];

                            genome.loadSensors(inputBox);
                            outputs = genome.calculate();
                            totalOutputs[r-1][c-1] = outputs.get(0);
                            genome.reset();
                        }
                    }

                    highest = 0.0;
                    int highestR = -1, highestC = -1;
                    lowest = 1.0;
                    int lowestR = -1, lowestC = -1;
                    for(int r = 0; r < totalOutputs.length; r++){
                        for(int c = 0; c < totalOutputs[0].length; c++){
                            if(totalOutputs[r][c] > highest && inputs[r+1][c+1] == -1){
                                highest = totalOutputs[r][c];
                                highestR = r;
                                highestC = c;
                            }else if(totalOutputs[r][c] < lowest && inputs[r+1][c+1] == -1){
                                lowest = totalOutputs[r][c];
                                lowestR = r;
                                lowestC = c;
                            }
                        }
                    }

                    //if(highestId < 0){
                        //System.out.println("No open output found.");
                    //}else{
                    game.pick(highestR, highestC);
                    if(iteration >= 40)
                        game.flag(flags[iteration%40][0], flags[iteration%40][1]);
                    game.flag(lowestR, lowestC);
                    int[] flag = {lowestR, lowestC};
                    flags[iteration%40] = flag;

                    //}

                    //System.out.print(game);
                    //System.out.println(" Click at row: " + bestR+1 + " column: " + bestC+1);

                    iteration++;
                }

                if(game.getState() == GameState.State.WIN){
                    System.out.println("*******************************WIN*****************************");
                }

                o.genome.reset();

                for(int i = 0; i < rows; i++){
                    actualInputs[i] = Arrays.copyOfRange(inputs[i+1], 1, columns+1);
                }

                o.fitness = (double) cellsCleared(actualInputs) * 10;
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
            //System.out.print(totalGenFitness / pop.organisms.size() + " ");
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

            game.restart(false);

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

    private int cellsCleared(double[][] inputs){
        int cellCount = 0;
        for(int j = 0; j < inputs.length; j++){
            for(int k = 0; k < inputs[0].length; k++){
                if(inputs[j][k] != -1){
                    cellCount++;
                }
            }
        }

        return cellCount;
    }
}
