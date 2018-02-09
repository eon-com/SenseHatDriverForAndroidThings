package com.eon.androidthings.sensehatdriverlibrary.devices;


public class AccelerometerSensorConstants {
    // see https://github.com/eclipse/kura/blob/develop/kura/examples/org.eclipse.kura.raspberrypi.sensehat/src/main/java/org/eclipse/kura/raspberrypi/sensehat/sensors/LSM9DS1.java
    // Accelerometer and gyroscope register address map
    public static final int ACT_THS = 0x04;
    public static final int ACT_DUR = 0x05;
    public static final int INT_GEN_CFG_XL = 0x06;
    public static final int INT_GEN_THS_X_XL = 0x07;
    public static final int INT_GEN_THS_Y_XL = 0x08;
    public static final int INT_GEN_THS_Z_XL = 0x09;
    public static final int INT_GEN_DUR_XL = 0x0A;
    public static final int REFERENCE_G = 0x0B;
    public static final int INT1_CTRL = 0x0C;
    public static final int INT2_CTRL = 0x0D;
    public static final int WHO_AM_I_XG = 0x0F;
    public static final int CTRL_REG1_G = 0x10;
    public static final int CTRL_REG2_G = 0x11;
    public static final int CTRL_REG3_G = 0x12;
    public static final int ORIENT_CFG_G = 0x13;
    public static final int INT_GEN_SRC_G = 0x14;
    public static final int OUT_TEMP_L = 0x15;
    public static final int OUT_TEMP_H = 0x16;
    public static final int STATUS_REG_0 = 0x17;
    public static final int OUT_X_L_G = 0x18;
    public static final int OUT_X_H_G = 0x19;
    public static final int OUT_Y_L_G = 0x1A;
    public static final int OUT_Y_H_G = 0x1B;
    public static final int OUT_Z_L_G = 0x1C;
    public static final int OUT_Z_H_G = 0x1D;
    public static final int CTRL_REG4 = 0x1E;
    public static final int CTRL_REG5_XL = 0x1F;
    public static final int CTRL_REG6_XL = 0x20;
    public static final int CTRL_REG7_XL = 0x21;
    public static final int CTRL_REG8 = 0x22;
    public static final int CTRL_REG9 = 0x23;
    public static final int CTRL_REG10 = 0x24;
    public static final int INT_GEN_SRC_XL = 0x26;
    public static final int STATUS_REG_1 = 0x27;
    public static final int OUT_X_L_XL = 0x28;
    public static final int OUT_X_H_XL = 0x29;
    public static final int OUT_Y_L_XL = 0x2A;
    public static final int OUT_Y_H_XL = 0x2B;
    public static final int OUT_Z_L_XL = 0x2C;
    public static final int OUT_Z_H_XL = 0x2D;
    public static final int FIFO_CTRL = 0x2E;
    public static final int FIFO_SRC = 0x2F;
    public static final int INT_GEN_CFG_G = 0x30;
    public static final int INT_GEN_THS_XH_G = 0x31;
    public static final int INT_GEN_THS_XL_G = 0x32;
    public static final int INT_GEN_THS_YH_G = 0x33;
    public static final int INT_GEN_THS_YL_G = 0x34;
    public static final int INT_GEN_THS_ZH_G = 0x35;
    public static final int INT_GEN_THS_ZL_G = 0x36;
    public static final int INT_GEN_DUR_G = 0x37;

    // Magnetic sensor register address map
    public static final int OFFSET_X_REG_L_M = 0x05;
    public static final int OFFSET_X_REG_H_M = 0x06;
    public static final int OFFSET_Y_REG_L_M = 0x07;
    public static final int OFFSET_Y_REG_H_M = 0x08;
    public static final int OFFSET_Z_REG_L_M = 0x09;
    public static final int OFFSET_Z_REG_H_M = 0x0A;
    public static final int WHO_AM_I_M = 0x0F;
    public static final int CTRL_REG1_M = 0x20;
    public static final int CTRL_REG2_M = 0x21;
    public static final int CTRL_REG3_M = 0x22;
    public static final int CTRL_REG4_M = 0x23;
    public static final int CTRL_REG5_M = 0x24;
    public static final int STATUS_REG_M = 0x27;
    public static final int OUT_X_L_M = 0x28;
    public static final int OUT_X_H_M = 0x29;
    public static final int OUT_Y_L_M = 0x2A;
    public static final int OUT_Y_H_M = 0x2B;
    public static final int OUT_Z_L_M = 0x2C;
    public static final int OUT_Z_H_M = 0x2D;
    public static final int INT_CFG_M = 0x30;
    public static final int INT_SRC_M = 0x31;
    public static final int INT_THS_L_M = 0x32;
    public static final int INT_THS_H_M = 0x33;

