package com.eon.androidthinks.myfirstapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.eon.androidthings.sensehatdriverlibrary.demos.JoystickDemo;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class HomeActivity extends Activity {

    private JoystickDemo joystickDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            // first init Views, so that the following method could use the UI
            this.setContentView(R.layout.activity_home);

            SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
//            SenseHat senseHat = new SenseHat(sensorManager);
//            final LedMatrix ledMatrix = senseHat.getLedMatrix();
//            ledMatrix.draw(Color.BLACK);    // trun off

            this.joystickDemo = new JoystickDemo(sensorManager);

        } catch (Exception e) {
            // TODO Exception Handling
            e.printStackTrace();
        }
    }

}
