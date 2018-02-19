package com.eon.androidthings.sensehatdriverlibrary.devices.fonts;

import android.graphics.Bitmap;

/**
 * TODO Description
 */

public interface LEDFont {

    public Bitmap getBitmapForLetter(char letter);

    public Bitmap getBitmapForSentence(String sentence);

}