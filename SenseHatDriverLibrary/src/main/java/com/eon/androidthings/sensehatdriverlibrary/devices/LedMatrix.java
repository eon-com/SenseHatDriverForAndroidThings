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
    public static final int ROTATE_CCW1 = -1;
    public static final int ROTATE_CCW2 = -2;
    public static final int ROTATE_CCW3 = -3;

    // ----------------------------------------------------- Instance Variables
    private static final int BUFFER_SIZE = WIDTH * HEIGHT * 3 + 1;  // pixel and RGB

    private final byte[] mBuffer = new byte[BUFFER_SIZE];
    private final Positioner mPositioner = new Positioner();   // Defaults to ROTATE_NONE

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
    public void setRotation(@IntRange(from = -3,to = 3) final int rotations) {
        this.mPositioner.setRotations(rotations);
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
        if (this.mPositioner.rotations != ROTATE_NONE) {
            this.drawPositioned(bitmap, 0, 0, WIDTH, HEIGHT);
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
        this.drawPositioned(bitmap, startX, startY, width, height);
    }

    // -------------------------------------------------------- Private Methods

    private void drawPositioned(@NonNull final Bitmap bitmap,
                                final int startX,
                                final int startY,
                                final int width,
                                final int height) throws IOException {

        for (final Position position : mPositioner.withBounds(startX, startY, width, height)) {
            final int p = bitmap.getPixel(position.fromX, position.fromY);
            float a = Color.alpha(p) / 255.f;
            this.mBuffer[1 + position.toX + WIDTH * 0 + 3 * WIDTH * position.toY] =
                    (byte) ((int) (Color.red(p) * a) >> 3);
            this.mBuffer[1 + position.toX + WIDTH * 1 + 3 * WIDTH * position.toY] =
                    (byte) ((int) (Color.green(p) * a) >> 3);
            this.mBuffer[1 + position.toX + WIDTH * 2 + 3 * WIDTH * position.toY] =
                    (byte) ((int) (Color.blue(p) * a) >> 3);
        }
        this.i2cDevice.write(this.mBuffer, this.mBuffer.length);
    }

    private static class Positioner implements Iterable<Position> {

        private int rotations = 0;
        private int startX = -1;
        private int startY = -1;
        private int width = WIDTH;
        private int height = HEIGHT;

        void setRotations(@IntRange(from = -3, to = 3) final int rotations) {
            if (rotations < 0) {
                this.rotations = ((rotations < -3) ? rotations % 4 : rotations) + 4;
            } else {
                this.rotations = (rotations > 3) ? rotations % 4 : rotations;
            }
        }

        OnPositionListener createListener() {
            switch (rotations) {
                case 1: // ROTATE_CW1, ROTATE_CCW3
                    return new OnPositionListener() {
                        @Override
                        public int[] initPositionedCoords() {
                            return new int[]{WIDTH - 1, 0};
                        }

                        @Override
                        public void updatePositionedCoords(int[] coords) {
                            coords[1]++;
                            if (coords[1] == HEIGHT) {
                                coords[1] = 0;
                                coords[0]--;
                            }
                        }
                    };
                case 2: // ROTATE_CW2, ROTATE_CCW2
                    return new OnPositionListener() {
                        @Override
                        public int[] initPositionedCoords() {
                            return new int[]{WIDTH - 1, HEIGHT - 1};
                        }

                        @Override
                        public void updatePositionedCoords(int[] coords) {
                            coords[0]--;
                            if (coords[0] == -1) {
                                coords[0] = WIDTH - 1;
                                coords[1]--;
                            }
                        }
                    };
                case 3: // ROTATE_CW3, ROTATE_CCW1
                    return new OnPositionListener() {
                        @Override
                        public int[] initPositionedCoords() {
                            return new int[]{0, HEIGHT - 1};
                        }

                        @Override
                        public void updatePositionedCoords(int[] coords) {
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
                    return new OnPositionListener() {
                        @Override
                        public int[] initPositionedCoords() {
                            return new int[]{0, 0};
                        }

                        @Override
                        public void updatePositionedCoords(int[] coords) {
                            coords[0]++;
                            if (coords[0] == WIDTH) {
                                coords[0] = 0;
                                coords[1]++;
                            }
                        }
                    };
            }
        }

        Positioner withBounds(final int startX, final int startY, final int width, final int height) {
            this.startX = startX;
            this.startY = startY;
            this.width = width;
            this.height = height;
            return this;
        }

        @Override
        public Iterator<Position> iterator() {
            final PositionIterator iterator;
            if (startX > -1 || startY > -1) {
                iterator = new PositionIterator(createListener(), startX, startY, width, height);
                startX = -1;
                startY = -1;
            } else {
                iterator = new PositionIterator(createListener());
            }
            return iterator;
        }

    }

    private interface OnPositionListener {
        /**
         * Initialize the starting coordinates for the positioning action.
         * @return an integer array with the initial values for the positioned X and Y.
         */
        @NonNull
        @Size(value = 2)
        int[] initPositionedCoords();

        /**
         * Update the given rotated X and Y values. Runs when PositionIterator.next() is called.
         * @param coords An array containing the X and Y values to be updated.
         */
        void updatePositionedCoords(@NonNull @Size(value = 2) final int[] coords);
    }

    private static class PositionIterator implements Iterator<Position> {

        private final OnPositionListener onRotationListener;
        @Size(value = 2)
        private final int[] positionedCoords;

        private final int startX;
        private final int endX, endY;

        private int x, y;

        public PositionIterator(@NonNull final OnPositionListener onRotationListener) {
            this(onRotationListener, 0, 0, WIDTH, HEIGHT);
        }

        public PositionIterator(@NonNull final OnPositionListener onRotationListener,
                                final int startX,
                                final int startY,
                                final int width,
                                final int height) {

            if (null == onRotationListener) {
                throw new IllegalArgumentException("onRotationListener must not be null.");
            }
            this.onRotationListener = onRotationListener;
            positionedCoords = onRotationListener.initPositionedCoords();

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
        public Position next() {
            final Position result = new Position(x++, y, positionedCoords[0], positionedCoords[1]);
            if (x == endX) {
                x = startX;
                y++;
            }
            onRotationListener.updatePositionedCoords(positionedCoords);
            return result;
        }

    }

    private static class Position {

        private final int fromX, fromY;
        private final int toX, toY;

        public Position(int fromX, int fromY, int toX, int toY) {
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }

    }

}