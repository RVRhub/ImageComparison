package com.rybak.tool.similarity.comparison.finder;

import com.rybak.tool.similarity.comparison.ComparisonUtils;
import com.rybak.tool.similarity.comparison.Rect;

public class LeftSideScanner implements Scanner {
    private int[][] baseFrame;
    private int[][] screenShot;
    private Rect rect;

    public LeftSideScanner(int[][] baseFrame, int[][] screenShot) {
        this.baseFrame = baseFrame;
        this.screenShot = screenShot;
        this.rect = rect;
    }

    @Override
    public boolean execute(boolean isRectChanged, Rect rect) {
        int index;
        int limit;
        index = rect.getX();
        limit = rect.getX() + rect.getH();
        while (index < limit && rect.getY() != 0) {
            if (ComparisonUtils.notEqualsPixel(baseFrame[index][rect.getY()], screenShot[index][rect.getY()])) {
                isRectChanged = true;
                rect.setY(rect.getY() - 2);
                rect.setW(rect.getW() + 2);
                index = rect.getX();
                continue;
            }
            index = index + 2;
        }
        return isRectChanged;
    }
}
