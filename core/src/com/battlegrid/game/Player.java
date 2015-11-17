package com.battlegrid.game;

import java.util.ArrayList;

import com.badlogic.gdx.utils.TimeUtils;

/*
 * Represents a player on the game board.
 */
public class Player {
	// either player
	public int hp;
	public int status;
	public Deck myDeck;
	public int animFrame; // state of animation
	public long animTimer;
	public int theDuration;
	long lastDraw; // time of last draw
	public ArrayList<Card> myHand;
	public ArrayList<Card> random;
	public boolean hasDrawn;
	public boolean drawing;
	// ai only
	public boolean AI;
	
	public Player(int theHP, boolean isAI) {
		hp = theHP;
		AI = isAI;
		animFrame = 0; // idle
		animTimer = 0;
		theDuration = 0;
		lastDraw = 0;
		myDeck  = new Deck();
		myHand  = new ArrayList<Card>();
		random  = new ArrayList<Card>();
		drawing = false;
		hasDrawn = false;
	}
	
	public void setAnimFrame(int state, int duration) {
		animFrame = state;
		theDuration = duration;
		animTimer = TimeUtils.millis();
		
	}
	
	public void resetFrame() {
		animFrame = 0;
	}
	
	public void draw() {
		for (int i = 0; i < 5; i++) {
			random.add(myDeck.draw());
		} 
		hasDrawn = true;
	}
	
	/*
	 * Reset animation frames.
	 */
	public void checkFrame() {
		if (animFrame != 0 && TimeUtils.timeSinceMillis(animTimer) > theDuration) {
			resetFrame();
		}
	}
	
	
}
