package com.battlegrid.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/*
 * Displays an animated welcome screen, prompting the user 
 * to start a new match or view the credits, etc.
 */
public class WelcomeScreen implements Screen {
	// CORE GAME
	private final BattleGrid myGame;
	private OrthographicCamera myCamera;
	// ART ASSETS
	TextureRegion wp1; // wallpaper frame 1
	TextureRegion wp2; // wallpaper frame 2
	Animation wpAnim; // animated wallpaper
	TextureRegion currentFrame; // current wallpaper frame
	// AUDIO
	Music welcomeTheme;
	Sound selectSound;
	public WelcomeScreen(final BattleGrid theGame) {
		// COPY GAME STATE
		myGame = theGame;

		// CONFIGURE CAMERA
		myCamera = new OrthographicCamera();
		myCamera.setToOrtho(false, 800, 480);
		
		// CONFIGURE ART ASSETS
		wp1 = new TextureRegion(new Texture("WelcomeScreen/wp1.png"));
		wp2 = new TextureRegion(new Texture("WelcomeScreen/wp2.png"));
		wpAnim = new Animation(0.4f, wp1, wp2); // swap frames every .4 seconds
		
		// CONFIGURE MUSIC
		welcomeTheme = Gdx.audio.newMusic(Gdx.files.internal("WelcomeScreen/welcome.mp3"));
		welcomeTheme.setLooping(true);
		welcomeTheme.play();
		
		// CONFIGURE SFX
		selectSound = Gdx.audio.newSound(Gdx.files.internal("General/select.wav"));
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

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
        	selectSound.play(); 
        	//myGame.setScreen(new FadeOutTransition(myGame, new GameScreen(myGame, 1)));
        	myGame.setScreen(new FadeOutTransition(myGame, new SettingsScreen(myGame, 1), 1));
            dispose();
        }
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		wp1          = null;
		wp2          = null;
		wpAnim       = null;
		currentFrame = null;
		welcomeTheme.stop();
		welcomeTheme = null;
		selectSound  = null;
	}

}
