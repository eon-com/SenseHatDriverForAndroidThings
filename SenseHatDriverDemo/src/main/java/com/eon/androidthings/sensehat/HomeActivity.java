package com.eon.androidthings.sensehat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.eon.androidthings.sensehat.gui.IGui;
import com.eon.androidthings.sensehatdriverlibrary.SenseHat;
import com.eon.androidthings.sensehatdriverlibrary.devices.LedMatrix;
import com.eon.androidthings.sensehat.demos.JoystickDemo;
import com.eon.androidthings.sensehat.demos.LedDrawingDemo;
import com.eon.androidthings.sensehat.uitils.NetworkUtils;

import org.apache.commons.lang3.exception.ExceptionUtils;

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
    private LedDrawingDemo ledDrawingDemo;

    private TextView cursorCoordTextView;
    private TextView cursorColorTextView;
    private TextView ipAdressTextView;
    private TextView exceptionTextView;

    private TextView tempTextView;
    private TextView humTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {


            // first init Views, so that the following method could use the UI
            this.setContentView(R.layout.activity_home);
            this.cursorCoordTextView = this.findViewById(R.id.cursorCoordTextView);
            this.cursorColorTextView = this.findViewById(R.id.cursorColorTextView);
            this.ipAdressTextView = this.findViewById(R.id.ipAdressTextView);
            this.exceptionTextView = this.findViewById(R.id.exceptionTextView);
            this.tempTextView = this.findViewById(R.id.temperatureTextView);
            this.humTextView = this.findViewById(R.id.humidityTextView);


            String myIP = "********************** IP: " + NetworkUtils.getIPAddress(true) + " **********************";
            this.ipAdressTextView.setText(myIP);
            System.out.println("**** myIP:" + myIP);


            // ********************
            SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
            SenseHat senseHat = SenseHat.init(sensorManager);
            final LedMatrix ledMatrix = senseHat.getLedMatrix();
            ledMatrix.draw(Color.RED);    // trun off


            /** Text-Scrolling
             */
            this.ledDrawingDemo = new LedDrawingDemo(this);

            /**
             * Humidity and temperature demo...
             */
            SensorEventListener temperatureListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if(event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                        tempTextView.setText(event.values[0] + " °C");
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    if(sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                        System.out.println("TEMP-ACUU:" + sensor + " acc:''" + accuracy);
                    }
                }
            };

            SensorEventListener humidityListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    humTextView.setText(event.values[0] + " %");
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    System.out.println("HUM-ACUU:" + sensor + " acc:''" + accuracy);
                }
            };

            senseHat.addHumidityTempatureSensorListener(humidityListener, temperatureListener);

            /** Simple Joystick demo*/
            this.joystickDemo = new JoystickDemo(new IGui() {
                @Override
                public void setCursorInformations(final String xCoord, final String yCoord, final String color)

                {

                    HomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String coord = xCoord + "/" + yCoord;
                            HomeActivity.this.cursorCoordTextView.setText(coord);
                            HomeActivity.this.cursorColorTextView.setText(color);
                        }
                    });

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            String ex = ExceptionUtils.getStackTrace(e);
            this.exceptionTextView.setText(ex);
        }
    }

}
