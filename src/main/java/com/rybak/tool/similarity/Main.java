package com.rybak.tool.similarity;

import com.rybak.tool.similarity.comparison.Comparison;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args)  {

        try {
            String firstImage = "/Users/roman/repo/ImageComparison/src/main/resources/img/image1.png";
            String secondImage = "/Users/roman/repo/ImageComparison/src/main/resources/img/image2.png";
            String resultFileName = "/Users/roman/repo/ImageComparison/src/main/resources/img/result.png";

            Comparison comparison = new Comparison();
            File file1 = new File(firstImage);
            File file2 = new File(secondImage);

            BufferedImage screenShotImg = ImageIO.read(file1);
            BufferedImage baseImg = ImageIO.read(file2);

            BufferedImage result = comparison.startComparison( baseImg, screenShotImg);

            ImageIO.write(result, "PNG", new File(resultFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
