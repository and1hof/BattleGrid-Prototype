package com.battlegrid.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;

/*
 * This AI class controls the AI player.
 */
public class BattleAI {
	Player p2;
	int difficulty;
	GameBoard myBoard;
	private long step;
	private long time;
	Sound shootSound;
	Player p1;
	Point moveLog;
	int moveCount = 0;

	
	public BattleAI(Player thePlayer1, Player thePlayer2, int theD, GameBoard theBoard) {
		p1 = thePlayer1;
		p2 = thePlayer2;
		difficulty = theD;
		myBoard = theBoard;
		moveLog = new Point();
		time = 0;
		shootSound = Gdx.audio.newSound(Gdx.files.internal("GameScreen/shoot.wav"));
		config(difficulty);
	}
	
	// AI also moves much faster at higher difficulty.
	private void config(int d) {
		if (difficulty == 4) {
			step = 350;
		} else if (difficulty > 0) {
			step = 500;
		} else {
			step = 750;
		}
	}
	
	public void draw() {
		p2.draw();
		for (int i = 0; i < p2.random.size(); i++) {
			p2.myHand.add(p2.random.get(i));
		} 
		
		if (difficulty > 0) {
			p2.myHand = cardMax(p2.myHand); // pull optimal order rather than random
		}
		//System.out.println("act: " + cardUtility(p2.myHand));
		//System.out.println(cardUtility(p2.myHand));
		p2.random = new ArrayList<Card>(); // reset it
	
	}
	
	private void step() {
		time = TimeUtils.millis();
	}
	
	/*
	 * Called on every render frame during battle phase.
	 */
	public void think(int theState) {
		if (TimeUtils.timeSinceMillis(time) > step && theState == 1) {
			// see if enough time has passed since last move
			
			// use agent of correct difficulty level
			if (difficulty == 0) {
				agentZero();
			} else if (difficulty > 0 && difficulty < 4) {
				agentOne();
			} else {
				agentTwo();
			}
			step();
		}
	}
	
	
	/*
	 * An intermediate level AI for average players.
	 */
	public void agentOne() {
		int[][] state = myBoard.clone();
		ArrayList<Integer> moves = myBoard.moves(false, deepClone(state));
		int max = -1000;
		int move = moves.get(0);
		boolean attack = false;
		// attack first.
		if (p2.myHand.size() > 0) {
			Card next = p2.myHand.get(0);
			Point me = myBoard.findPlayer(false, state);
			Point them = myBoard.findPlayer(true, state);
			if (me.y == them.y && (me.x - them.x) <= next.range) {
				attack();
				attack = true;
			}
		}
		for (int i = 0; i < moves.size(); i++) {
			int[][] nextState = predictState(false, deepClone(state), moves.get(i));

			int temp = moveUtility(deepClone(nextState)); // calculate only this turn.
			if (temp > max) {
				max = temp;
				move = moves.get(i);
			}
		}
		if (!attack) {
			Point temp = (Point) myBoard.findPlayer(false, myBoard.clone()).clone();
			moveLog.x = temp.x; // record the choice
			moveLog.y = temp.y;
			myBoard.move(move, p2);
			animator(move);
		}
		
	}
	
	/*
	 * This guy is tough.
	 * He acts just like agentOne(), but calculates several moves ahead and moves quicker.
	 */
	public void agentTwo() {
		int[][] state = myBoard.clone();
		ArrayList<Integer> moves = myBoard.moves(false, deepClone(state));
		int max = -1000;
		int move = moves.get(0);
		boolean attack = false;
		// attack first.
		if (p2.myHand.size() > 0) {
			Card next = p2.myHand.get(0);
			Point me = myBoard.findPlayer(false, state);
			Point them = myBoard.findPlayer(true, state);
			if (me.y == them.y && (me.x - them.x) <= next.range) {
				attack();
				attack = true;
			} else if (next.range < 3) {
				moveCount++;

				if (moveCount % 15 == 0) { // waste low range cards if x seconds pass

					attack();
					attack = true;
					moveCount = 0;
				}
			}
		}
		for (int i = 0; i < moves.size(); i++) {
			int[][] nextState = predictState(false, deepClone(state), moves.get(i));
			
//			//System.out.println(moves);
//			for (int x = 0;x < nextState.length;x++) {
//				for (int k = 0; k < nextState[x].length; k++) {
//					System.out.print(nextState[x][k]);
//				}
//				System.out.println();
//			}
			
			int temp = moveUtility(deepClone(nextState));//miniMax(deepClone(nextState), true, 5, 0, 0); // calculate a few moves ahead
//			System.out.println(temp);
//			System.out.println(temp);
			if (temp > max) {
				max = temp;
				move = moves.get(i);
			}
		}
		if (!attack) {
			Point temp = (Point) myBoard.findPlayer(false, myBoard.clone()).clone();
			moveLog.x = temp.x; // record the choice
			moveLog.y = temp.y;
			
			myBoard.move(move, p2);
			animator(move);
		}
		
	}
	
