package com.battlegrid.game;

import java.awt.Point;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;

/*
 * The game screen manages a single battle between the player and the AI.
 */
public class GameScreen implements Screen {
	// CORE GAME
	private final BattleGrid myGame;
	private OrthographicCamera myCamera;
	public Player p1;
	public Player p2;
	public BattleAI ai;
	public GameBoard myBoard;
	public int gameState;
	// ART ASSETS
	// ART: BACKGROUND FRAMES
	TextureRegion bg1;
	TextureRegion bg2;
	TextureRegion bg3;
	TextureRegion cbg1;
	TextureRegion card;
	TextureRegion health;
	// ART: PLAYER AND AI FRAMES
	Animation p1Idle;
	Animation p2Idle;
	Animation p1Shoot;
	Animation p2Shoot;
	Animation p1Move;
	Animation p2Move;
	// ANIMATION ASSETS
	// ANIMATION: BACKGROUND
	Animation bgAnim;
	TextureRegion currentFrame;
	FadeInTransition inT;
	TextureRegion b1;
	TextureRegion b2;
	TextureRegion b3;
	TextureRegion indicator;
	Animation bAnim;
	// AUDIO: MUSIC
	Music battleTheme;
	Sound shootSound;
	Sound selectSound;
	Sound winSound;
	Sound loseSound;
	int draws = 1;

	public GameScreen(final BattleGrid theGame, int difficulty) {
		// copy game state
		myGame = theGame;
		// configure camera to match our desktop configuration
		myCamera = new OrthographicCamera();
		myCamera.setToOrtho(false, 800, 480);
		inT = new FadeInTransition(myGame);
		// set up the game
		p1 = new Player(500, false);
		p2 = new Player(500, true); // this player is AI
		myBoard = new GameBoard(p1, p2);
		ai = new BattleAI(p1, p2, difficulty, myBoard);
		gameState = 0; // 0 is draw. 1 is battle.
		// configure art assets

		// BACKGROUND
		bg1 = new TextureRegion(new Texture("GameScreen/bg1.png"));
		bg2 = new TextureRegion(new Texture("GameScreen/bg2.png"));
		bg3 = new TextureRegion(new Texture("GameScreen/bg3.png"));
		cbg1 = new TextureRegion(new Texture("GameScreen/cbg1.png"));
		card = new TextureRegion(new Texture("GameScreen/card.png"));
		health = new TextureRegion(new Texture("GameScreen/health.png"));
		indicator = new TextureRegion(new Texture("GameScreen/indicator.png"));
		bgAnim = new Animation(0.35f, bg1, bg2, bg3, bg2);
		bgAnim.setPlayMode(Animation.PlayMode.LOOP);
		// PLAYER AND AI
		p1Idle = getAnim("GameScreen/p1-0.png", 1, 4, .6f);
		p2Idle = getAnim("GameScreen/p2-0.png", 1, 4, .6f);
		p1Shoot = getAnim("GameScreen/p1-1.png", 1, 5, .04f);
		p2Shoot = getAnim("GameScreen/p2-1.png", 1, 5, .04f);
		p1Move  = getAnim("GameScreen/p1-2.png", 1, 4, .025f);
		p2Move = getAnim("GameScreen/p2-2.png", 1, 4, .025f);
		//p2Shoot = getAnim("GameScreen/p2-1.png", 1, 5, .3f);
		// CONFIGURE MUSIC
		battleTheme = Gdx.audio.newMusic(Gdx.files.internal("GameScreen/battle.mp3"));
		battleTheme.setLooping(true);
		battleTheme.play();
		// CONFIGURE SFX
		shootSound = Gdx.audio.newSound(Gdx.files.internal("GameScreen/shoot.wav"));
		winSound = Gdx.audio.newSound(Gdx.files.internal("GameScreen/win.wav"));
		loseSound = Gdx.audio.newSound(Gdx.files.internal("GameScreen/lose.wav"));
		selectSound = Gdx.audio.newSound(Gdx.files.internal("General/select.wav"));
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		if (gameState == 0) {
			drawPhase();
		} else if (gameState == 1) {
			battlePhase();
		}
	}

