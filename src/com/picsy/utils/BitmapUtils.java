package com.picsy.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * @author samuelkirton
 */
public class BitmapUtils {

	/**
	 * Crop the bitmap using the provided arguments
	 * @param	bitmap	The bitmap to modify
	 * @return	The new bitmap with the cropping applied
	 */
	public static Bitmap cropBitmap(Bitmap bitmap, int yCrop, int heightCrop) {
		Matrix matrix = new Matrix();
		matrix.postRotate(-90);
		
		bitmap = Bitmap.createBitmap(
			bitmap, 
			heightCrop, 
			0, 
			bitmap.getWidth()-heightCrop-yCrop,
			bitmap.getHeight(), 
			matrix, 
			true
		);
		
		return bitmap;
	}
}
