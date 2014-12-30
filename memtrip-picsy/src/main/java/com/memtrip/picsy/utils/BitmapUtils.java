package com.memtrip.picsy.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

public class BitmapUtils {
    /**
     * Crop the bitmap using the provided arguments
     * @param	bitmap	The bitmap to modify
     * @return	The new bitmap with the cropping applied
     */
    public static Bitmap cropBitmap(Bitmap bitmap, boolean flipScale) {
        Matrix matrix = new Matrix();
        if (flipScale)
            matrix.setScale(-1, 1);

        int dimen = 0;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            dimen = bitmap.getHeight();
        } else {
            dimen = bitmap.getWidth();
        }

        Bitmap newBitmap = Bitmap.createBitmap(
            bitmap,
            0,
            0,
            dimen,
            dimen,
            matrix,
            true
        );

        return newBitmap;
    }
}