	/*
	 * Minimax with alpha beta pruning to calculate optimal move
	 */
	private int miniMax(int[][] state, boolean isAI, int depth, int alpha, int beta) {
	    if (depth <= 0) {
	        return moveUtility(state);
	    }
	    if (isAI) {
	        int currentAlpha = Integer.MIN_VALUE;
			ArrayList<Integer> moves = myBoard.moves(false, deepClone(state));
	        for (int i = 0; i < moves.size(); i++) {
	        	int[][] nextState = deepClone(predictState(false, deepClone(state), moves.get(i)));
	            currentAlpha = Math.max(currentAlpha, miniMax(deepClone(nextState), false, depth - 1, alpha, beta));
	            alpha = Math.max(alpha, currentAlpha);
	            if (alpha >= beta) {
	                return alpha;
	            }
	        }
	        return currentAlpha;
	    }
	    int currentBeta = Integer.MAX_VALUE;
	    ArrayList<Integer> moves = myBoard.moves(true, deepClone(state));
	    for (int i = 0; i < moves.size(); i++) {
        	int[][] nextState = deepClone(predictState(true, deepClone(state), moves.get(i)));
	        currentBeta = Math.min(currentBeta, miniMax(deepClone(nextState), true, depth - 1, alpha, beta));
	        beta = Math.min(beta, currentBeta);
	        if (beta <= alpha) {
	            return beta;
	        }
	    }
	    return currentBeta;
	}
	
	/*
	 * clones an int array
	 */
	public static int[][] deepClone(int[][] input) {
	    if (input == null)
	        return null;
	    int[][] result = new int[input.length][];
	    for (int r = 0; r < input.length; r++) {
	        result[r] = input[r].clone();
	    }
	    return result;
	}
	/*
	 * Predicts a game board in the future. 
	 */
	public int[][] predictState(boolean playerOne, int[][] theState, int move) {
		int[][] result = deepClone(theState);
		int target = 1;
		if (!playerOne) {
			target = 2;
		}
		Point pos = myBoard.findPlayer(playerOne, result);
		ArrayList<Integer> theMoves = myBoard.moves(playerOne, result);
		int x  = pos.x;
		int y = pos.y;

		if (theMoves.indexOf(move) != -1 ) {
			if (move == 0) {
				result[x][y + 1] = target;
				result[x][y] = 0;
			} else if (move == 1) {
				result[x][y - 1] = target;
				result[x][y] = 0;
			} else if (move == 2) {
				result[x + 1][y] = target;
				result[x][y] = 0;
			} else if (move == 3) {
				result[x - 1][y] = target;
				result[x][y] = 0;
			}

		}

		
		return result;
	}
	/*
	 * Calculates heuristic value of a given move based on bonus multipliers, number 
	 * of cards in hand, position of ai and player, and the hp both player and ai.
	 */
	public int moveUtility(int[][] state) {
		int util = 0;
		Point me = myBoard.findPlayer(false, deepClone(state));
		Point meCurrent = myBoard.findPlayer(false, myBoard.clone());
		Point them = myBoard.findPlayer(true, deepClone(state));
		Point themCurrent = myBoard.findPlayer(true, myBoard.clone());
		// DON'T PLAY SAME TILES
		if (me.x == moveLog.x && me.y == moveLog.y) {
			util--;
		}
		// PLAY OFFENSE WHILE CARDS REMAIN
		if (p2.myHand.size() > 0) {
			Card p2Next = p2.myHand.get(0);

			if (me.y == them.y) {

				util++; // we are on correct y square
			}
			//try to stay out of enemy fire, but close enough to step in for a shot.
			if (p2Next.range == 1 && me.x == them.x + 1) {
				util++;
			}
			if (p2Next.range == 2 && me.x == them.x + 2) {
				util++;
			}
			if (p2Next.range == 3 && me.x == them.x + 3) {
				util++;
			}
			// don't let them win trade
			if (p1.myHand.size() > 0) {
				Card p1Next = p1.myHand.get(0);
				if (p1Next.range >= p2Next.range && (me.x -them.x) <= p1Next.range && (me.x-them.x) > p2Next.range) {
					util--; // avoid the trade we can't win.
				}
				if ((me.x -them.x) <= p1Next.range && (me.x-them.x) <= p2Next.range && p1Next.damage < p2Next.damage) {
					util = util + 2;; // we will out damage them in a trade.
				} else if ((me.x -them.x) <= p1Next.range && (me.x-them.x) <= p2Next.range && p1Next.damage > p2Next.damage) {
					util--; // we won't out damage them in trade
				}
			}
		} 
		// PLAY DEFENSE WHILE CARDS ARE GONE
		if (p2.myHand.size() == 0 && p1.myHand.size() > 0) {
			Card p1Next = p1.myHand.get(0);
			if ((me.x - them.x) <= p1Next.range) {
				util--;
			}
			if (me.y == them.y) {
				util = util - 2; // can't hit us if we dodge.
			}
		} 
		
		
		
		return util;
	}
	