	public void drawPhase() {
		/*
		 * GENERAL
		 */
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		myCamera.update();
		myGame.myBatch.setProjectionMatrix(myCamera.combined);

		/*
		 * GAMEPLAY
		 */
		if (!p1.hasDrawn) {
			p1.myHand = new ArrayList<Card>();
			p2.myHand = new ArrayList<Card>();
			p1.draw();
			ai.draw();
		}
		getDrawInput(p1);

		/*
		 * BEGIN RENDERING CYCLE
		 */
		myGame.myBatch.begin();
		myGame.myBatch.draw(cbg1, 0, 0);
		for (int i = 0; i < p1.random.size(); i++) {
			myGame.myBatch.draw(card, 37 + 150 * i, 150, 125, 200);
			myGame.myFont.setColor(Color.BLACK);
			myGame.myFont.draw(myGame.myBatch, p1.random.get(i).name, 52 + 120 * i + (30 * i), 240);
		} myGame.myFont.setColor(Color.WHITE);
		myGame.myBatch.end();

		if (p1.random.size() == 0) {
			p1.hasDrawn = false;
			p1.lastDraw = TimeUtils.millis();
			gameState = 1; // all cards now in hand. move to battle state!
		}

	}

	private void getDrawInput(Player p1) {
		if (Gdx.input.justTouched()) {
			int x = Gdx.input.getX();
			int y = Gdx.input.getY();
			// System.out.println("X: " + x + " Y: " + y);
			/*
			 * Allow the player to choose his own "card pick order" so he can
			 * optimize ranges and elements.
			 */
			for (int i = 0; i < p1.random.size(); i++) {
				if (x > 50 + 150 * i && x < 150 + 150 * i) {
					selectSound.play();
					p1.myHand.add(new Card(p1.random.get(i)));
					p1.random.remove(i);
				}
			}
		}

	}

	public void battlePhase() {
		/*
		 * GENERAL
		 */
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		myCamera.update();
		myGame.myBatch.setProjectionMatrix(myCamera.combined);
		p1.checkFrame();
		p2.checkFrame();
		ai.think(gameState);
		/*
		 * GAMEPLAY
		 */
		getBattleInput(p1);

		// update sprites
		currentFrame = bgAnim.getKeyFrame(myGame.stateTime, true);

		/*
		 * BEGIN RENDERING CYCLE
		 */
		myGame.myBatch.begin();

		// RENDER: BACKGROUND
		myGame.myBatch.draw(currentFrame, 0, 0); // render game board

		// RENDER: PLAYER AND AI
		int[] p1Draw = animState(p1); // collect coordinates for player
		int[] p2Draw = animState(p2); // collect coordinates for AI

	
		drawUI();
		// TARGET INDICATORS
//		if (p1.myHand.size() > 0) {
//			int[] p1Indi = animIndi(p1);
//			myGame.myBatch.draw(indicator, p1Indi[0], p1Indi[1]);
//		}
		
		int p1f = 4;
		int p2f = 4;
		int width = 100;
		int p2Width = 100;
		if (p1.animFrame == 1) {
			p1f = 5;
			width = 200;
		}
		if (p2.animFrame == 1) {
			p2f = 4;
			p2Width = 200;
		}
		Animation p1Anim = getAvatar(p1);
		Animation p2Anim = getAvatar(p2);
		TextureRegion p1Frame = p1Anim.getKeyFrame(myGame.stateTime, true);
		TextureRegion p2Frame = p2Anim.getKeyFrame(myGame.stateTime, true);
		myGame.myBatch.draw(p1Frame, p1Draw[0], p1Draw[1], width, 125); // player
		myGame.myBatch.draw(p2Frame, p2Draw[0], p2Draw[1], p2Width, 125);
		myGame.myBatch.end();
		
		if (p1.hp < 1) {
			loseSound.play();
			myGame.setScreen(new FadeOutTransition(myGame, new FinalScreen(myGame, false), 8));
			dispose();
		}
		if (p2.hp < 1) {
			winSound.play();
			myGame.setScreen(new FadeOutTransition(myGame, new FinalScreen(myGame, true), 8));
			dispose();
		}
		if (TimeUtils.timeSinceMillis(p1.lastDraw) > 15000 && p1.myDeck.size() > 0) {
			gameState = 0;
			draws++;
		} else if (TimeUtils.timeSinceMillis(p1.lastDraw) > 15000 && p1.myDeck.size() == 0){
			loseSound.play();
			myGame.setScreen(new FadeOutTransition(myGame, new FinalScreen(myGame, false), 8));
			dispose();
		}
	}

