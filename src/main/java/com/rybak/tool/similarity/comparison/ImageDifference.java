package com.rybak.tool.similarity.comparison;

import com.rybak.tool.similarity.comparison.finder.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class ImageDifference {
    private final Logger log = LoggerFactory.getLogger(ImageDifference.class);

    public LinkedList<Rect> differenceImage(int[][] baseFrame, int[][] screenShot, int width, int height, List<Rect> excludeRects) {
        int xOffset = 0;
        int yOffset = 0;

        LinkedList<Rect> rectangles = new LinkedList<>();
        rectangles.addAll(excludeRects);

        int verticalLimit = xOffset + height;
        int horizontalLimit = yOffset + width;

        for (int xRover = xOffset; xRover < verticalLimit; xRover += 1) {
            for (int yRover = yOffset; yRover < horizontalLimit; yRover += 1) {

                if (ComparisonUtils.notEqualsPixel(baseFrame[xRover][yRover],screenShot[xRover][yRover])) {
                    // Skip over the already processed Rectangles
                    int yRoverCurrent = checkYRover(xRover, yRover, rectangles);
                    if (yRoverCurrent != yRover) {
                        continue;
                    }

                    Rect rect = createRect(xOffset, yOffset, xRover, yRover);

                    Scanner[] scanners = {
                            new LeftSideScanner(baseFrame, screenShot),
                            new BottomScanner(baseFrame, screenShot, verticalLimit),
                            new RightSideScanner(baseFrame, screenShot, horizontalLimit)

                    } ;
                    ScannerProcessor scannerProcessor = new ScannerProcessor();
                    scannerProcessor.setScanners(scanners);
                    scannerProcessor.process(rect);

                    removeRectanglesThatComeInsid(rectangles, rect);

                    // Giving a head start to the yRover when a rectangle is found
                    rectangles.addFirst(rect);

                    yRover = rect.getY() + rect.getW() - 1;
                }
            }
        }

        rectangles.removeAll(excludeRects);
        return rectangles;
    }

    private void removeRectanglesThatComeInsid(LinkedList<Rect> rectangles, Rect rect) {
        int idx = 0;
        while (idx < rectangles.size()) {
            Rect r = rectangles.get(idx);
            if (((rect.getX() <= r.getX()) && (rect.getX() + rect.getH() >= r.getX() + r.getH()))
                    && ((rect.getY() <= r.getY() + 5) && (rect.getY() + rect.getW() >= r.getY() + 5 + r.getW()))) {
                rectangles.remove(r);
            } else {
                idx += 1;
            }
        }
    }

    private Rect createRect(int xOffset, int yOffset, int xRover, int yRover) {
        int x = ((xRover - 3) < xOffset) ? xOffset : (xRover - 4);
        int y = ((yRover - 3) < yOffset) ? yOffset : (yRover - 4);
        return new Rect(x, y, 5, 5);
    }

    private int checkYRover(int xRover, final int yRover, LinkedList<Rect> rectangles) {
        for (Rect itrRect : rectangles) {
            if (((xRover < itrRect.getX() + itrRect.getH())
                    && (xRover >= itrRect.getX()))
                    && ((yRover < itrRect.getY() + itrRect.getW())
                    && (yRover >= itrRect.getY()))) {
                return itrRect.getY() + itrRect.getW() - 1;
            }
        }
        return yRover;
    }

}