package com.rybak.tool.similarity.comparison.finder;


import com.rybak.tool.similarity.comparison.Rect;

public class ScannerProcessor {


    private Scanner[] scanners;

    public void setScanners(Scanner[] scanners) {
        this.scanners = scanners;
    }

    public void process(Rect rect)
    {
        boolean isRectChanged = true;
        while (isRectChanged) {
            isRectChanged = false;

            for (Scanner scanner : scanners) {
                isRectChanged = scanner.execute(isRectChanged, rect);
            }
        }

    }

   // public void setScanner(ScanningRectangle scanner) {
   //     this.scanner = scanner;
    //}

//    public final void handleRequest() {
//
//        while (isRectChanged) {
//            isRectChanged = false;
//            index = 0;
//
//
//            isRectChanged = scanningOfLeftSideBoundaryOfRectangle(baseFrame, screenShot, yOffset, isRectChanged, rect);
//            isRectChanged = scanningOfBottomBoundaryOfRectangle(baseFrame, screenShot, isRectChanged, rect, verticalLimit);
//            isRectChanged = scanningOfRightSideBoundaryOfRectangle(baseFrame, screenShot, isRectChanged, rect, horizontalLimit);
//
//        } // while(isRectChanged)
//
//
//        if (isRectChanged) {
//            // пусть сделает это!
//            handle();
//        } else {
//            // если есть еще обработчик
//            if (successor != null) {
//                // отдаем запрос ему
//                successor.handleRequest(request);
//            }
//        }
//    }
//
//    protected abstract void handle();



}
