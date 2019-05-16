package com.PhotoGridMaker.Softsimplicity.slicer;

import android.graphics.Bitmap;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deepak on 2017/12/12.
 */

public abstract class BitmapSlicer {

    private static final int PIC_BORDER_LEN = 667;
    private static final int PIC_DIVIDER_LEN = 24;

    private Bitmap srcBitmap;
    private BitmapSliceListener mListener;
    private int mAspectX;
    private int mAspectY;

    protected BitmapSlicer() {
        int hNum = getHorizontalPicNumber();
        int vNum = getVerticalPicNumber();
        mAspectX = PIC_BORDER_LEN * hNum + PIC_DIVIDER_LEN * (hNum - 1);
        mAspectY = PIC_BORDER_LEN * vNum + PIC_DIVIDER_LEN * (vNum - 1);
    }

    public final BitmapSlicer registerListener(BitmapSliceListener listener) {
        mListener = listener;
        return this;
    }

    public final BitmapSlicer setSrcBitmap(Bitmap srcBitmap) {
        this.srcBitmap = srcBitmap;
        return this;
    }

    @MainThread
    public final void slice() {
        Observable.just(srcBitmap)
                .map(new Function<Bitmap, List<Bitmap>>() {
                    @Override
                    public List<Bitmap> apply(Bitmap bitmap) throws Exception {
                        return doSlice(bitmap);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Bitmap>>() {
                    @Override
                    public void accept(List<Bitmap> bitmaps) throws Exception {
                        if (mListener != null) {
                            if (bitmaps != null) {
                                mListener.onSliceSuccess(srcBitmap, bitmaps);
                            } else {
                                mListener.onSliceFailed();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        if (mListener != null) {
                            mListener.onSliceFailed();
                        }
                    }
                });
    }

    /**
     * @return Number of horizontal pictures
     */
    protected abstract int getHorizontalPicNumber();

    /**
     * @return Number of portrait images
     */
    protected abstract int getVerticalPicNumber();

    @WorkerThread
    private List<Bitmap> doSlice(Bitmap srcBitmap) {
        int srcW = srcBitmap.getWidth();
        int srcH = srcBitmap.getHeight();
        int hNum = getHorizontalPicNumber();
        int vNum = getVerticalPicNumber();
        int picW = srcW * PIC_BORDER_LEN / mAspectX;
        int picH = srcH * PIC_BORDER_LEN / mAspectY;
        int dividerW = srcW * PIC_DIVIDER_LEN / mAspectX;
        int dividerH = srcH * PIC_DIVIDER_LEN / mAspectY;
        List<Bitmap> bitmaps = new ArrayList<>();
        for (int j = 0; j < vNum; j++) {
            for (int i = 0; i < hNum; i++) {
                Bitmap bitmap = Bitmap.createBitmap(srcBitmap, (picW + dividerW) * i, (picH + dividerH) * j, picW, picH);
                bitmaps.add(bitmap);
            }
        }
        return bitmaps;
    }

    public final int getAspectX() {
        return mAspectX;
    }

    public final int getAspectY() {
        return mAspectY;
    }

    public final int calculateOutputX(int srcW, int srcH) {
        switch (compareRate(srcW, srcH)) {
            case 0:// The same proportion
            case -1://The source image ratio is smaller than the target ratio
                return srcW;
            case 1: // Source image ratio is larger than target ratio
                return srcH * getAspectX() / getAspectY();
        }
        return 0;
    }

    public final int calculateOutputY(int srcW, int srcH) {
        switch (compareRate(srcW, srcH)) {
            case 0://The same proportion
            case 1://Source image ratio is larger than target ratio
                return srcH;
            case -1://The source image ratio is smaller than the target ratio
                return srcW * getAspectY() / getAspectX();
        }
        return 0;
    }

    private int compareRate(int srcW, int srcH) {
        //
        //Rate indicates the aspect ratio of the picture. The larger the Rate, the shorter the picture, the smaller the Rate, the thinner the picture.
        double desRate = getAspectX() * 1.0 / getAspectY();
        double srcRate = srcW * 1.0 / srcH;
        if (desRate < srcRate) {
            return 1;
        } else if (desRate > srcRate) {
            return -1;
        } else {
            return 0;
        }
    }


    public interface BitmapSliceListener {

        void onSliceSuccess(Bitmap srcBitmap, List<Bitmap> desBitmaps);

        void onSliceFailed();
    }
}
