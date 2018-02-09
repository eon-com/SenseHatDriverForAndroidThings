package com.eon.androidthings.sensehatdriverlibrary.devices;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.android.things.userdriver.UserDriverManager;
import com.google.android.things.userdriver.UserSensor;
import com.google.android.things.userdriver.UserSensorDriver;
import com.google.android.things.userdriver.UserSensorReading;

import java.io.IOException;
import java.util.UUID;

/**
 * Driver for:
 * I2C  Address  0x5f: 		HTS221		Humidity/Temperature
 */

public class HumidityTemperatureSensorDriver implements AutoCloseable {

    // -------------------------------------------------------------- Constants

    private static final String DRIVER_VENDOR = "STMicroelectronics";
    private static final String DRIVER_NAME = "HTS221";
    private static final int DRIVER_MIN_DELAY_US = Math.round(1000000.0f / HumidityTemperatureSensor.MAX_FREQ_HZ);
    private static final int DRIVER_MAX_DELAY_US = Math.round(1000000.0f / HumidityTemperatureSensor.MIN_FREQ_HZ);

    // ----------------------------------------------------- Instance Variables

    private final HumidityTemperatureSensor sensor;
    private final SensorManager sensorManager;

    private SensorEventListener humidityListener;
    private SensorEventListener temperatureListener;

    private HumidityUserDriver humidityUserDriver;
    private TemperatureUserDriver temperatureUserDriver;

    // ------------------------------------------------------------ Constructor
    public HumidityTemperatureSensorDriver(SensorManager sensorManager) throws IOException {
        this.sensorManager = sensorManager;
        this.sensor = new HumidityTemperatureSensor();

    }

    // --------------------------------------------------------- Public Methods

