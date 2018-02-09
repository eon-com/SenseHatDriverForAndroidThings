package com.eon.androidthings.sensehatdriverlibrary.devices;

import android.support.annotation.IntDef;
import android.support.annotation.VisibleForTesting;

import com.eon.androidthings.sensehatdriverlibrary.utils.I2CDeviceRegistry;
import com.google.android.things.pio.I2cDevice;

import java.io.IOException;

/**
 * I2C  Address  0x5f: 		HTS221		Humidity/Temperature
 */

public class HumidityTemperatureSensor implements AutoCloseable {

    // -------------------------------------------------------------- Constants

    public static final float MAX_HUMIDITY_PERCENT = 100f;
    public static final float MAX_TEMP_C = 120f;
    public static final float MAX_POWER_CONSUMPTION_UA = 22.5f;
    public static final float MAX_FREQ_HZ = 12.5f;
    public static final float MIN_FREQ_HZ = 1f;

    private static final int DEVICE_ID = 0xBC;
    private static final int I2C_ADDRESS = 0x5F;

    @IntDef({AV_CONF_AVGH_4, AV_CONF_AVGH_8, AV_CONF_AVGH_16, AV_CONF_AVGH_32, AV_CONF_AVGH_64,
            AV_CONF_AVGH_128, AV_CONF_AVGH_256, AV_CONF_AVGH_512})
    private @interface HumidityAverageConfiguration {
    }

    private static final int AV_CONF_AVGH_4 = 0b000;
    private static final int AV_CONF_AVGH_8 = 0b001;
    private static final int AV_CONF_AVGH_16 = 0b010;
    private static final int AV_CONF_AVGH_32 = 0b011; // Default
    private static final int AV_CONF_AVGH_64 = 0b100;
    private static final int AV_CONF_AVGH_128 = 0b101;
    private static final int AV_CONF_AVGH_256 = 0b110;
    private static final int AV_CONF_AVGH_512 = 0b111;

    @IntDef({AV_CONF_AVGT_2, AV_CONF_AVGT_4, AV_CONF_AVGT_8, AV_CONF_AVGT_16, AV_CONF_AVGT_32,
            AV_CONF_AVGT_64, AV_CONF_AVGT_128, AV_CONF_AVGT_256})
    private @interface TemperatureAverageConfiguration {
    }

    private static final int AV_CONF_AVGT_2 = 0b000;
    private static final int AV_CONF_AVGT_4 = 0b001;
    private static final int AV_CONF_AVGT_8 = 0b010;
    private static final int AV_CONF_AVGT_16 = 0b011; // Default
    private static final int AV_CONF_AVGT_32 = 0b100;
    private static final int AV_CONF_AVGT_64 = 0b101;
    private static final int AV_CONF_AVGT_128 = 0b110;
    private static final int AV_CONF_AVGT_256 = 0b111;

    @IntDef({MODE_POWER_DOWN, MODE_ACTIVE})
    private @interface Mode {
    }

    public static final int MODE_POWER_DOWN = 0;
    public static final int MODE_ACTIVE = 1;

    @IntDef({HTS221_ODR_ONE_SHOT, HTS221_ODR_1_HZ, HTS221_ODR_7_HZ, HTS221_ODR_12_5_HZ})
    private @interface OutputDataRate {
    }

    private static final int HTS221_ODR_ONE_SHOT = 0b00;
    private static final int HTS221_ODR_1_HZ = 0b01;
    private static final int HTS221_ODR_7_HZ = 0b10;
    private static final int HTS221_ODR_12_5_HZ = 0b11;

    private static final int HTS221_REG_WHO_AM_I = 0x0F; // R
    private static final int HTS221_REG_AV_CONF = 0x10; // R/W
    private static final int HTS221_REG_CTRL_REG1 = 0x20; // R/W
    private static final int HTS221_REG_CTRL_REG2 = 0x21; // R/W
    private static final int HTS221_REG_CTRL_REG3 = 0x22; // R/W
    private static final int HTS221_REG_STATUS_REG = 0x27; // R
    private static final int HTS221_REG_HUMIDITY_OUT_L = 0x28; // R
    private static final int HTS221_REG_HUMIDITY_OUT_H = 0x29; // R
    private static final int HTS221_REG_TEMP_OUT_L = 0x2A; // R
    private static final int HTS221_REG_TEMP_OUT_H = 0x2B; // R

