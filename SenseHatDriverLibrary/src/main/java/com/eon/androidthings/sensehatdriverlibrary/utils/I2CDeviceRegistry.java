package com.eon.androidthings.sensehatdriverlibrary.utils;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.HashMap;


/**
 * Registry for all I2C-Devices
 */
public class I2CDeviceRegistry {

    // -------------------------------------------------------------- Constants
    private static final String I2C_BUS_NAME = "I2C1";

    // ----------------------------------------------------- Instance Variables

    private static final HashMap<Integer, I2cDevice> registry = new HashMap<>();

    // --------------------------------------------------------- Public Methods

    /**
     * If the device is already in the registry -> deliver this instance.
     * If not, open new device with the PeripheralManagerService.
     *
     * @param i2cAddress
     * @return
     * @throws IOException
     */
    public static I2cDevice openOrReUseDevice(int i2cAddress) throws IOException {
        I2cDevice i2cDevice = registry.get(i2cAddress);
        if (i2cDevice == null) {
            PeripheralManagerService peripheralManagerService = new PeripheralManagerService();
            i2cDevice = peripheralManagerService.openI2cDevice(I2CDeviceRegistry.I2C_BUS_NAME, i2cAddress);
            registry.put(i2cAddress, i2cDevice);
        }
        return i2cDevice;
    }

    /**
     * Remove the i2cDevice from registry, nothing more.
     *
     * @param i2cAddress
     * @return
     */
    public static I2cDevice removeFromRegistry(int i2cAddress) {
        return registry.remove(i2cAddress);
    }
}
