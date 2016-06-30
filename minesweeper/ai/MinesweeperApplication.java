package minesweeper.ai;

import java.io.IOException;

import minesweeper.ai.games.GameState;
import minesweeper.ai.games.NativeGameState;
import minesweeper.ai.games.Windows7GameState;
import minesweeper.ai.players.AIPlayer;
import minesweeper.ai.players.AIPlayer.DebugMode;
import minesweeper.ai.players.ProbablisticSearchTreeAI;
import minesweeper.ai.players.SearchTreeAI;

public class MinesweeperApplication {
	
	public static void main(String... args) throws IOException, InterruptedException {
		
		GameState game = NativeGameState.createIntermediateGame();
		
		AIPlayer player = new ProbablisticSearchTreeAI(DebugMode.OFF);
		int wins = 0;
		long timer;
		for(int i = 0; i < 100; i++){
			timer = System.nanoTime();
			player.solve(game);
			if(game.getState() == GameState.State.WIN){
				wins++;
			}
			game.restart(true);
			System.out.println(System.nanoTime() - timer);
		}
		System.out.println("Wins: " + wins);
	}

}
