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

        Bitmap newBitmap = Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.getWidth(),
            bitmap.getWidth(),
            matrix,
            true
        );

        return newBitmap;
    }
}