    /**
     * add listeners
     *
     * @param humidityListener
     * @param temperatureListener
     */
    public void addHumidityTemperatoreSensorListener(final SensorEventListener humidityListener, final SensorEventListener temperatureListener) {
        this.humidityListener = humidityListener;
        this.temperatureListener = temperatureListener;
        this.sensorManager.registerDynamicSensorCallback(new SensorManager.DynamicSensorCallback() {
            @Override
            public void onDynamicSensorConnected(Sensor sensor) {
                if (humidityListener != null && sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
                    HumidityTemperatureSensorDriver.this.sensorManager.registerListener(humidityListener, sensor,
                            SensorManager.SENSOR_DELAY_NORMAL);
                } else if (temperatureListener != null && sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                    HumidityTemperatureSensorDriver.this.sensorManager.registerListener(temperatureListener, sensor,
                            SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
        });

        // Ist es evtl. ein reihenfolge problem???
        this.registerHumiditySensor();
        this.registerTemperatureSensor();

    }


    @Override
    public void close() throws Exception {
        this.unRegisterHumiditySensor();
        this.unRegisterTemperatureSensor();
    }

    // -------------------------------------------------------- Private Methods
    private void unRegisterHumiditySensor() {
        if (this.humidityListener != null) {
            this.sensorManager.unregisterListener(this.humidityListener);
        }
        if (this.humidityUserDriver != null) {
            UserDriverManager.getManager().unregisterSensor(this.humidityUserDriver.getUserSensor());
            this.humidityUserDriver = null;
        }
    }

    private void unRegisterTemperatureSensor() {
        if (this.temperatureListener != null) {
            this.sensorManager.unregisterListener(this.temperatureListener);
        }
        if (this.temperatureUserDriver != null) {
            UserDriverManager.getManager().unregisterSensor(this.temperatureUserDriver.getUserSensor());
            this.temperatureUserDriver = null;
        }
    }

    private void registerHumiditySensor() {
        this.humidityUserDriver = new HumidityUserDriver();
        UserDriverManager.getManager().registerSensor(this.humidityUserDriver.getUserSensor());
    }

    private void registerTemperatureSensor() {
        this.temperatureUserDriver = new TemperatureUserDriver();
        UserDriverManager.getManager().registerSensor(this.temperatureUserDriver.getUserSensor());
    }


    private void maybeSleep() throws IOException {
        if ((this.temperatureUserDriver == null || !this.temperatureUserDriver.isEnabled()) &&
                (this.humidityUserDriver == null || !this.humidityUserDriver.isEnabled())) {
            this.sensor.setMode(HumidityTemperatureSensor.MODE_POWER_DOWN);
        } else {
            this.sensor.setMode(HumidityTemperatureSensor.MODE_ACTIVE);
        }
    }


    ////////////////////////////////////// Sensor Driver
    private class HumidityUserDriver extends UserSensorDriver {

        private static final float DRIVER_MAX_RANGE = HumidityTemperatureSensor.MAX_HUMIDITY_PERCENT;
        private static final float DRIVER_RESOLUTION = 0.004f;
        private static final float DRIVER_POWER = HumidityTemperatureSensor.MAX_POWER_CONSUMPTION_UA / 1000f;
        private static final int DRIVER_VERSION = 1;
        private static final String DRIVER_REQUIRED_PERMISSION = "";

        private boolean mEnabled;
        private UserSensor mUserSensor;

        private UserSensor getUserSensor() {
            if (this.mUserSensor == null) {
                this.mUserSensor = new UserSensor.Builder()
                        .setType(Sensor.TYPE_RELATIVE_HUMIDITY)
                        .setName(DRIVER_NAME)
                        .setVendor(DRIVER_VENDOR)
                        .setVersion(DRIVER_VERSION)
                        .setMaxRange(DRIVER_MAX_RANGE)
                        .setResolution(DRIVER_RESOLUTION)
                        .setPower(DRIVER_POWER)
                        .setMinDelay(DRIVER_MIN_DELAY_US)
                        .setRequiredPermission(DRIVER_REQUIRED_PERMISSION)
                        .setMaxDelay(DRIVER_MAX_DELAY_US)
                        .setUuid(UUID.randomUUID())
                        .setDriver(this)
                        .build();
            }
            return this.mUserSensor;
        }

        @Override
        public UserSensorReading read() throws IOException {
            return new UserSensorReading(new float[]{HumidityTemperatureSensorDriver.this.sensor.readHumidity()});
        }

        @Override
        public void setEnabled(boolean enabled) throws IOException {
            this.mEnabled = enabled;
            HumidityTemperatureSensorDriver.this.maybeSleep();
        }

        private boolean isEnabled() {
            return this.mEnabled;
        }

    }

    private class TemperatureUserDriver extends UserSensorDriver {

        private static final float DRIVER_MAX_RANGE = HumidityTemperatureSensor.MAX_TEMP_C;
        private static final float DRIVER_RESOLUTION = 0.016f;
        private static final float DRIVER_POWER = HumidityTemperatureSensor.MAX_POWER_CONSUMPTION_UA / 1000f;
        private static final int DRIVER_VERSION = 1;
        private static final String DRIVER_REQUIRED_PERMISSION = "";

        private boolean mEnabled;
        private UserSensor mUserSensor;

        private UserSensor getUserSensor() {
            if (this.mUserSensor == null) {
                this.mUserSensor = new UserSensor.Builder()
                        .setType(Sensor.TYPE_AMBIENT_TEMPERATURE)
                        .setName(DRIVER_NAME)
                        .setVendor(DRIVER_VENDOR)
                        .setVersion(DRIVER_VERSION)
                        .setMaxRange(DRIVER_MAX_RANGE)
                        .setResolution(DRIVER_RESOLUTION)
                        .setPower(DRIVER_POWER)
                        .setMinDelay(DRIVER_MIN_DELAY_US)
                        .setRequiredPermission(DRIVER_REQUIRED_PERMISSION)
                        .setMaxDelay(DRIVER_MAX_DELAY_US)
                        .setUuid(UUID.randomUUID())
                        .setDriver(this)
                        .build();
            }
            return this.mUserSensor;
        }

        @Override
        public UserSensorReading read() throws IOException {
            return new UserSensorReading(new float[]{HumidityTemperatureSensorDriver.this.sensor.readTemperature()});
        }

        @Override
        public void setEnabled(boolean enabled) throws IOException {
            this.mEnabled = enabled;
            HumidityTemperatureSensorDriver.this.maybeSleep();
        }

        private boolean isEnabled() {
            return this.mEnabled;
        }

    }


}
