package com.eon.androidthings.sensehatdriverlibrary.devices;

import com.google.android.things.pio.I2cDevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Jostick Device
 */
public class Joystick {

    // -------------------------------------------------------------- Constants
    // LEDMatrix and Joystick use the same address
    public static final int I2C_ADDRESS = 0x46;

    // ----------------------------------------------------- Instance Variables

    private static final long POLLING_TIME = 100;   // read joystick state all: 100ms // TODO variable joystick poolling time
    private List<JoystickListener> listeners = new ArrayList<>();
    private I2cDevice i2cDevice;

    // ------------------------------------------------------------ Constructor
    public Joystick(I2cDevice i2cDevice) throws IOException {

        this.i2cDevice = i2cDevice;

        this.initJoystick();
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Add  Joystick-Listener
     *
     * @param joystickListener
     */
    public void addListener(JoystickListener joystickListener) {
        this.listeners.add(joystickListener);
    }


    // -------------------------------------------------------- Private Methods

    /**
     * Initlize the Joystick polling state reader
     */
    private void initJoystick() {
        //byte joyState = mDevice.readRegByte(0xf2);
        // Start Listening
        new Thread(new Runnable() {

            @Override
            public void run() {

                do {
                    try {
                        if (Joystick.this.listeners.size() != 0) {

                            byte joyState = Joystick.this.i2cDevice.readRegByte(0xf2);

                            JoystickDirectionEnum direction = this.evaluateDirection(joyState);
                            for (JoystickListener listener : Joystick.this.listeners) {
                                listener.stickMoved(direction);
                            }
//                            System.out.println(joyState);
                        }
                        Thread.sleep(POLLING_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } while (true);

            }

            private JoystickDirectionEnum evaluateDirection(byte joyState) {
                JoystickDirectionEnum direction = JoystickDirectionEnum.IDLE;
                // TODO make it better (nicer)
                if ((joyState & 0x10) != 0) {
                    direction = JoystickDirectionEnum.LEFT;
                } else if ((joyState & 0x02) != 0) {
                    direction = JoystickDirectionEnum.RIGHT;
                } else if ((joyState & 0x04) != 0) {
                    direction = JoystickDirectionEnum.UP;
                } else if ((joyState & 0x01) != 0) {
                    direction = JoystickDirectionEnum.DOWN;
                } else if ((joyState & 0x08) != 0) {
                    direction = JoystickDirectionEnum.BUTTON_PRESSED;
                } else if (joyState == -128) {
                    direction = JoystickDirectionEnum.IDLE;
                }
                return direction;
            }
        }).start();


    }


}
