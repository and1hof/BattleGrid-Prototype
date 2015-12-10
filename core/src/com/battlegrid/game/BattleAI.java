package com.battlegrid.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;

/*
 * This AI class controls the AI player.
 * There are three agents, each based on a a difficulty from 1-3. However, there is 
 * room to implement harder (or easier) agents in the future.
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

	/*
	 * The AI is a player object with extra control over what he does.
	 * As a result, the AI needs to manage some extra variables.
	 */
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

	/*
	 * I found that the most valuable heuristic to simulate a stronger AI agent
	 * while keeping the AI looking "human-like" was to increase the speed at which the AI makes choices.
	 * The step variable determines the rate at which the AI makes a new choice, in milliseconds.
	 */
	private void config(int d) {
		if (difficulty == 4) {
			step = 350;
		} else if (difficulty > 0) {
			step = 500;
		} else {
			step = 750;
		}
	}

	/*
	 * For the draw phase of the game, the weakest agent pulls random cards.
	 * Other agents perform maximization on all available cards, taking into account debuffs on the enemy, 
	 * card elements / bonus damage multipliers and card damage. This results in a optimal card order in terms
	 * of potential damage output.
	 */
	public void draw() {
		p2.draw();
		for (int i = 0; i < p2.random.size(); i++) {
			p2.myHand.add(p2.random.get(i));
		}

		if (difficulty > 0) {
			p2.myHand = cardMax(p2.myHand);
			// pull optimal order rather than random
		}
		p2.random = new ArrayList<Card>(); // reset it

	}

	/*
	 * Perform one "step", or choice. A step represents each time the AI thinks about what to do next.
	 */
	private void step() {
		time = TimeUtils.millis();
	}

	/*
	 * The think method just handles steps for agents, and ensures enough time has passed
	 * for the agent to think about a move again.
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
	 * The intermediate agent performs maximization (similar to minimax) over 
	 * all cards during draw phase. As a result, he chooses an optimal pick order.
	 * 
	 * He also has a moderate step speed, so he thinks pretty frequently. He will always attack first if possible,
	 * and after that he requests the utility function to determine optimal movements.
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

			int temp = moveUtility(deepClone(nextState)); // calculate only this
															// turn.
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
	 * This agent is the most difficult one, and is pretty tough.
	 * In addition to validating his moves using the utility function
	 * and performing maximization on card order, he has a faster movement speed
	 * resulting in him being able to unload combos quite fast. He also has some added
	 * features like wasting cards if the enemy is refusing to go in range to be hit. This 
	 * prevents him from being locked out of combat if he gets a sequence of 1 range draws while the enemy
	 * (player) keeps getting 2-3 range draws.
	 * 
	 * I built an alpha-beta pruning minimax so he could look several moves ahead,
	 * but he ends up acting like a robot since the human never plays optimally.
	 * He jumps between two or so squares, and it isn't much fun to play against.
	 * I learned that in games like this, detailed heuristics provide a much better experience for
	 * the player.
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

				if (moveCount % 15 == 0) {
					// waste low range cards if x seconds pass
					attack();
					attack = true;
					moveCount = 0;
				}
			}
		}
		for (int i = 0; i < moves.size(); i++) {
			int[][] nextState = predictState(false, deepClone(state), moves.get(i));
			int temp = moveUtility(deepClone(nextState));
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
	 * This is a minimax algorithm with alpha beta pruning that allows you to look several
	 * game boards ahead. Unfortunately, it doesn't provide a very human-seeming AI because
	 * this is not a game which is easily evaluated based on numbers. Using this algorithm, 
	 * the AI bunny hops between it's two favorite squares. 
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
	 * This helper method just deep clones a 2d array of ints.
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
	 * This helper method predicts a game board in the future 
	 * based on the current game board and a simulated move.
	 */
	public int[][] predictState(boolean playerOne, int[][] theState, int move) {
		int[][] result = deepClone(theState);
		int target = 1;
		if (!playerOne) {
			target = 2;
		}
		Point pos = myBoard.findPlayer(playerOne, result);
		ArrayList<Integer> theMoves = myBoard.moves(playerOne, result);
		int x = pos.x;
		int y = pos.y;

		if (theMoves.indexOf(move) != -1) {
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
	 * This utility function takes into a large number of heuristics to help the AI 
	 * figure out where to move to next.
	 * 
	 * - The AI plays offensively while he has cards in his hand, but more defensively when he lacks cards.
	 * - The AI also incorporates the range of enemy fire, so if he can get a shot off while not getting hit 
	 * - (e.x. AI has a 2 range and player has a 1 range) - he will optimize his position and fire from a safe location.
	 * - In order to provide a more human-like experience, the AI will avoid the tile he was last on, this prevents most 
	 * bunny-hopping between two tiles.
	 * - The AI will also factor in the damage of the enemie's card, so that he can determine if a trade is worth it or not 
	 * (e.x. AI deals 50 dmg, player deals 25 dmg, this is a good trade. If reversed it's a bad trade.)
	 * - The AI also likes to dodge the player on the Y axis. This is a valid heuristics, since all cards shoot down the x
	 * currently - but if cards with variable width (e.x. 3 wide, 1 long) are implemented it might need modified.
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
			// try to stay out of enemy fire, but close enough to step in for a
			// shot.
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
				if (p1Next.range >= p2Next.range && (me.x - them.x) <= p1Next.range && (me.x - them.x) > p2Next.range) {
					util--;
					// avoid the trade we can't win.
				}
				if ((me.x - them.x) <= p1Next.range && (me.x - them.x) <= p2Next.range
						&& p1Next.damage < p2Next.damage) {
					util = util + 2;
					;
					// we will out damage them in a trade.
				} else if ((me.x - them.x) <= p1Next.range && (me.x - them.x) <= p2Next.range
						&& p1Next.damage > p2Next.damage) {
					util--;
					// we won't out damage them in trade
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
				util = util - 2;
				// can't hit us if we dodge.
			}
		}

		return util;
	}

	/*
	 * Iterates through all possible combinations of card hands and returns the
	 * optimal one based on the cardUtility() function which takes into
	 * consideration the elements of the cards, the damage of each card, and the
	 * enemy's current status affliction.
	 */
	public ArrayList<Card> cardMax(ArrayList<Card> theCards) {
		int max = 0;
		ArrayList<Card> result = new ArrayList<Card>(theCards);

		for (int i = 0; i < theCards.size(); i++) {
			for (int k = 0; k < theCards.size(); k++) {
				if (k < theCards.size() - 1) {
					Collections.swap(theCards, k, k + 1);
					if (cardUtility(theCards) > max) {
						// System.out.println("PRed: " +cardUtility(theCards));
						max = cardUtility(theCards);
						result = (ArrayList<Card>) theCards.clone();
					}
				}
			}
		}

		return result;
	}

	/*
	 * A utility function for determining correct pick order based on card
	 * damage and elements. 5 cards, 5! 
	 * 
	 * This takes into account all active environment variables: player debuff status,
	 * card element, card damage, and optimizes not only for bonus damage combos but for the 
	 * highest damage bonus damage combos in the correct order for maximium card hand damage.
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

	/*
	 * This is a helper method to keep the AI's 
	 * animations running when he moves since his movements 
	 * are tied to a variable step timer.
	 */
	public void animator(int move) {
		if (move == 0 || move == 1) {
			p2.setAnimFrame(2, 100);
		}
		if (move == 2 || move == 3) {
			p2.setAnimFrame(3, 100);
		}
	}

	/*
	 * This method aids in performing attacks against the player.
	 * An attack involves damage dealt (potentially), a sound clip played,
	 * an animation of a few frames loading up, and a card being removed from the hand.
	 * Also remember bonus damage multipliers.
	 */
	public void attack() {
		if (p2.myHand.size() > 0) {
			p2.setAnimFrame(1, 200); // shoot
			shootSound.play();
			Card temp = p2.myHand.get(0);
			if (myBoard.checkShot(temp.range, true)) {
				if (p1.status == 1 && temp.element == 2) {
					p1.hp = p1.hp - (temp.damage * 2);
					// double damage multiplier if correct order cards
				} else if (p1.status == 3 && temp.element == 4) {
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