    private static final int HTS221_REG_H0_RH_X2 = 0x30;
    private static final int HTS221_REG_H1_RH_X2 = 0x31;
    private static final int HTS221_REG_T0_DEGC_X8 = 0x32;
    private static final int HTS221_REG_T1_DEGC_X8 = 0x33;
    private static final int HTS221_REG_T1_T0_MSB = 0x35;
    private static final int HTS221_REG_H0_T0_OUT_L = 0x36;
    private static final int HTS221_REG_H0_T0_OUT_H = 0x37;
    private static final int HTS221_REG_H1_T0_OUT_L = 0x3A;
    private static final int HTS221_REG_H1_T0_OUT_H = 0x3B;
    private static final int HTS221_REG_T0_OUT_L = 0x3C;
    private static final int HTS221_REG_T0_OUT_H = 0x3D;
    private static final int HTS221_REG_T1_OUT_L = 0x3E;
    private static final int HTS221_REG_T1_OUT_H = 0x3F;

    private static final int HTS221_POWER_DOWN_MASK = 0b10000000;
    private static final int HTS221_BDU_MASK = 0b00000100;
    private static final int HTS221_ODR_MASK = 0b00000011;

    // ----------------------------------------------------- Instance Variables

    private I2cDevice mDevice;

    private final byte[] mBuffer = new byte[2]; // For reading registers

    private int mMode;
    private boolean mBlockDataUpdate;
    private int mOutputDataRate;
    private float[] mTemperatureCalibration, mHumidityCalibration; // Calibration parameters

    // ------------------------------------------------------------ Constructor
    public HumidityTemperatureSensor() throws IOException {

        I2cDevice device = I2CDeviceRegistry.openOrReUseDevice(I2C_ADDRESS);
        try {
            this.connect(device);
        } catch (IOException | RuntimeException e) {
            try {
                this.close();
            } catch (IOException | RuntimeException ignored) {
            }
            throw e;
        }
    }

    // --------------------------------------------------------- Public Methods


    public float readHumidity() throws IOException {
        if (this.mDevice == null) {
            throw new IllegalStateException("I2C device is already closed");
        }

        if (this.isHumidityDataAvailable()) {
            int rawHumidity = (short) this.readRegister(HTS221_REG_HUMIDITY_OUT_L);
            return compensateSample(rawHumidity, this.mHumidityCalibration);
        } else {
            throw new IOException("Humidity data is not yet available");
        }
    }

    public float readTemperature() throws IOException {
        if (this.mDevice == null) {
            throw new IllegalStateException("I2C device is already closed");
        }

        if (this.isTemperatureDataAvailable()) {
            int rawTemp = (short) this.readRegister(HTS221_REG_TEMP_OUT_L);
            return compensateSample(rawTemp, this.mTemperatureCalibration);
        } else {
            throw new IOException("Temperature data is not yet available");
        }
    }

    public void setMode(@Mode int mode) throws IOException {
        if (this.mDevice == null) {
            throw new IllegalStateException("I2C device is already closed");
        }

        int regCtrl = this.mDevice.readRegByte(HTS221_REG_CTRL_REG1) & 0xFF;
        if (mode == MODE_POWER_DOWN) {
            regCtrl &= (~HTS221_POWER_DOWN_MASK & 0xFF);
        } else {
            regCtrl |= HTS221_POWER_DOWN_MASK;
        }
        this.mDevice.writeRegByte(HTS221_REG_CTRL_REG1, (byte) regCtrl);
        this.mMode = mode;
    }

    // -------------------------------------------------------- Private Methods
    private void connect(I2cDevice device) throws IOException {
        this.mDevice = device;

        if ((this.mDevice.readRegByte(HTS221_REG_WHO_AM_I) & 0xFF) != DEVICE_ID) {
            throw new IllegalStateException("I2C device is not HTS221 sensor");
        }

        this.setAveragedSamples(AV_CONF_AVGH_32, AV_CONF_AVGT_16);
        this.setBlockDataUpdate(true);
        this.setOutputDataRate(HTS221_ODR_1_HZ);
        this.setMode(MODE_ACTIVE);

        this.readCalibration();
    }


    private void setBlockDataUpdate(boolean enabled) throws IOException {
        if (this.mDevice == null) {
            throw new IllegalStateException("I2C device is already closed");
        }

        int regCtrl = this.mDevice.readRegByte(HTS221_REG_CTRL_REG1) & 0xFF;
        regCtrl &= (~HTS221_BDU_MASK & 0xFF);
        if (enabled) {
            regCtrl |= HTS221_BDU_MASK;
        }
        this.mDevice.writeRegByte(HTS221_REG_CTRL_REG1, (byte) regCtrl);
        this.mBlockDataUpdate = enabled;
    }

