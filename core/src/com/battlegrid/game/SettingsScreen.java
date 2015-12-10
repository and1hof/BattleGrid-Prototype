package com.battlegrid.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/*
 * Displays an animated settings screen, giving the player 
 * choice of difficulty on three levels (easy, medium, hard).
 */
public class SettingsScreen implements Screen {
		// CORE GAME
		private final BattleGrid myGame;
		private OrthographicCamera myCamera;
		int difficulty;
		// ART ASSETS
		TextureRegion wp1; // wallpaper frame 1
		TextureRegion wp2; // wallpaper frame 2
		Animation wpAnim; // animated wallpaper
		TextureRegion currentFrame; // current wallpaper frame
		// AUDIO ASSETS
		Sound selectSound;
		
		public SettingsScreen(final BattleGrid theGame, int theDifficulty) {
			// COPY GAME STATE
			myGame = theGame;
			difficulty = theDifficulty; // default is 0
			
			// CONFIGURE CAMERA
			myCamera = new OrthographicCamera();
			myCamera.setToOrtho(false, 800, 480);
			
			// CONFIGURE ART ASSETS
			wp1 = new TextureRegion(new Texture("SettingsScreen/wp1.png"));
			wp2 = new TextureRegion(new Texture("SettingsScreen/wp2.png"));
			wpAnim = new Animation(0.6f, wp1, wp2); // swap frames every .4 seconds
			
			// CONFIGURE SFX
			selectSound = Gdx.audio.newSound(Gdx.files.internal("General/select.wav"));
		}
		
		@Override
		public void show() {}

		@Override
		public void render(float delta) {
			// ask openGL to clear the screen of any previously set pixels
	        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
	        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	        
	        // tell the camera to check what its looking at every time a new frame renders
	        myCamera.update();
	        myGame.myBatch.setProjectionMatrix(myCamera.combined);

	        // load in current animation frame
	        currentFrame = wpAnim.getKeyFrame(myGame.stateTime, true);
	        // render the welcome screen
	        myGame.myBatch.begin();
	        myGame.myBatch.draw(currentFrame, 0, 0);
	        myGame.myBatch.end();

	        // Transition to the game
	        if (Gdx.input.isTouched()) {
	        	int x = Gdx.input.getX();
	        	
	        	if (x < 185) { // only check x coordinate.
	        		difficulty = 0;
	        	} else if (x > 185 && x < 400) {
	        		difficulty = 1;
	        	} else {
	        		difficulty = 4;
	        	}
	        	selectSound.play(); 
	        	myGame.setScreen(new FadeOutTransition(myGame, new GameScreen(myGame, difficulty), 1));
	            dispose();
	        }
			
		}

		@Override
		public void resize(int width, int height) {}

		@Override
		public void pause() {}

		@Override
		public void resume() {}

		@Override
		public void hide() {}

		@Override
		public void dispose() {
			wp1          = null;
			wp2          = null;
			wpAnim       = null;
			currentFrame = null;
			selectSound  = null;
		}

	}
