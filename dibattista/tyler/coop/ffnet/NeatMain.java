package dibattista.tyler.coop.ffnet;


import dibattista.tyler.coop.ffnet.minesweeper.NeatAI;
import minesweeper.ai.games.GameState;
import minesweeper.ai.games.NativeGameState;
import minesweeper.ai.games.Windows7GameState;
import minesweeper.ai.players.AIPlayer;

/**
 * Created by Tyler on 20/03/2016.
 */
public class NeatMain {

    public static void main(String[] inputs) {

        long startTime = System.nanoTime(); //store starting time to determine total running time

        GameState game = NativeGameState.createIntermediateGame();

        NeatAI neat = new NeatAI(16, 16);
        neat.solve(game);

        System.out.println("Time elapsed in ns: " + (System.nanoTime()-startTime));
    }
}