    private void setOutputDataRate(@OutputDataRate int outputDataRate) throws IOException {
        if (this.mDevice == null) {
            throw new IllegalStateException("I2C device is already closed");
        }

        int regCtrl = this.mDevice.readRegByte(HTS221_REG_CTRL_REG1) & 0xFF;
        regCtrl &= (~HTS221_ODR_MASK & 0xFF);
        regCtrl |= outputDataRate;
        this.mDevice.writeRegByte(HTS221_REG_CTRL_REG1, (byte) regCtrl);
        this.mOutputDataRate = outputDataRate;
    }

    private void setAveragedSamples(@HumidityAverageConfiguration int humidityAverage,
                                    @TemperatureAverageConfiguration int temperatureAverage)
            throws IOException {
        if (this.mDevice == null) {
            throw new IllegalStateException("I2C device is already closed");
        }

        int regCtrl = this.mDevice.readRegByte(HTS221_REG_AV_CONF) & 0xC0;
        regCtrl |= humidityAverage | (temperatureAverage << 3);
        this.mDevice.writeRegByte(HTS221_REG_AV_CONF, (byte) (regCtrl));
    }

    @Override
    public void close() throws IOException {
        if (this.mDevice != null) {
            try {
                this.setMode(MODE_POWER_DOWN);
                this.mDevice.close();
            } finally {
                this.mDevice = null;
            }
        }
    }

    private void readCalibration() throws IOException {
        if (this.mDevice == null) {
            throw new IllegalStateException("I2C device is already closed");
        }

        this.mBuffer[0] = this.mDevice.readRegByte(HTS221_REG_H0_RH_X2);
        this.mBuffer[1] = this.mDevice.readRegByte(HTS221_REG_H1_RH_X2);
        int h0 = (this.mBuffer[0] & 0xFF);
        int h1 = (this.mBuffer[1] & 0xFF);

        int h0T0Out = (short) this.readRegister(HTS221_REG_H0_T0_OUT_L);
        int h1T0Out = (short) this.readRegister(HTS221_REG_H1_T0_OUT_L);

        int t0 = this.mDevice.readRegByte(HTS221_REG_T0_DEGC_X8) & 0xFF;
        int t1 = this.mDevice.readRegByte(HTS221_REG_T1_DEGC_X8) & 0xFF;
        int msb = this.mDevice.readRegByte(HTS221_REG_T1_T0_MSB) & 0x0F;
        t0 |= (msb & 0x03) << 8;
        t1 |= (msb & 0x0C) << 6;

        int t0Out = (short) this.readRegister(HTS221_REG_T0_OUT_L);
        int t1Out = (short) this.readRegister(HTS221_REG_T1_OUT_L);

        this.mHumidityCalibration = calibrateHumidityParameters(h0, h1, h0T0Out, h1T0Out);
        this.mTemperatureCalibration = calibrateTemperatureParameters(t0, t1, t0Out, t1Out);
    }

    @VisibleForTesting
    static float[] calibrateHumidityParameters(int h0, int h1, int h0T0Out, int h1T0Out) {
        float[] humidityParameters = new float[2];
        humidityParameters[0] = ((h1 - h0) / 2.0f) / (h1T0Out - h0T0Out);
        humidityParameters[1] = (h0 / 2.0f) - (humidityParameters[0] * h0T0Out);
        return humidityParameters;
    }

    @VisibleForTesting
    static float[] calibrateTemperatureParameters(int t0, int t1, int t0Out, int t1Out) {
        float[] temperatureParameters = new float[2];
        temperatureParameters[0] = ((t1 - t0) / 8.0f) / (t1Out - t0Out);
        temperatureParameters[1] = (t0 / 8.0f) - (temperatureParameters[0] * t0Out);
        return temperatureParameters;
    }


    private int readRegister(int address) throws IOException {
        this.mDevice.readRegBuffer(address | 0x80, this.mBuffer, 2);
        return ((this.mBuffer[1] & 0xFF) << 8) | (this.mBuffer[0] & 0xFF);
    }

    private boolean isHumidityDataAvailable() throws IOException {
        return (this.mDevice.readRegByte(HTS221_REG_STATUS_REG) & 0x02) != 0;
    }

    private boolean isTemperatureDataAvailable() throws IOException {
        return (this.mDevice.readRegByte(HTS221_REG_STATUS_REG) & 0x01) != 0;
    }

    @VisibleForTesting
    static float compensateSample(int rawValue, float[] calibration) {
        return rawValue * calibration[0] + calibration[1];
    }


}
