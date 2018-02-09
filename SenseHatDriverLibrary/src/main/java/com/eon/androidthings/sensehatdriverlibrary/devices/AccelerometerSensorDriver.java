package com.eon.androidthings.sensehatdriverlibrary.devices;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.eon.androidthings.sensehatdriverlibrary.utils.I2CDeviceRegistry;
import com.google.android.things.pio.I2cDevice;
import com.google.android.things.userdriver.UserSensor;
import com.google.android.things.userdriver.UserSensorDriver;
import com.google.android.things.userdriver.UserSensorReading;

import java.io.IOException;
import java.util.UUID;

/**
 * Test without Driver-Class
 * TODO not implemented yet
 */

public class AccelerometerSensorDriver extends UserSensorDriver implements AutoCloseable {

    // TODO ANPASSEN an LSM9DS1		Accelerometer and Magnetometer

    protected static final int I2C_ADDRESS = 0x6a;

    private static final String DRIVER_VENDOR = "STMicroelectronics";
    private static final String DRIVER_NAME = "HTS221";
    private static final float DRIVER_MAX_RANGE = HumidityTemperatureSensor.MAX_HUMIDITY_PERCENT;
    private static final float DRIVER_RESOLUTION = 0.004f;
    private static final float DRIVER_POWER = HumidityTemperatureSensor.MAX_POWER_CONSUMPTION_UA / 1000f;
    private static final int DRIVER_VERSION = 1;
    private static final String DRIVER_REQUIRED_PERMISSION = "";
    private static final int DRIVER_MIN_DELAY_US = Math.round(1000000.0f / HumidityTemperatureSensor.MAX_FREQ_HZ);
    private static final int DRIVER_MAX_DELAY_US = Math.round(1000000.0f / HumidityTemperatureSensor.MIN_FREQ_HZ);

    private SensorManager sensorManager;

    private UserSensor userSensor;
    private I2cDevice i2cDevice;    // read data from Bus

    public AccelerometerSensorDriver(SensorManager sensorManager) throws IOException {
        this.sensorManager = sensorManager;

        this.initSensor();
    }

    // Autocloseable
    @Override
    public void close() throws Exception {
        // TODO relase sensor device
    }

    // UserSensorDriver
    @Override
    public UserSensorReading read() throws IOException {
        return new UserSensorReading(new float[]{
// TODO @@@ hier gehts weiter
//                i2cDevice.write(register);
//                Thread.sleep(5);
//                int result = i2cDevice.read();
//
//                sensor.readTemperature()


        });
    }


    public float[] getAccelerometerRaw() throws InterruptedException {

        // Accelerometer x y z raw data in Gs
        float[] acc = new float[3];

        int accFS = 0;

//        try {
        accFS = this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.CTRL_REG6_XL) & 0x00000018;
        if (accFS == 0x00000000) { // +/-2g
            acc[0] = (this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_X_H_XL) << 8 | this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_X_L_XL) & 0x000000FF) * AccelerometerSensorConstants.ACC_SCALE_2G;
            acc[1] = (this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_Y_H_XL) << 8 | this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_Y_L_XL) & 0x000000FF) * AccelerometerSensorConstants.ACC_SCALE_2G;
            acc[2] = (this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_Z_H_XL) << 8 | this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_Z_L_XL) & 0x000000FF) * AccelerometerSensorConstants.ACC_SCALE_2G;
        } else if (accFS == 0x00000010) { // +/-4g
            acc[0] = (this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_X_H_XL) << 8 | this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_X_L_XL) & 0x000000FF) * AccelerometerSensorConstants.ACC_SCALE_4G;
            acc[1] = (this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_Y_H_XL) << 8 | this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_Y_L_XL) & 0x000000FF) * AccelerometerSensorConstants.ACC_SCALE_4G;
            acc[2] = (this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_Z_H_XL) << 8 | this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_Z_L_XL) & 0x000000FF) * AccelerometerSensorConstants.ACC_SCALE_4G;
        } else if (accFS == 0x00000018) { // +/-8g
            acc[0] = (this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_X_H_XL) << 8 | this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_X_L_XL) & 0x000000FF) * AccelerometerSensorConstants.ACC_SCALE_8G;
            acc[1] = (this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_Y_H_XL) << 8 | this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_Y_L_XL) & 0x000000FF) * AccelerometerSensorConstants.ACC_SCALE_8G;
            acc[2] = (this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_Z_H_XL) << 8 | this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_Z_L_XL) & 0x000000FF) * AccelerometerSensorConstants.ACC_SCALE_8G;
        } else if (accFS == 0x00000008) { // +/-16g
            acc[0] = (this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_X_H_XL) << 8 | this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_X_L_XL) & 0x000000FF)
                    * AccelerometerSensorConstants.ACC_SCALE_16G;
            acc[1] = (this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_Y_H_XL) << 8 | this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_Y_L_XL) & 0x000000FF)
                    * AccelerometerSensorConstants.ACC_SCALE_16G;
            acc[2] = (this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_Z_H_XL) << 8 | this.read(AccelerometerSensorConstants.ACC_DEVICE, AccelerometerSensorConstants.OUT_Z_L_XL) & 0x000000FF)
                    * AccelerometerSensorConstants.ACC_SCALE_16G;
        }

        acc[0] = -acc[0];
        acc[1] = -acc[1];

        this.calibrateAcceleration(acc);

        // Swap X and Y axis to match SenseHat library
        float accTemp = acc[1];
        acc[1] = acc[0];
        acc[0] = accTemp;

//        } catch (KuraException e) {
//            s_logger.error("Unable to read to I2C device.", e);
//        }

        return acc;

    }

    private int read(int device, int register) throws InterruptedException {
        int result = 0;
//        try {
        if (device == 0) {
            //@@@@  i2cDevice.write(register);
            Thread.sleep(5);
            //@@@result = i2cDevice.read();
        }
//        } catch (IOException e) {
//            s_logger.error("Unable to read to I2C device", e);
//        } catch (InterruptedException e1) {
//            s_logger.error(e1.toString());
//        }

        return result;
    }

    private void calibrateAcceleration(float[] acc) {

        if (acc[0] >= 0.0) {
            acc[0] = acc[0] / AccelerometerSensorConstants.ACCEL_CAL_MAX_X;
        } else {
            acc[0] = acc[0] / -AccelerometerSensorConstants.ACCEL_CAL_MIN_X;
        }

        if (acc[1] >= 0.0) {
            acc[1] = acc[1] / AccelerometerSensorConstants.ACCEL_CAL_MAX_Y;
        } else {
            acc[1] = acc[1] / -AccelerometerSensorConstants.ACCEL_CAL_MIN_Y;
        }

        if (acc[2] >= 0.0) {
            acc[2] = acc[2] / AccelerometerSensorConstants.ACCEL_CAL_MAX_Z;
        } else {
            acc[2] = acc[2] / -AccelerometerSensorConstants.ACCEL_CAL_MIN_Z;
        }

    }

    private void initSensor() throws IOException {

        this.i2cDevice = I2CDeviceRegistry.openOrReUseDevice(AccelerometerSensorDriver.I2C_ADDRESS);

        this.userSensor = new UserSensor.Builder()
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


}
