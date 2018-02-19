package com.eon.androidthings.sensehatdriverlibrary.devices.fonts;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.eon.androidthings.sensehatdriverlibrary.devices.LedMatrix;

import java.io.InputStream;
import java.util.HashMap;

/**
 * TODO Description
 */

public abstract class AbstractLEDFont implements LEDFont {

    abstract public String getFontImageResourceName();

    abstract public String getFontLetters();

    private AssetManager assetManager;
    // Stores all parsed Bitmap-Letters
    private HashMap<Integer, Bitmap> allChars = new HashMap<>();

    public AbstractLEDFont(AssetManager assetManager) {

        this.assetManager = assetManager;
        // Start parsing the font
        try {
            String fontImage = this.getFontImageResourceName();
            String fontLetters = this.getFontLetters();
            InputStream imageStream = assetManager.open(fontImage);
            Bitmap fontBitmap = BitmapFactory.decodeStream(imageStream);

            // Firstline of the fontBitmap indicates if a char starts or not
            // Second to 9 line is the char itself
            int fontWidth = fontBitmap.getWidth();
            int fontHight = fontBitmap.getHeight();

            byte[] fontChars = fontLetters.getBytes();

            int currentLetter = 0;
            boolean charStarts = false;
            int charStartsX = 0;
            int charStopsX = 0;
            for (int currentX = 0; currentX < fontWidth; currentX++) {

                if (currentLetter >= fontChars.length) {
                    System.out.println("break");
                    break;
                }
                char letter = (char) fontChars[currentLetter++];

                int indicationPixel = fontBitmap.getPixel(currentX, 0);
                // RRGGBBAA -> FF000000
                String pixelColors = Integer.toHexString(indicationPixel);
//                System.out.println(pixelColors + " " +a);
                if (pixelColors.toUpperCase().startsWith("FF")) {
                    charStartsX = currentX;
                    for (int charEnds = currentX + 1; charEnds < fontWidth; charEnds++) {
                        indicationPixel = fontBitmap.getPixel(charEnds, 0);
                        pixelColors = Integer.toHexString(indicationPixel);
                        if (pixelColors.toUpperCase().startsWith("FF")) {
                            charStopsX = charEnds;
                            break;
                        }
                    }
                    System.out.println("Current Letter: '" + letter + "' X-Size (stop-start):" + (charStopsX - charStartsX));
                    Bitmap bitmapChar = Bitmap.createBitmap(//
                            LedMatrix.WIDTH,//
                            LedMatrix.HEIGHT, //
                            Bitmap.Config.ARGB_8888);
                    int y = 0;
                    for (int copyY = 1; copyY < fontHight; copyY++) {
                        int x = 0;
                        for (int copyX = charStartsX; copyX < charStopsX; copyX++) {
                            int color = fontBitmap.getPixel(copyX, copyY);
                            color = this.enhancePixelColor(x, y, color);
                            pixelColors = Integer.toHexString(color);

                            if (x < LedMatrix.WIDTH && y < LedMatrix.HEIGHT) {
                                System.out.println(pixelColors);
                                bitmapChar.setPixel(x, y, color);
                            }
                            x++;
                        }
                        y++;
                    }
                    this.allChars.put((int) letter, bitmapChar);

                    currentX = charStopsX - 1;
                }
            }
            // TODO get last char
            System.out.println("ende");


        } catch (Exception e) {
// TODO Exception Handling
            e.printStackTrace();
        }

    }

    // --------------------------------------------------------- Public Methods
    public Bitmap getBitmapForLetter(char letter) {
        return this.allChars.get((int) letter);
    }

    public Bitmap getBitmapForSentence(String sentence) {

        // TODO:low evaluate the real width
        Bitmap bitmapSentence = Bitmap.createBitmap(//
                LedMatrix.WIDTH * sentence.length(),//
                LedMatrix.HEIGHT, //
                Bitmap.Config.ARGB_8888);
        int currentXOffset = 0;
        char[] allLetters = sentence.toCharArray();
        for (char currentChar : allLetters) {
            Bitmap charBitmap = this.getBitmapForLetter(currentChar);
            // do a simple copy
            for (int y = 0; y < charBitmap.getHeight(); y++) {
                for (int x = 0; x < charBitmap.getWidth(); x++) {
                    int color = charBitmap.getPixel(x, y);
                    bitmapSentence.setPixel(x + currentXOffset, y, color);
                }
            }
            currentXOffset += charBitmap.getWidth();
        }
        return bitmapSentence;
    }

    protected int enhancePixelColor(int x, int y, int currentColor) {
        return currentColor;
    }

    // -------------------------------------------------------- Private Methods


}
