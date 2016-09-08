package com.rybak.tool.similarity.comparison;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Comparison {

    private final Logger log = LoggerFactory.getLogger(Comparison.class);

    public BufferedImage startComparison(BufferedImage screenShotImg, BufferedImage baseImg) throws IOException {
        return startComparison(screenShotImg, baseImg, Collections.emptyList());
    }

    public BufferedImage startComparison(BufferedImage screenShotImg, BufferedImage baseImg, List<Rect> excludeRects) throws IOException {
        LinkedList<Rect> rectangles = null;

        int width = baseImg.getWidth();
        int height = baseImg.getHeight();

        int length = baseImg.getWidth() * baseImg.getHeight();

        int[][] baseFrame = new int[height][width];
        int[][] screenShot = new int[height][width];

        preparationData(screenShotImg, baseImg, width, height, length, baseFrame, screenShot);

        // Finding Differences between Image
        long startTime = System.nanoTime();
        ImageDifference imDiff = new ImageDifference();
        rectangles = imDiff.differenceImage(baseFrame, screenShot, width, height, excludeRects);
        long endTime = System.nanoTime();

        // Creating the Result Image
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = result.createGraphics();
        g2d.drawImage(screenShotImg, 0, 0, null);

        // Mark the rectangles found
        int index = 0;
        for (Rect rect : rectangles) {
            log.info(++index + "." + rect);

            g2d.setColor(Color.RED);
            g2d.drawRect(rect.getY(), rect.getX(), rect.getW(), rect.getH());
        }

        double d = ((double) (endTime - startTime) / 1000000);
        log.debug("\nTotal Time : {} ms", d);

        return result;
    }

    private void preparationData(BufferedImage screenShotImg, BufferedImage baseImg, int width, int height, int length, int[][] baseFrame, int[][] screenShot) {
        int[] baseImgPix = new int[length];
        int[] screenShotImgPix = new int[length];

        baseImg.getRGB(0, 0, baseImg.getWidth(), baseImg.getHeight(), baseImgPix, 0, baseImg.getWidth());
        screenShotImg.getRGB(0, 0, screenShotImg.getWidth(), screenShotImg.getHeight(), screenShotImgPix, 0, screenShotImg.getWidth());

        long start = System.nanoTime();
        for (int row = 0; row < height; row++) {
            System.arraycopy(baseImgPix, (row * width), baseFrame[row], 0, width);
            System.arraycopy(screenShotImgPix, (row * width), screenShot[row], 0, width);
        }
        long end = System.nanoTime();

        log.debug("Array Copy : {}" , ((double) (end - start) / 1000000));
    }
}
