package com.eon.androidthings.sensehatdriverlibrary.devices.fonts;

import android.content.res.AssetManager;

/**
 * TODO Description
 */

public class MultiColorFont extends AbstractLEDFont {

    //    ----------------------------------------------------- Instance Variables
    private static final String MULTICOLOR_FONT_IMAGE = "ColorFont.png";
    private static final String FONT_LETTERS = " ABCDEFGHIJKLMNOPQRSTUVWXYZÅÄÖÉÜabcdefghijklmnopqrstuvwxyzåäöéü0123456789.,?!\"#$%&-+*:;/\\<>()'`=";


    // ------------------------------------------------------------ Constructor
    public MultiColorFont(AssetManager assetManager) {
        super(assetManager);
    }

    // --------------------------------------------------------- Public Methods
    @Override
    public String getFontImageResourceName() {
        return MULTICOLOR_FONT_IMAGE;
    }

    @Override
    public String getFontLetters() {
        return FONT_LETTERS;
    }


}
