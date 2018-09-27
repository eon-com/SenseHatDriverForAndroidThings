package com.eon.androidthings.sensehat.demos;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.SensorManager;

import com.eon.androidthings.sensehatdriverlibrary.SenseHat;
import com.eon.androidthings.sensehatdriverlibrary.devices.JoystickDirectionEnum;
import com.eon.androidthings.sensehatdriverlibrary.devices.JoystickListener;
import com.eon.androidthings.sensehatdriverlibrary.devices.LedMatrix;
import com.eon.androidthings.sensehat.gui.IGui;

import java.io.IOException;

/**
 * Simple demonstration of Joystick movement
 */

public class JoystickDemo {

    private LedMatrix ledMatrix;

    private int[] pixelColors = new int[]{
            Color.RED, Color.YELLOW, Color.YELLOW, Color.GREEN, Color.GREEN, Color.YELLOW, Color.YELLOW, Color.RED
    };

    private int currentXPosition = 3;
    private int currentYPosition = 3;

    private IGui gui;

    public JoystickDemo(SensorManager sensorManager, IGui gui) throws IOException {
        this.gui = gui;

        SenseHat senseHat = SenseHat.getInstance();

        this.ledMatrix = senseHat.getLedMatrix();
        this.ledMatrix.draw(Color.RED);   // turn total of

        this.drawCurrentPixel(this.currentYPosition);


        senseHat.addJoystickListener(new JoystickListener() {
            @Override
            public void stickMoved(JoystickDirectionEnum direction) throws IOException {
                //System.out.println("moved:" + direction);
                int currentColor = -1;
                switch (direction) {
                    case IDLE:
                        //ledMatrix.draw(Color.BLACK);
                        break;
                    case UP:
                        JoystickDemo.this.currentYPosition = JoystickDemo.this.currentYPosition > 0 ? JoystickDemo.this.currentYPosition - 1 : 0;
                        currentColor = JoystickDemo.this.currentYPosition;
                        if (JoystickDemo.this.currentXPosition == 0 || JoystickDemo.this.currentXPosition == 7 || JoystickDemo.this.currentYPosition == 0) {
                            currentColor = 0;
                        }
                        JoystickDemo.this.drawCurrentPixel(currentColor);
                        break;
                    case DOWN:
                        JoystickDemo.this.currentYPosition = JoystickDemo.this.currentYPosition < 8 ? JoystickDemo.this.currentYPosition + 1 : 7;
                        currentColor = JoystickDemo.this.currentYPosition;
                        if (JoystickDemo.this.currentXPosition == 0 || JoystickDemo.this.currentXPosition == 7 || JoystickDemo.this.currentYPosition == 7) {
                            currentColor = 7;
                        }
                        JoystickDemo.this.drawCurrentPixel(currentColor);
                        break;
                    case LEFT:
                        JoystickDemo.this.currentXPosition = JoystickDemo.this.currentXPosition > 0 ? JoystickDemo.this.currentXPosition - 1 : 0;

                        currentColor = JoystickDemo.this.currentXPosition;
                        if (JoystickDemo.this.currentXPosition == 0 || JoystickDemo.this.currentYPosition == 0 || JoystickDemo.this.currentYPosition == 7) {
                            currentColor = 0;
                        }
                        JoystickDemo.this.drawCurrentPixel(currentColor);
                        break;
                    case RIGHT:
                        JoystickDemo.this.currentXPosition = JoystickDemo.this.currentXPosition < 8 ? JoystickDemo.this.currentXPosition + 1 : 7;
                        currentColor = JoystickDemo.this.currentXPosition;
                        if (JoystickDemo.this.currentXPosition == 7 || JoystickDemo.this.currentYPosition == 0 || JoystickDemo.this.currentYPosition == 7) {
                            currentColor = 7;
                        }
                        JoystickDemo.this.drawCurrentPixel(currentColor);
                        break;
                    case BUTTON_PRESSED:
                        JoystickDemo.this.ledMatrix.draw(Color.WHITE);
                        break;
                }

            }
        });


    }

    private void drawCurrentPixel(int colorPosition) throws IOException {
        Bitmap bitmap = Bitmap.createBitmap(8, 8, Bitmap.Config.ARGB_8888);
        int currentColor = this.pixelColors[colorPosition];
        bitmap.setPixel(this.currentXPosition, this.currentYPosition, currentColor);

        this.ledMatrix.draw(bitmap);

        String colorText = Color.valueOf(currentColor).getColorSpace().toString();
        this.gui.setCursorInformations("" + this.currentXPosition,//
                "" + this.currentYPosition, //
                colorText);
    }

}
