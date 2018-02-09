package com.eon.androidthings.sensehatdriverlibrary;

import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.eon.androidthings.sensehatdriverlibrary.devices.HumidityTemperatureSensorDriver;
import com.eon.androidthings.sensehatdriverlibrary.devices.Joystick;
import com.eon.androidthings.sensehatdriverlibrary.devices.JoystickListener;
import com.eon.androidthings.sensehatdriverlibrary.devices.LedMatrix;
import com.eon.androidthings.sensehatdriverlibrary.utils.I2CDeviceRegistry;

import java.io.IOException;

/**
 * SinglePoint of Entry. Gives access to all SensHat-Devices
 * Usage:<code>
 * SenseHat senseHatInstance = SenseHat.init(sensorManager);
 * // in future
 * SenseHat senseHatInstance = SenseHat.getInstamce();
 * // or
 * LedMatrix ledMatrix = SenseHat.getInstamce().getLedMatrix();
 * ledMatrix.draw(Color.GREEN);
 * <p>
 * </code>
 */
public class SenseHat {

    // -------------------------------------------------------------- Constants
    private static SenseHat instance = null;

    // ----------------------------------------------------- Instance Variables
    // Global SensorManager
    private SensorManager sensorManager;

    // Devices, like LED, Joystick, ...
    private LedMatrix ledMatrix = null;
    private Joystick joystick = null;
    // Humidity/Temperature
    private HumidityTemperatureSensorDriver humidityTemperatorSensorDriver;

    // ------------------------------------------------------------ Constructor
    private SenseHat() {
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Initialize the SenseHat
     *
     * @param sensorManager
     * @return
     * @throws IOException
     */
    public static SenseHat init(SensorManager sensorManager) throws IOException {
        if (instance != null) {
            throw new RuntimeException("You already called init-Methode...but you can call SenseHat.destroy()");
        }
        instance = new SenseHat();

        instance.sensorManager = sensorManager;
        instance.initSensors();

        return instance;
    }

    /**
     * Return the single instance of the SenseHat
     *
     * @return
     */
    public static SenseHat getInstance() {
        if (instance == null) {
            throw new RuntimeException("Please call SenseHat.init(..) before you can access the SenseHat!");
        }
        return instance;
    }

    /**
     * Destroys the current single SenseHat, so the init-Methode could call again!
     */
    public static void destroy() {
        instance = null;
        // TODO Should I cleanup the devices???
    }


    /**
     * Returns the LedMatrix instance
     *
     * @return
     */
    public LedMatrix getLedMatrix() {
        return this.ledMatrix;
    }

    /**
     * Adds a joystickListener
     *
     * @param joystickListener
     */
    public void addJoystickListener(JoystickListener joystickListener) {
        this.joystick.addListener(joystickListener);
    }

    /**
     * Adds a humidityListener and temperatureListener
     *
     * @param humidityListener
     * @param temperatureListener
     */
    public void addHumidityTempatureSensorListener(final SensorEventListener humidityListener, final SensorEventListener temperatureListener) {
        this.humidityTemperatorSensorDriver.addHumidityTemperatoreSensorListener(humidityListener, temperatureListener);
    }


    // -------------------------------------------------------- Private Methods

    /**
     * Initialize all Sensors
     *
     * @throws IOException
     */
    private void initSensors() throws IOException {

        // LED Matrix
        this.ledMatrix = new LedMatrix(I2CDeviceRegistry.openOrReUseDevice(LedMatrix.I2C_ADDRESS));

        // Joystick use the same bus
        this.joystick = new Joystick(I2CDeviceRegistry.openOrReUseDevice(Joystick.I2C_ADDRESS));

        // Humidity/Temperature
        this.humidityTemperatorSensorDriver = new HumidityTemperatureSensorDriver(this.sensorManager);

        // Accelerometer
        // TODO @@@ not implemented yet this.accelerometerSensor = new AccelerometerSensorDriver(this.sensorManager);
    }

}
