package minesweeper.ai;

import java.io.IOException;

import minesweeper.ai.games.GameState;
import minesweeper.ai.games.Windows7GameState;
import minesweeper.ai.players.AIPlayer;
import minesweeper.ai.players.AIPlayer.DebugMode;
import minesweeper.ai.players.ProbablisticSearchTreeAI;

public class MinesweeperApplication {
	
	public static void main(String... args) throws IOException, InterruptedException {
		
		GameState game = Windows7GameState.createAdvancedGame();
		
		AIPlayer player = new ProbablisticSearchTreeAI(DebugMode.ON);
		player.solve(game);
		Thread.sleep(3333);
		System.out.println(game.getState());
		
	}

}
