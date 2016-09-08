package com.rybak.tool.similarity.comparison;

public class ComparisonUtils {

    public static boolean notEqualsPixel(int pixelFirst, int pixelSecond)
    {
        int red, blue, green;
        int red2, blue2, green2;
        float differenceRed, differenceGreen, differenceBlue, differenceForThisPixel;
        final int sizeOneColor = 255;
        final double delta = 0.1;

        red                 = (pixelFirst & 0x00ff0000) >> 16;
        green               = (pixelFirst & 0x0000ff00) >> 8;
        blue                =  pixelFirst & 0x000000ff;

        red2                = (pixelSecond & 0x00ff0000) >> 16;
        green2              = (pixelSecond & 0x0000ff00) >> 8;
        blue2               =  pixelSecond & 0x000000ff;

        if (red != red2 || green != green2 || blue != blue2) {
            differenceRed   =  red - red2 / sizeOneColor;
            differenceGreen = ( green - green2 ) / sizeOneColor;
            differenceBlue  = ( blue - blue2 ) / sizeOneColor;
            differenceForThisPixel = ( differenceRed + differenceGreen + differenceBlue ) / 3;
            return differenceForThisPixel > (sizeOneColor*delta);
        }

        return false;
    }
}
