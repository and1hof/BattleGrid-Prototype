package com.battlegrid.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/*
 * This is the core game class. It acts as the game loop, and renders
 * screens whenever required.
 */
public class BattleGrid extends Game {
	public SpriteBatch myBatch; // renderer for graphics
	public BitmapFont myFont; // font for all game-generated text
	public float stateTime; // game clock
	public static ScreenshotFactory sc; // for recording pixels on screen
	
	/*
	 * This method is called one time on application start.
	 * It loads the welcome screen.
	 */
	public void create () {
		// initialize fields
		myBatch = new SpriteBatch();
		myFont = new BitmapFont();
		stateTime = 0;
		// load welcome screen
		this.setScreen(new WelcomeScreen(this));
	}


	/*
	 * Called 60 times per second. Requests frames to update via myBatch.
	 */
	public void render () {
		stateTime += Gdx.graphics.getDeltaTime(); // increment game clock
		super.render();
	}
	
	/*
     * Removes variables from memory once game is finished.
	 */
	public void dispose() {
		myBatch.dispose();
		myFont.dispose();
	}
}