	/*
	 * Draws UI text components on the screen.
	 */
	public void drawUI() {
		String c1;
		String c2;
		if (p1.myHand.size() > 0) {
			c1 = p1.myHand.get(0).name;
		} else {
			c1 = "EMPTY HAND";
		}
		if (p2.myHand.size() > 0) {
			c2 = p2.myHand.get(0).name;
		} else {
			c2 = "EMPTY HAND";
		}
		// RENDER UI COMPONENTS
		String dtext = "Next Draw: ";
		if (draws == 6) {
			dtext = "Game Over: ";
		}
		
		long tilNextDraw = ((15000 - TimeUtils.timeSinceMillis(p1.lastDraw)) / 1000);
		myGame.myBatch.draw(health, 0, 420, 165, 60);
		myGame.myBatch.draw(health, 635, 420, 165, 60);
		myGame.myFont.draw(myGame.myBatch, "HP: " + p1.hp, 10, 470);
		myGame.myFont.draw(myGame.myBatch, "HP: " + p2.hp, 645, 470);
		myGame.myFont.draw(myGame.myBatch, c1, 10, 455);
		myGame.myFont.draw(myGame.myBatch, c2, 645, 455);
		myGame.myFont.draw(myGame.myBatch, dtext + tilNextDraw, 10, 440);
		myGame.myFont.draw(myGame.myBatch, dtext + tilNextDraw, 645, 440);
		myGame.myFont.draw(myGame.myBatch, "("+p1.myHand.size() + ")", 140, 470);
		myGame.myFont.draw(myGame.myBatch, "("+p2.myHand.size() + ")", 775, 470);
	}

	/*
	 * Get and return avatar for player or AI based on game state.
	 */
	private Animation getAvatar(Player thePlayer) {
		boolean isP1 = !thePlayer.AI;

		if (isP1) {
			if (p1.animFrame == 1) {
				return p1Shoot;
			} else if (p1.animFrame == 2 || p1.animFrame == 3) {
				return p1Move;
			}
			return p1Idle;
		} 
		if (p2.animFrame == 1) {
			return p2Shoot;
		} else if (p2.animFrame == 2 || p2.animFrame == 3) {
			return p2Move;
		}
		return p2Idle;
	}

