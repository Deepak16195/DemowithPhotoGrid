package com.PhotoGridMaker.Softsimplicity.slicer;

/**
 * Created by Deepak on 2017/12/12.
 */

public class FourPicBitmapSlicer extends BitmapSlicer {

    @Override
    protected int getHorizontalPicNumber() {
        return 2;
    }

    @Override
    protected int getVerticalPicNumber() {
        return 2;
    }
}
