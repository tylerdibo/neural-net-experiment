package minesweeper.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import minesweeper.ai.games.BoardInfoHelper;
import minesweeper.ai.games.MutableBoard;
import minesweeper.ai.games.BoardConfiguration.Cell;
import minesweeper.ai.games.BoardConfiguration.Position;
import minesweeper.ai.utils.Node;

public class TestablePlayer {
	
	public void backtrackSolve(MutableBoard grid) {
		BoardInfoHelper helper = new BoardInfoHelper(grid);
    	List<Position> positions = helper.getUnknownBorderCells();
    	toPick = new HashMap<>(); toFlag = new HashMap<>();
    	toPickSet = new HashSet<>(positions); toFlagSet = new HashSet<>(positions);
    	backtrackSolve(grid, helper, positions, new Node(null,null), new Node(null,null));
    	System.out.println("To Pick " + toPickSet);
    	System.out.println("To Flag " + toFlagSet);
    	for(Position p : toPickSet)
    		grid.setCell(p, Cell.NO_BOMB);
    	for(Position p : toFlagSet)
    		grid.setCell(p, Cell.FLAG);
    	System.out.println(grid);
	}
	
	private Map<Position,Integer> toPick, toFlag;
	private Set<Position> toPickSet, toFlagSet;
	private void incrementAll(Map<Position,Integer> map, Iterable<Position> list) {
		for(Position p : list)
			map.put(p,map.getOrDefault(p,0)+1);
	}
	private boolean backtrackSolve(MutableBoard board, BoardInfoHelper helper, List<Position> positions, Node outPick, Node outFlag) {
		if(positions.isEmpty()) {
			System.out.println("sucessful backtrack");
			toPickSet.retainAll(outPick.asSet());
			toFlagSet.retainAll(outFlag.asSet());
			incrementAll(toPick,outPick);
			incrementAll(toFlag,outFlag);
			return true;
		}
		Position p = positions.remove(0);
		board.setCell(p,Cell.NO_BOMB);
		boolean result = false;
		if(helper.validate(p))
			if(backtrackSolve(board,helper,positions,new Node(p,outPick),outFlag))
				result = true;
		board.setCell(p, Cell.FLAG);
		if(helper.validate(p)) 
			if(backtrackSolve(board,helper,positions,outPick,new Node(p,outFlag)))
				result = true;
		board.setCell(p, Cell.UNKNOWN);
		positions.add(0,p);
		return result;
	}

}
