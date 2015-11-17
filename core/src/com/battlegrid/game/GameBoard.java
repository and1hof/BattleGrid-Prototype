package com.battlegrid.game;

import java.awt.Point;
import java.util.ArrayList;

public class GameBoard {
	public int[][] board = {{0,0,0},{0,1,0},{0,0,0},{0,0,0},{0,2,0},{0,0,0}};
	public Player p1;
	public Player p2;
	
	public GameBoard(Player one, Player two) {
		p1 = one;
		p2 = two;
	}
	
	/*
	 * Find player's location on the board.
	 */
	public Point findPlayer(boolean p1, int[][] theBoard) {
		int y = -1;
		int x = -1;
		int target = 1; // p1 is represented as a 1 on game board
		if (!p1) {
			target = 2; // p2 is represented as a 2 on game board
		}
		for (int i = 0; i < theBoard.length; i++) {
			for (int k = 0; k < theBoard[i].length; k++) {
				if (theBoard[i][k] == target) {
					x = i;
					y = k;
					break;
				}
			}
		}
		return new Point(x, y);
	}
	
	public boolean move(int move, Player thePlayer) {
		boolean moved = false;
		boolean isP1 = !thePlayer.AI;
		Point pos    = this.findPlayer(isP1, board);
		int x = pos.x;
		int y = pos.y;
		int target = 1;
		if (!isP1) {
			target = 2;
		}
		ArrayList<Integer> theMoves = moves(isP1, board);
		if (theMoves.indexOf(move) != -1 ) {
			if (move == 0) {
				board[x][y + 1] = target;
				board[x][y] = 0;
			} else if (move == 1) {
				board[x][y - 1] = target;
				board[x][y] = 0;
			} else if (move == 2) {
				board[x + 1][y] = target;
				board[x][y] = 0;
			} else if (move == 3) {
				board[x - 1][y] = target;
				board[x][y] = 0;
			}
			moved = true;
		}
		return moved;
	}
	
	public ArrayList<Integer> moves(boolean p1, int[][] theBoard) {
		ArrayList<Integer> theMoves = new ArrayList<Integer>();
		Point player = findPlayer(p1, theBoard);
		int x = player.x;
		int y = player.y;
		
		// up
		if (isValid(x, y + 1, p1)) {
			theMoves.add(0);
		}
		// down
		if (isValid(x, y - 1, p1)) {
			theMoves.add(1);
		}
		// right
		if (isValid(x + 1, y, p1)) {
			theMoves.add(2);
		}
		// left
		if (isValid(x - 1, y, p1)) {
			theMoves.add(3);
		}
		return theMoves;
	}
	
	/*
	 * Checks if a board location is valid.
	 */
	public boolean isValid(int x, int y, boolean playerOne) {
		if (playerOne) {
			if (x >= 0 && x <= 2 && y >= 0 && y <= 2) {
				return true;
			}
		} else {
			if (x >= 3 && x <= 5 && y >= 0 && y <= 2) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkShot(int range, boolean playerOne) {
		Point p1 = findPlayer(playerOne, board);
		Point p2 = findPlayer(!playerOne, board);
		
		if (Math.abs(p1.x - p2.x) <= range && p1.y == p2.y) {
			return true;
		}
		return false;
	}
	
	public int[][] clone() {
		int[][] input = board;
	    if (input == null)
	        return null;
	    int[][] result = new int[input.length][];
	    for (int r = 0; r < input.length; r++) {
	        result[r] = input[r].clone();
	    }
	    return result;
	}
}
