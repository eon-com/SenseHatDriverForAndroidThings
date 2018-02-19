package com.eon.androidthings.sensehatdriverlibrary.devices.fonts;

import android.content.res.AssetManager;

/**
 * TODO Description
 */

public class BlackWhiteFont extends AbstractLEDFont {

    private static final String FONT_IMAGE = "BlackWhiteFont.png";
    //    ----------------------------------------------------- Instance Variables
    private static final String FONT_LETTERS = " ABCDEFGHIJKLMNOPQRSTUVWXYZÅÄÖÉÜabcdefghijklmnopqrstuvwxyzåäöéü0123456789.,?!\"#$%&-+*:;/\\<>()'`=";


    // ------------------------------------------------------------ Constructor
    public BlackWhiteFont(AssetManager assetManager) {
        super(assetManager);
    }

    // --------------------------------------------------------- Public Methods
    @Override
    public String getFontImageResourceName() {
        return FONT_IMAGE;
    }

    @Override
    public String getFontLetters() {
        return FONT_LETTERS;
    }

    protected int enhancePixelColor(int x, int y, int currentColor) {
        String colorAsHex = Integer.toHexString(currentColor);

//        currentColor = currentColor | 16777215; // white
        currentColor = currentColor | 255; // blue
        String colorAsHex2 = Integer.toHexString(currentColor);
        return currentColor;
    }


}
