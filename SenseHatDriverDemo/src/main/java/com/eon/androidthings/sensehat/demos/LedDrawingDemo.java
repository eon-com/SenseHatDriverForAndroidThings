package com.eon.androidthings.sensehat.demos;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;

import com.eon.androidthings.sensehat.R;
import com.eon.androidthings.sensehatdriverlibrary.SenseHat;
import com.eon.androidthings.sensehatdriverlibrary.devices.LedMatrix;
import com.eon.androidthings.sensehatdriverlibrary.devices.fonts.BlackWhiteFont;
import com.eon.androidthings.sensehatdriverlibrary.devices.fonts.LEDFont;

import java.io.IOException;

/**
 * LED Drawing Demo
 */

public class LedDrawingDemo {

    public LedDrawingDemo(final Context context) throws IOException {

        //SenseHat.init(sensorManager);
 //      final LEDFont font = new MultiColorFont(assetmanager);
        final LEDFont font = new BlackWhiteFont(context.getAssets());

        // Draw one letter on the LED Matrix
        //            Bitmap letterBitmap = font.getBitmapForLetter('a');
        //            SenseHat.getInstance().getLedMatrix().draw(letterBitmap);

        // infinity text scrolling
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final LedMatrix ledMatrix = SenseHat.getInstance().getLedMatrix();
                    // Render icon and text once and re-use.
                    final Bitmap iconBitmap = createIconBitmap(context.getResources());
                    // TODO At the moment only uppercase letters are working
                    final Bitmap sentenceBitmap = font.getBitmapForSentence(" HELLO RASPI ");
                    do {
                        // Rotate iconBitmap a few times
                        for (int rotation = 0; rotation < 12; rotation++) {
                            ledMatrix.setRotation(rotation);
                            ledMatrix.draw(iconBitmap);
                            Thread.sleep(500);
                        }
                        // Reset rotation for the text scroll
                        ledMatrix.setRotation(0);
                        // Scroll the text on screen.
                        for (int currentX = 0; currentX < sentenceBitmap.getWidth() - LedMatrix.WIDTH; currentX++) {
                            // Using the currentX value, draw a specific segment of the sentenceBitmap.
                            ledMatrix.draw(
                                    sentenceBitmap, currentX, 0, LedMatrix.WIDTH, LedMatrix.HEIGHT);
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

    Bitmap createIconBitmap(final Resources resources) {
        // Setting up result canvas
        final Bitmap result = Bitmap.createBitmap(
                LedMatrix.WIDTH, LedMatrix.HEIGHT, Bitmap.Config.ARGB_8888, true);
        final Canvas canvas = new Canvas(result);
        // Background color and canvas Paint
        canvas.drawColor(Color.BLUE);
        final Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN));
        // Icon bitmap read
        final Bitmap icon = BitmapFactory.decodeResource(resources, R.drawable.led_icon_android);
        final Rect srcRect = new Rect(0, 0, icon.getWidth(), icon.getHeight());
        final Rect dstRect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
        // Draw icon and recycle source bitmap
        canvas.drawBitmap(icon, srcRect, dstRect, paint);
        icon.recycle();
        return result;
    }

}