    public static final int WHO_AM_I_AG_ID = 0x68;
    public static final int WHO_AM_I_M_ID = 0x3D;

    public static final int ACC_DEVICE = 0;
    public static final int MAG_DEVICE = 1;

    public static final float ACC_SCALE_2G = 0.000061F;
    public static final float ACC_SCALE_4G = 0.000122F;
    public static final float ACC_SCALE_8G = 0.000244F;
    public static final float ACC_SCALE_16G = 0.000732F;

    public static final float ACCEL_CAL_MIN_X = -0.988512F;
    public static final float ACCEL_CAL_MIN_Y = -1.011500F;
    public static final float ACCEL_CAL_MIN_Z = -1.012328F;
    public static final float ACCEL_CAL_MAX_X = 1.006410F;
    public static final float ACCEL_CAL_MAX_Y = 1.004973F;
    public static final float ACCEL_CAL_MAX_Z = 1.001244F;

    public static final float GYRO_SCALE_250 = (float) (Math.PI / 180.0) * 0.00875F;
    public static final float GYRO_SCALE_500 = (float) (Math.PI / 180.0) * 0.0175F;
    public static final float GYRO_SCALE_2000 = (float) (Math.PI / 180.0) * 0.07F;

    public static final float GYRO_BIAS_X_INIT = 0.024642F;
    public static final float GYRO_BIAS_Y_INIT = 0.020255F;
    public static final float GYRO_BIAS_Z_INIT = -0.011905F;

    public static final float GYRO_LEARNING_ALPHA = 2.0F;
    public static final float GYRO_CONTINIOUS_ALPHA = 0.01F;

    public static final float ACC_ZERO = 0.05F;
    public static final float GYRO_ZERO = 0.2F;

    public static final float MAG_SCALE_4 = 0.014F;
    public static final float MAG_SCALE_8 = 0.029F;
    public static final float MAG_SCALE_12 = 0.043F;
    public static final float MAG_SCALE_16 = 0.058F;

    public static final float COMPASS_ALPHA = 0.2F;
    public static final float COMPASS_MIN_X = -26.074535F;
    public static final float COMPASS_MIN_Y = -2.034567F;
    public static final float COMPASS_MIN_Z = -14.253133F;
    public static final float COMPASS_MAX_X = 49.599648F;
    public static final float COMPASS_MAX_Y = 70.567223F;
    public static final float COMPASS_MAX_Z = 55.166424F;
    public static final float COMPASS_ELLIPSOID_OFFSET_X = 0.268940F;
    public static final float COMPASS_ELLIPSOID_OFFSET_Y = 0.530345F;
    public static final float COMPASS_ELLIPSOID_OFFSET_Z = -0.120908F;
    public static final float COMPASS_ELLIPSOID_CORR_11 = 0.973294F;
    public static final float COMPASS_ELLIPSOID_CORR_12 = -0.014069F;
    public static final float COMPASS_ELLIPSOID_CORR_13 = -0.021423F;
    public static final float COMPASS_ELLIPSOID_CORR_21 = -0.014069F;
    public static final float COMPASS_ELLIPSOID_CORR_22 = 0.965692F;
    public static final float COMPASS_ELLIPSOID_CORR_23 = -0.002746F;
    public static final float COMPASS_ELLIPSOID_CORR_31 = -0.021423F;
    public static final float COMPASS_ELLIPSOID_CORR_32 = -0.002746F;
    public static final float COMPASS_ELLIPSOID_CORR_33 = 0.980103F;

}