	/*
	 * Check for keyboard input from the human player.
	 */
	private void getBattleInput(Player thePlayer) {
		if (Gdx.input.isKeyJustPressed(Keys.DPAD_UP)) {
			myBoard.move(0, thePlayer);
			thePlayer.setAnimFrame(2, 100);
		} else if (Gdx.input.isKeyJustPressed(Keys.DPAD_DOWN)) {
			myBoard.move(1, thePlayer);
			thePlayer.setAnimFrame(2, 100);
		} else if (Gdx.input.isKeyJustPressed(Keys.DPAD_LEFT)) {
			myBoard.move(3, thePlayer);
			thePlayer.setAnimFrame(3, 100);
		} else if (Gdx.input.isKeyJustPressed(Keys.DPAD_RIGHT)) {
			myBoard.move(2, thePlayer);
			thePlayer.setAnimFrame(3, 100);
		} else if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			if (p1.myHand.size() > 0) {
				thePlayer.setAnimFrame(1, 200); // shoot
				shootSound.play();
				attack();
			}
		}
	}

	/*
	 * Attempt to attack the enemy. Remove card from hand after attack. Apply
	 * status based on card element.
	 */
	public void attack() {
		Card temp = p1.myHand.get(0);
		if (myBoard.checkShot(temp.range, true)) {
			if (p2.status == 1 && temp.element == 2) {
				p2.hp = p2.hp - (temp.damage * 2); // double damage multiplier
													// if correct order cards
			} else if (p2.status == 3 && temp.element == 4) {
				p2.hp = p2.hp - (temp.damage * 2);
			} else {
				p2.hp = p2.hp - temp.damage;
			}
			p2.status = temp.element;
		}
		p1.myHand.remove(0);
	}

	/*
	 * Return an array of coordinates, of where to render the player and AI on
	 * the game board.
	 */
	public int[] animState(Player thePlayer) {
		int[] result = { 0, 0, 0, 0 }; // x, y, scaleX, scaleY
		// grab player position data from game board
		boolean isP1 = !thePlayer.AI;
		Point pos = myBoard.findPlayer(isP1, myBoard.clone());
		int x = pos.x;
		int y = pos.y;

		if (isP1) { // calculate P1's coordinates
			result[0] = 10 + 133 * x;
			result[1] = 75 + 70 * y;
		} else { // calculate AI's coordinates
			result[0] = 10 + 133 * x;
			result[1] = 75 + 70 * y;
		}
		return result;
	}
	
	/*
	 * Scale the targetting indicators for the game board.
	 */
	public int[] animIndi(Player thePlayer) {
		int[] result = { 0, 0, 0, 0 }; // x, y, scaleX, scaleY
		// grab player position data from game board
		boolean isP1 = !thePlayer.AI;
		Point pos = myBoard.findPlayer(isP1, myBoard.clone());
		int x = pos.x;
		int y = pos.y;
		
		if (isP1 && p1.myHand.size() > 0) { // calculate P1's coordinates
			Card next = p1.myHand.get(0);
			double size = 1;
			for (int i = 0; i < next.range; i++) {
				size = size + .75;
			}
			result[2] = (int) (115 * next.range) - (15 * next.range)*(y+1); // x scale
			result[3] = 115; // y scale
			if (y == 2) {
				result[0] = 180 + x * 75 + 60; // x coordinate
				result[1] = 175; // y coordinate
				result[2] = result[2] - x*2;
			} else if (y == 1) {
				result[0] = 120 + x * 95 + 80; // x coordinate
				result[1] = 115; // y coordinate
				result[2] = result[2] + 6*next.range - x*3;
			} else {
				result[0] = 30 + x * 130 + 100; // x coordinate
				result[1] = 25; // y coordinate
				result[2] = result[2] + 20*next.range - x*5;
			}
		} else { // calculate AI's coordinates
			// unused right now
		}
		return result;
	}

	/*
	 * Return the correct player sprite to be rendered.
	 */
	public Animation getAnim(String sheet, int row, int col, float flip) {
		Animation myAnim;
		Texture mySheet;
		TextureRegion myFrame;
		TextureRegion[] myFrames;

		mySheet = new Texture(sheet);
		TextureRegion[][] temp = TextureRegion.split(mySheet, mySheet.getWidth() / col, mySheet.getHeight() / row); // cols,
																													// rows
		myFrames = new TextureRegion[col * row]; // columns times rows
		int index = 0;
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				myFrames[index++] = temp[i][j];
			}
		}
		myAnim = new Animation(flip, myFrames);
		myFrame = myAnim.getKeyFrame(myGame.stateTime, true);

		return myAnim;
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void pause() {

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
		// TODO Auto-generated method stub
		battleTheme.stop();
		battleTheme = null;

	}

}
