package com.eon.androidthings.sensehatdriverlibrary.devices;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.google.android.things.pio.I2cDevice;

import java.io.IOException;
import java.util.Iterator;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Size;

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

    public static final int ROTATE_NONE = 0;
    public static final int ROTATE_CW1 = 1;
    public static final int ROTATE_CW2 = 2;
    public static final int ROTATE_CW3 = 3;
    public static final int ROTATE_CCW1 = ROTATE_CW3;
    public static final int ROTATE_CCW2 = ROTATE_CW2;
    public static final int ROTATE_CCW3 = ROTATE_CW1;

    // ----------------------------------------------------- Instance Variables
    private static final int BUFFER_SIZE = WIDTH * HEIGHT * 3 + 1;  // pixel and RGB

    private final byte[] mBuffer = new byte[BUFFER_SIZE];
    private final Pivoter mPivoter = new Pivoter();   // Defaults to ROTATE_NONE

    private I2cDevice i2cDevice;

    // ------------------------------------------------------------ Constructor

    /**
     * Create a new LED matrix driver connected on the given I2C bus.
     */
    public LedMatrix(final I2cDevice i2cDevice) {
        this.i2cDevice = i2cDevice;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Change the orientation of draws to the LED matrix.
     * @param rotations The number of 90 degree rotations to apply to the matrix.
     */
    public void setRotation(@IntRange(from=0,to=3) final int rotations) {
        this.mPivoter.setRotations(rotations);
    }

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
    public void draw(@ColorInt final int color) throws IOException {
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
    public void draw(@NonNull final Drawable drawable) throws IOException {
        final Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
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
    public void draw(@NonNull final Bitmap bitmap) throws IOException {
        if (this.mPivoter.rotations != ROTATE_NONE) {
            this.drawPivoted(bitmap, 0, 0, WIDTH, HEIGHT);
            return;
        }
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

    /**
     * Draw a range of the given bitmap to the LED matrix.
     * @param bitmap Bitmap to draw
     * @param startX
     * @param startY
     * @param width
     * @param height
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public void draw(@NonNull final Bitmap bitmap,
                     final int startX,
                     final int startY,
                     final int width,
                     final int height) throws IOException {

        if (width > WIDTH || height > HEIGHT) {
            throw new IllegalArgumentException("Invalid bounds; too large for LED Matrix.");
        } else if (startX < 0 || startY < 0
                || (startX + width) > bitmap.getWidth()
                || (startY + height) > bitmap.getHeight()) {

            throw new IllegalArgumentException(
                    "Invalid bounds; boundary out of bitmap dimension range.");
        }
        this.drawPivoted(bitmap, startX, startY, width, height);
    }

    // -------------------------------------------------------- Private Methods

    private void drawPivoted(@NonNull final Bitmap bitmap,
                             final int startX,
                             final int startY,
                             final int width,
                             final int height) throws IOException {

        for (final Pivot pivot : mPivoter.withBounds(startX, startY, width, height)) {
            final int p = bitmap.getPixel(pivot.fromX, pivot.fromY);
            float a = Color.alpha(p) / 255.f;
            this.mBuffer[1 + pivot.toX + WIDTH * 0 + 3 * WIDTH * pivot.toY] =
                    (byte) ((int) (Color.red(p) * a) >> 3);
            this.mBuffer[1 + pivot.toX + WIDTH * 1 + 3 * WIDTH * pivot.toY] =
                    (byte) ((int) (Color.green(p) * a) >> 3);
            this.mBuffer[1 + pivot.toX + WIDTH * 2 + 3 * WIDTH * pivot.toY] =
                    (byte) ((int) (Color.blue(p) * a) >> 3);
        }
        this.i2cDevice.write(this.mBuffer, this.mBuffer.length);
    }

    private static class Pivoter implements Iterable<Pivot> {

        private int rotations = 0;
        private int startX = -1;
        private int startY = -1;
        private int width = WIDTH;
        private int height = HEIGHT;

        void setRotations(@IntRange(from=0, to=3) final int rotations) {
            if (rotations < 0) {
                this.rotations = ((rotations < -3) ? rotations % 4 : rotations) + 4;
            } else {
                this.rotations = (rotations > 3) ? rotations % 4 : rotations;
            }
        }

        OnPivotListener createListener() {
            switch (rotations) {
                case 1: // ROTATE_CW1, ROTATE_CCW3
                    return new OnPivotListener() {
                        @Override
                        public int[] initPivotedCoords() {
                            return new int[]{WIDTH - 1, 0};
                        }

                        @Override
                        public void updatePivotedCoords(int[] coords) {
                            coords[1]++;
                            if (coords[1] == HEIGHT) {
                                coords[1] = 0;
                                coords[0]--;
                            }
                        }
                    };
                case 2: // ROTATE_CW2, ROTATE_CCW2
                    return new OnPivotListener() {
                        @Override
                        public int[] initPivotedCoords() {
                            return new int[]{WIDTH - 1, HEIGHT - 1};
                        }

                        @Override
                        public void updatePivotedCoords(int[] coords) {
                            coords[0]--;
                            if (coords[0] == -1) {
                                coords[0] = WIDTH - 1;
                                coords[1]--;
                            }
                        }
                    };
                case 3: // ROTATE_CW3, ROTATE_CCW1
                    return new OnPivotListener() {
                        @Override
                        public int[] initPivotedCoords() {
                            return new int[]{0, HEIGHT - 1};
                        }

                        @Override
                        public void updatePivotedCoords(int[] coords) {
                            coords[1]--;
                            if (coords[1] == -1) {
                                coords[1] = HEIGHT - 1;
                                coords[0]++;
                            }
                        }
                    };
                case 0: // ROTATE_NONE
                case 4:
                default:
                    return new OnPivotListener() {
                        @Override
                        public int[] initPivotedCoords() {
                            return new int[]{0, 0};
                        }

                        @Override
                        public void updatePivotedCoords(int[] coords) {
                            coords[0]++;
                            if (coords[0] == WIDTH) {
                                coords[0] = 0;
                                coords[1]++;
                            }
                        }
                    };
            }
        }

        Pivoter withBounds(final int startX, final int startY, final int width, final int height) {
            this.startX = startX;
            this.startY = startY;
            this.width = width;
            this.height = height;
            return this;
        }

        @Override
        public Iterator<Pivot> iterator() {
            final PivotIterator iterator;
            if (startX > -1 || startY > -1) {
                iterator = new PivotIterator(createListener(), startX, startY, width, height);
                startX = -1;
                startY = -1;
            } else {
                iterator = new PivotIterator(createListener());
            }
            return iterator;
        }

    }

    private interface OnPivotListener {
        /**
         * Initialize the starting coordinates for the pivot action.
         * @return an integer array with the initial values for the pivoted X and Y.
         */
        @NonNull
        @Size(value = 2)
        int[] initPivotedCoords();

        /**
         * Update the given rotated X and Y values. Runs when PivotIterator.next() is called.
         * @param coords An array containing the X and Y values to be updated.
         */
        void updatePivotedCoords(@NonNull @Size(value = 2) final int[] coords);
    }

    private static class PivotIterator implements Iterator<Pivot> {

        private final OnPivotListener onRotationListener;
        @Size(value = 2)
        private final int[] pivotedCoords;

        private final int startX;
        private final int endX, endY;

        private int x, y;

        public PivotIterator(@NonNull final OnPivotListener onRotationListener) {
            this(onRotationListener, 0, 0, WIDTH, HEIGHT);
        }

        public PivotIterator(@NonNull final OnPivotListener onRotationListener,
                                final int startX,
                                final int startY,
                                final int width,
                                final int height) {

            if (null == onRotationListener) {
                throw new IllegalArgumentException("onRotationListener must not be null.");
            }
            this.onRotationListener = onRotationListener;
            pivotedCoords = onRotationListener.initPivotedCoords();

            // Bitmap looping values
            this.startX = startX;
            x = startX;
            y = startY;
            endX = x + width;
            endY = y + height;
        }

        @Override
        public boolean hasNext() {
            return y < endY;
        }

        @Override
        public Pivot next() {
            final Pivot result = new Pivot(x++, y, pivotedCoords[0], pivotedCoords[1]);
            if (x == endX) {
                x = startX;
                y++;
            }
            onRotationListener.updatePivotedCoords(pivotedCoords);
            return result;
        }

    }

    private static class Pivot {

        private final int fromX, fromY;
        private final int toX, toY;

        public Pivot(int fromX, int fromY, int toX, int toY) {
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }

    }

}