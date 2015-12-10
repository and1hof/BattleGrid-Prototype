package com.battlegrid.game;

import com.badlogic.gdx.utils.TimeUtils;

/*
 * Perform a controlled fade in for a given game screen.
 */
public class FadeInTransition {
	// CORE GAME
	private BattleGrid myGame;
	public long duration;
	public long alphaState;
	private float alpha;

	public FadeInTransition(BattleGrid theGame) {
		// SAVE SCREEN STATE
		myGame = theGame;
		duration = TimeUtils.millis(); // record current time in milliseconds
		alphaState = TimeUtils.millis(); // time of last darken
		alpha = 0.1f;
		myGame.myBatch.setColor(1f, 1f, 1f, alpha);
	}

	/*
	 * Modify the batch renderer's alpha by incrementing .1f every 1/20th of a
	 * second
	 */
	public void incrementAlpha() {
		if (TimeUtils.timeSinceMillis(alphaState) > 50) {
			// check for alpha change
			alpha = alpha + 0.1f;
			myGame.myBatch.setColor(1f, 1f, 1f, alpha);
			alphaState = TimeUtils.millis();
		}
		if (TimeUtils.timeSinceMillis(duration) > 900) {
			// animation is a bit under a half second
			myGame.myBatch.setColor(1f, 1f, 1f, 1f);
			// reset alpha
		}
	}

	public void reset() {
		alpha = 0.1f;
		duration = TimeUtils.millis();
		alphaState = TimeUtils.millis();
	}
}
