package com.picsy.utils;

import java.util.List;

import com.picsy.activity.CameraActivity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.view.Display;
import android.view.Surface;

/**
 * Camera helper methods
 * @author samuelkirton
 */
public class CameraUtils {
	
	/**
	 * Is the camera auto focus feature supported?
	 * @param	params	Camera parameters
	 * @param	context	Application context
	 * @return	Is the camera auto focus feature supported?
	 */
	public static boolean isAutoFocusContinousPictureSupported(Parameters params, Context context) {
		boolean isAutoFocusContinousPictureSupported = false;
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
			isAutoFocusContinousPictureSupported = isCameraFeatureSupported(
				Parameters.FOCUS_MODE_CONTINUOUS_PICTURE, 
				params.getSupportedFocusModes()
			);
		}
		
		return isAutoFocusContinousPictureSupported;
	}
	
    /**
     * Set the display orientation of the provided camera object
     * @param	camera	Camera object
     */
    public static void setDisplayOrientation(Camera camera, int cameraId, Context context) {
    	int rotation = ((Activity)context).getWindowManager().getDefaultDisplay().getRotation();
    	CameraInfo cameraInfo = new CameraInfo();
    	Camera.getCameraInfo(cameraId, cameraInfo);

        int degrees = 0;
        switch (rotation) {
			case Surface.ROTATION_0: degrees = 0; break;
			case Surface.ROTATION_90: degrees = 90; break;
			case Surface.ROTATION_180: degrees = 180; break;
			case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
		if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (cameraInfo.orientation + degrees) % 360;
			result = (360 - result) % 360;
		} else {
			result = (cameraInfo.orientation - degrees + 360) % 360;
		}
        
        camera.setDisplayOrientation(result);
    }
    
    /**
     * Update the camera flash mode
     * @param	cameraFlashType	The camera flash type
     */
    public static void setCameraFlashType(Camera camera, String cameraFlashType) {
    	if (cameraFlashType.equals(CameraActivity.PARAM_FLASH_ON)) {
    		Camera.Parameters param = camera.getParameters();
    		param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
    		camera.setParameters(param);
    	} else if (cameraFlashType.equals(CameraActivity.PARAM_FLASH_OFF)) {
    		Camera.Parameters param = camera.getParameters();
    		param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
    		camera.setParameters(param);
    	} else if (cameraFlashType.equals(CameraActivity.PARAM_FLASH_AUTO)) {
    		Camera.Parameters param = camera.getParameters();
    		param.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
    		camera.setParameters(param);
    	}
    }
    
    /**
     * Calculate the optimal size
     * @param 	context	A context that is an instance of activity	
     * @param 	sizes	The possible camera sizes
     * @param 	targetRatio	The ratio of the camera
     * @return	The optimal size for the camera preview
     */
	public static Size getOptimalPreviewSize(Context context, 
			List<Size> sizes, 
			double targetRatio) {
		
	    // Use a very small tolerance because we want an exact match.
	    final double ASPECT_TOLERANCE = 0.001;
	    if (sizes == null) return null;
	
	    Size optimalSize = null;
	    double minDiff = Double.MAX_VALUE;
	
	    // Because of bugs of overlay and layout, we sometimes will try to
	    // layout the viewfinder in the portrait orientation and thus get the
	    // wrong size of preview surface. When we change the preview size, the
	    // new overlay will be created before the old one closed, which causes
	    // an exception. For now, just get the screen size.
	    Point point = getDefaultDisplaySize(context);
	    int targetHeight = Math.min(point.x, point.y);
	    // Try to find an size match aspect ratio and size
	    for (Size size : sizes) {
	        double ratio = (double) size.width / size.height;
	        if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
	        if (Math.abs(size.height - targetHeight) < minDiff) {
	            optimalSize = size;
	            minDiff = Math.abs(size.height - targetHeight);
	        }
	    }

	    if (optimalSize == null) {
	        minDiff = Double.MAX_VALUE;
	        for (Size size : sizes) {
	            if (Math.abs(size.height - targetHeight) < minDiff) {
	                optimalSize = size;
	                minDiff = Math.abs(size.height - targetHeight);
	            }
	        }
	    }
	    
	    return optimalSize;
	}
	
	/**
	 * Get the size of the screen display
	 * @param	context	A context that is an instance of activity
	 * @return	The size of the screen display
	 */
	@SuppressWarnings("deprecation")
	public static Point getDefaultDisplaySize(Context context) {
		if (!(context instanceof Activity))
			throw new IllegalStateException("The context argument must be an instance of activity");
		
		Point size = new Point();
		
		Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			display.getSize(size);
		} else {
			size.set(display.getWidth(), display.getHeight());
		}
		
		return size;
	}
	
	/**
	 * @param value
	 * @param	 supported	A list of supporte camera features
	 * @return	Is the vamera feature supported?
	 */
    private static boolean isCameraFeatureSupported(String value, List<String> supported) {
    	return supported == null ? false : supported.contains(value);
    }
}
