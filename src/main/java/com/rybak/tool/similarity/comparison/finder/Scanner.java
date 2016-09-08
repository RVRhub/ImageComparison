package com.rybak.tool.similarity.comparison.finder;


import com.rybak.tool.similarity.comparison.Rect;

public interface Scanner {
    boolean execute(boolean isRectChanged, Rect rect);
}
