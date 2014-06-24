package com.picsy.utils;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

/**
 * @author samuelkirton
 */
public class BitmapUtils {

	/**
	 * Crop the bitmap using the provided arguments
	 * @param	bitmap	The bitmap to modify
	 * @return	The new bitmap with the cropping applied
	 */
	public static Bitmap cropBitmap(Bitmap bitmap, int yCrop, int heightCrop, int rotation) {
		Matrix matrix = new Matrix();
		matrix.postRotate(rotation);
		
		Bitmap newBitmap = Bitmap.createBitmap(
			bitmap, 
			heightCrop, 
			0, 
			bitmap.getHeight(),
			bitmap.getHeight(), 
			matrix, 
			true
		);
		
		return newBitmap;
	}
	
	/**
	 * Calculate the rotation required for the image to be converted into portrait
	 * @param	filePath	The file path
	 * @return	The necessary rotation of the image
	 */
	public static int necessaryPortraitRotation(String filePath) {
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