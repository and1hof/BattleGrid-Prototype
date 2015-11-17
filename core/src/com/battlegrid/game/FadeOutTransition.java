package com.battlegrid.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;

/*
 * The transition screen fades to black and than fades to white providing a nice clean
 * transition between two game states.
 */
public class FadeOutTransition implements Screen {
	// CORE GAME
	private OrthographicCamera myCamera;
	private Screen myScreen;
	private BattleGrid myGame;
	public long duration;
	public long alphaState;
	private TextureRegion bg;
	private float alpha;
	private int scale;
	
	public FadeOutTransition(BattleGrid theGame, Screen theScreen, int theDur) {
		// SAVE SCREEN STATE
		myScreen = theScreen;
		myGame   = theGame;
		scale = theDur;
		myGame.sc.saveScreenshot(); // record last frame of last screen
		duration = TimeUtils.millis(); // record current time in miliseconds
		alphaState = TimeUtils.millis(); // time of last darken
		
		// CONFIGURE CAMERA
		myCamera = new OrthographicCamera();
		myCamera.setToOrtho(false, 800, 480);
		
		// CONFIGURE TRANSITION ASSETS
		bg = new TextureRegion(new Texture("tFrame.png"));
		alpha = 1f;
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

        // Darken tFrame every 1/20th of a second
        if (TimeUtils.timeSinceMillis(alphaState) > 50 * scale) {
        	alpha = alpha -0.1f;
        	alphaState = TimeUtils.millis();
        }
        
        myGame.myBatch.setColor(1f, 1f, 1f, alpha);
        myGame.myBatch.begin();
        myGame.myBatch.draw(bg, 0, 0);
        myGame.myBatch.end();

        if (TimeUtils.timeSinceMillis(duration) > 450 * scale) { // animation is a bit under a half second
        	myGame.myBatch.setColor(1f, 1f, 1f, 1f); // reset alpha
        	myGame.setScreen(myScreen); // jump to next screen
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
		myCamera = null;
		bg = null;
		
	}

}
