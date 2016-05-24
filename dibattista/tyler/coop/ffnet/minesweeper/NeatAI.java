package dibattista.tyler.coop.ffnet.minesweeper;

import dibattista.tyler.coop.ffnet.*;
import minesweeper.ai.games.GameState;
import minesweeper.ai.players.AIPlayer;

import java.util.ArrayList;

/**
 * Created by Tyler on 22/05/2016.
 */
public class NeatAI implements AIPlayer{

    Population pop;
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
            conn = new Connection(g.inputNeurons.get(i), n, 1.0, 0.0, false, 1.0);
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

        int iterCount = 0;
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
                highestId = 0;
                highest = 0.0;
                for (Double d : outputs) {
                    outputId = outputs.indexOf(d);
                    if (d > highest && inputs[outputId] == -1.0) {
                        highest = d;
                        highestId = outputId;
                    }
                }

                game.pick(highestId / rows, highestId % rows);

                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }*/
            }
            System.out.println(game + " " + iterCount++);

            o.fitness = (double) cellsCleared(game) * 10;

            //measure fitness with percentage of games won then average number of cells cleared?
            // or just average number of cells cleared

            game.restart();
        }
    }

    private int cellsCleared(GameState game){
        int cellCount = 0;
        for (int y = 0; y < columns; y++) {
            for (int x = 0; x < rows; x++) {
                if(game.getCell(y, x) == -1){
                    cellCount++;
                }
            }
        }

        return cellCount;
    }
}
