package com.battlegrid.game;

import java.io.File;
import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;

/*
 * Saves the pixels on the screen, so they can be used in a TransitionScreen.
 */
public class ScreenshotFactory {

	/*
	 * Save pixels to disk in PNG format. (root directory of game).
	 */
    public static void saveScreenshot(){
        try{
            FileHandle fh;
            do{
            	File legacy = new File("tFrame.png");
            	legacy.delete(); // delete if already exists
                fh = new FileHandle("tFrame" + ".png");
            }while (fh.exists());
            Pixmap pixmap = getScreenshot(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
            PixmapIO.writePNG(fh, pixmap);
            pixmap.dispose();
        }catch (Exception e){           
        }
    }

    /*
     * Collect pixels from screen.
     */
    private static Pixmap getScreenshot(int x, int y, int w, int h, boolean yDown){
        final Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(x, y, w, h);

        if (yDown) {
            // Flip on vertical depending on pixel configuration.
            ByteBuffer pixels = pixmap.getPixels();
            int numBytes = w * h * 4;
            byte[] lines = new byte[numBytes];
            int numBytesPerLine = w * 4;
            for (int i = 0; i < h; i++) {
                pixels.position((h - i - 1) * numBytesPerLine);
                pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
            }
            pixels.clear();
            pixels.put(lines);
            pixels.clear();
        }

        return pixmap;
    }
}