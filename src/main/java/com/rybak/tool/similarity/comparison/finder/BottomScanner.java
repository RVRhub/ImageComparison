package com.rybak.tool.similarity.comparison.finder;

import com.rybak.tool.similarity.comparison.ComparisonUtils;
import com.rybak.tool.similarity.comparison.Rect;


public class BottomScanner implements Scanner {

    private int[][] baseFrame;
    private int[][] screenShot;
    private int verticalLimit;

    public BottomScanner(int[][] baseFrame, int[][] screenShot, int verticalLimit) {
        this.baseFrame = baseFrame;
        this.screenShot = screenShot;
        this.verticalLimit = verticalLimit;
    }

    @Override
    public boolean execute(boolean isRectChanged, Rect rect)
    {
        int index;
        int limit;
        int rover;
        index = rect.getY();
        limit = rect.getY() + rect.getW();
        while ((index < limit) && (rect.getX() + rect.getH() != verticalLimit)) {
            rover = rect.getX() + rect.getH() - 1;
            if (ComparisonUtils.notEqualsPixel(baseFrame[rover][index], screenShot[rover][index])) {
                isRectChanged = true;
                rect.setH(rect.getH() + 1);
                index = rect.getY();
                continue;
            }

            index = index + 1;
        }
        return isRectChanged;
    }
}
