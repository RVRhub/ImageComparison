package com.rybak.tool.similarity.comparison.finder;

import com.rybak.tool.similarity.comparison.ComparisonUtils;
import com.rybak.tool.similarity.comparison.Rect;


public class RightSideScanner implements Scanner {

    private int[][] baseFrame;
    private int[][] screenShot;
    private int horizontalLimit;

    public RightSideScanner(int[][] baseFrame, int[][] screenShot, int horizontalLimit) {
        this.baseFrame = baseFrame;
        this.screenShot = screenShot;
        this.horizontalLimit = horizontalLimit;
    }

    @Override
    public boolean execute(boolean isRectChanged, Rect rect) {
        int index;
        int limit;
        int rover;
        index = rect.getX();
        limit = rect.getX() + rect.getH();
        while ((index < limit) && (rect.getY() + rect.getW() != horizontalLimit)) {
            rover = rect.getY() + rect.getW() - 1;
            if (ComparisonUtils.notEqualsPixel(baseFrame[index][rover],screenShot[index][rover])) {
                isRectChanged = true;
                rect.setW(rect.getW() + 1);
                index = rect.getX();
                continue;
            }

            index = index + 1;
        }
        return isRectChanged;
    }
}
