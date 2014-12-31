package com.memtrip.picsy.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.provider.MediaStore;

import java.io.IOException;

public class BitmapUtils {
    /**
     * Crop the bitmap using the provided arguments
     * @param	bitmap	The bitmap to modify
     * @return	The new bitmap with the cropping applied
     */
    public static Bitmap cropBitmap(Bitmap bitmap, int rotation, boolean flipScale) {
        Matrix matrix = new Matrix();
        if (flipScale)
            matrix.setScale(-1, 1);

        if (rotation != -1)
            matrix.setRotate(rotation);

        Bitmap newBitmap = Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.getWidth(),
            bitmap.getHeight(),
            matrix,
            true
        );

        return newBitmap;
    }

    public static int getManufactuerSpecificRotation(int deviceType) {
        int rotation = -1;

        if (android.os.Build.MANUFACTURER.contains("samsung")) {
            rotation = 90;
        }

        return rotation;
    }

    /**
     * Calculate the rotation required for the image to be converted into portrait
     * @param	title	The title of the file
     * @param   context The context to get the content resolver
     * @return	The necessary rotation of the image
     */
    public static int necessaryPortraitRotation(String filePath, String title, Context context) {
        int rotation =-1;

        Cursor mediaCursor = context.getContentResolver().query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
            MediaStore.MediaColumns.TITLE + "=?",
            new String[] { title },
            null
        );

        if (mediaCursor != null && mediaCursor.getCount() != 0) {
            while (mediaCursor.moveToNext()){
                rotation = mediaCursor.getInt(0);
                break;
            }
        } else {
            rotation = getExifOrientationAttribute(filePath);
        }

        return rotation;
    }

    /**
     * Calculate the rotation required for the image to be converted into portrait
     * @param   filePath    The file path
     * @return  The necessary rotation of the image
     */
    private static int getExifOrientationAttribute(String filePath) {
        int rotate = 0;
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            );

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) { }

        return rotate;
    }
}
