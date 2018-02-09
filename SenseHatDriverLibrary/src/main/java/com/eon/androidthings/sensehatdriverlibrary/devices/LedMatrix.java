package com.eon.androidthings.sensehatdriverlibrary.devices;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.google.android.things.pio.I2cDevice;

import java.io.IOException;

/**
 * Driver for the SenseHat LED matrix.
 * copy from: https://github.com/androidthings/contrib-drivers/blob/master/sensehat/src/main/java/com/google/android/things/contrib/driver/sensehat/LedMatrix.java
 */
public class LedMatrix implements AutoCloseable {

    // -------------------------------------------------------------- Constants
    // LEDMatrix and Joystick use the same address
    public static final int I2C_ADDRESS = 0x46;

    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;


    // ----------------------------------------------------- Instance Variables
    private static final int BUFFER_SIZE = WIDTH * HEIGHT * 3 + 1;  // pixel and RGB
    private byte[] mBuffer = new byte[BUFFER_SIZE];

    private I2cDevice i2cDevice;

    // ------------------------------------------------------------ Constructor

    /**
     * Create a new LED matrix driver connected on the given I2C bus.
     *
     * @throws IOException
     */
    public LedMatrix(I2cDevice i2cDevice) throws IOException {
        this.i2cDevice = i2cDevice;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Close the driver and the underlying device.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (this.i2cDevice != null) {
            try {
                this.i2cDevice.close();
            } finally {
                this.i2cDevice = null;
            }
        }
    }

    /**
     * Draw the given color to the LED matrix.
     *
     * @param color Color to draw
     * @throws IOException
     */
    public void draw(int color) throws IOException {
        this.mBuffer[0] = 0;
        float a = Color.alpha(color) / 255.f;
        byte r = (byte) ((int) (Color.red(color) * a) >> 3);
        byte g = (byte) ((int) (Color.green(color) * a) >> 3);
        byte b = (byte) ((int) (Color.blue(color) * a) >> 3);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                this.mBuffer[1 + x + WIDTH * 0 + 3 * WIDTH * y] = r;
                this.mBuffer[1 + x + WIDTH * 1 + 3 * WIDTH * y] = g;
                this.mBuffer[1 + x + WIDTH * 2 + 3 * WIDTH * y] = b;
            }
        }
        this.i2cDevice.write(this.mBuffer, this.mBuffer.length);
    }

    /**
     * Draw the given drawable to the LED matrix.
     *
     * @param drawable Drawable to draw
     * @throws IOException
     */
    public void draw(Drawable drawable) throws IOException {
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, WIDTH, HEIGHT);
        drawable.draw(canvas);
        this.draw(bitmap);
    }

    /**
     * Draw the given bitmap to the LED matrix.
     *
     * @param bitmap Bitmap to draw
     * @throws IOException
     */
    public void draw(Bitmap bitmap) throws IOException {
        Bitmap dest = Bitmap.createScaledBitmap(bitmap, 8, 8, true);
        this.mBuffer[0] = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int p = bitmap.getPixel(x, y);
                float a = Color.alpha(p) / 255.f;
                this.mBuffer[1 + x + WIDTH * 0 + 3 * WIDTH * y] = (byte) ((int) (Color.red(p) * a) >> 3);
                this.mBuffer[1 + x + WIDTH * 1 + 3 * WIDTH * y] = (byte) ((int) (Color.green(p) * a) >> 3);
                this.mBuffer[1 + x + WIDTH * 2 + 3 * WIDTH * y] = (byte) ((int) (Color.blue(p) * a) >> 3);
            }
        }
        this.i2cDevice.write(this.mBuffer, this.mBuffer.length);
    }

    // -------------------------------------------------------- Private Methods
}