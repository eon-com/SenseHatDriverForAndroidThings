package com.eon.androidthings.sensehatdriverlibrary.devices;

import java.io.IOException;

/**
 * Listen for Joystick-Events
 */
public interface JoystickListener {

    /**
     * Jostick moved?
     *
     * @param direction
     * @throws IOException
     */
    public void stickMoved(JoystickDirectionEnum direction) throws IOException;
}