	/*
	 * A random agent for beginners.
	 */
	public void agentZero() {
		ArrayList<Integer> moves = myBoard.moves(false, myBoard.clone());
		Random r = new Random();
		int r1 = r.nextInt(moves.size());
		int r2 = r.nextInt(10);
		if (r2 > 7) {
			attack();
		} else {
			myBoard.move(moves.get(r1), p2); // move
			animator(moves.get(r1));
		}
	}
	
	/*
	 * Iterates through all possible combinations of card hands and returns the optimal one based 
	 * on the cardUtility() function which takes into consideration the elements of the cards, the damage of each card,
	 * and the enemy's current status affliction.
	 */
	public ArrayList<Card> cardMax(ArrayList<Card> theCards) {
		int max = 0;
		ArrayList<Card> result = new ArrayList<Card>(theCards);
		
		for (int i = 0; i < theCards.size(); i++) {
			for (int k = 0; k < theCards.size(); k++) {
				if (k < theCards.size() - 1) {
					Collections.swap(theCards, k, k + 1);
					if (cardUtility(theCards) > max) {
						//System.out.println("PRed: " +cardUtility(theCards));
						max = cardUtility(theCards);
						result = (ArrayList<Card>) theCards.clone();
					}
				}
			}
		}

		return result;
	}
	
	/*
	 * A utility function for determining correct pick order based on card damage and elements.
	 * 5 cards, 5! possible combinations. Also consider current enemy status.
	 */
	public int cardUtility(ArrayList<Card> theCards) {
		int enemyStatus = p2.status;
		int score = 0;
		
		for (int i = 0; i < theCards.size(); i++) {
			if (theCards.get(i).element == (enemyStatus + 1)) {
				score = score + (theCards.get(i).damage * 2);
			} else {
				score = score + theCards.get(i).damage;
			}
			enemyStatus = theCards.get(i).element;
		}
		return score;
	}
	
	public void animator(int move) {
		if (move == 0 || move == 1) {
			p2.setAnimFrame(2, 50);
		}
		if (move == 2 || move == 3) {
			p2.setAnimFrame(3, 50);
		}
	}
	
	public void attack() {
		if (p2.myHand.size() > 0) {
			p2.setAnimFrame(1, 200); // shoot
			shootSound.play();
			Card temp = p2.myHand.get(0);
			if (myBoard.checkShot(temp.range, true)) {
				if (p1.status == 1 && temp.element == 2) {
					p1.hp = p1.hp - (temp.damage * 2); // double damage multiplier if correct order cards
				} else if (p1.status == 3 && temp.element == 4){
					p1.hp = p1.hp - (temp.damage * 2);
				} else {
					p1.hp = p1.hp - temp.damage;
				}
				p1.status = temp.element;
			}
			p2.myHand.remove(0);
			step();
		}
	}
}
