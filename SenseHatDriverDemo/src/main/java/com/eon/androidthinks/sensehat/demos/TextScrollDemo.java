package com.eon.androidthinks.sensehat.demos;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.hardware.SensorManager;

import com.eon.androidthings.sensehatdriverlibrary.SenseHat;
import com.eon.androidthings.sensehatdriverlibrary.devices.LedMatrix;
import com.eon.androidthings.sensehatdriverlibrary.devices.fonts.BlackWhiteFont;
import com.eon.androidthings.sensehatdriverlibrary.devices.fonts.LEDFont;

import java.io.IOException;

/**
 * TextScroll-Demo
 */

public class TextScrollDemo {


    public TextScrollDemo(final SensorManager sensorManager, final AssetManager assetmanager) throws IOException {

        //SenseHat.init(sensorManager);
//        final LEDFont font = new ColorFont(assetmanager);
        final LEDFont font = new BlackWhiteFont(assetmanager);

        // Draw one letter on the LED Matrix
        //            Bitmap letterBitmap = font.getBitmapForLetter('a');
        //            SenseHat.getInstance().getLedMatrix().draw(letterBitmap);

        // infinity text scrolling
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    do {
                        // TODO At the moment only uppercase letters are working
                        Bitmap sentenceBitmap = font.getBitmapForSentence(" HELLO WORLD ");
                        Bitmap targetBitmap = Bitmap.createBitmap(//
                                LedMatrix.WIDTH,//
                                LedMatrix.HEIGHT, //
                                Bitmap.Config.ARGB_8888);

                        for (int currentX = 0; currentX < sentenceBitmap.getWidth() - LedMatrix.WIDTH; currentX++) {
                            // copy sentenceBitmap to targetBitmap
                            for (int y = 0; y < LedMatrix.HEIGHT; y++) {

                                for (int x = 0; x < LedMatrix.WIDTH; x++) {
                                    int color = sentenceBitmap.getPixel(currentX + x, y);
                                    targetBitmap.setPixel(x, y, color);
                                }
                            }
                            SenseHat.getInstance().getLedMatrix().draw(targetBitmap);
                            Thread.sleep(100);
                        }
                    } while (true);
                } catch (Exception e) {
                    // TODO ExceptionHandling
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
