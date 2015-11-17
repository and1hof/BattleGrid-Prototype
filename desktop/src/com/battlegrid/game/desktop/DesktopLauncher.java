package com.battlegrid.game.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.battlegrid.game.BattleGrid;

/*
 * For desktop-specific window configuration.
 * 
 * -> Load in 480p by default. This is default "horizontal" resolution on most android devices.
 * -> Don't allow resizing because pixel art will get stretched.
 */
public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title     = "BattleGrid";
		config.width     = 800;
		config.height    = 480;
		config.resizable = false;
		new LwjglApplication(new BattleGrid(), config);
	}
}
