package com.memtrip.picsy.utils;

import android.app.Activity;
import android.content.Context;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.view.Surface;

import java.util.List;

/**
 * Utility classes for managing the camera
 */
public class CameraUtils {
    public static final String FLASH_ON = "FLASH_ON";
    public static final String FLASH_AUTO = "FLASH_AUTO";
    public static final String FLASH_OFF = "FLASH_OFF";

    /**
     * Is the camera auto focus feature supported?
     * @param	params	Camera parameters
     * @param	context	Application context
     * @return	Is the camera auto focus feature supported?
     */
    public static boolean isAutoFocusContinousPictureSupported(Camera.Parameters params, Context context) {
        boolean isAutoFocusContinousPictureSupported = false;
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
            isAutoFocusContinousPictureSupported = isCameraFeatureSupported(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
                    params.getSupportedFocusModes()
            );
        }

        return isAutoFocusContinousPictureSupported;
    }

    /**
     * Set the flash type of the camera instance
     * @param   camera  The camera instance
     * @param   cameraFlashType The flash type
     */
    public static void setCameraFlashType(Camera camera, String cameraFlashType) {
        if (cameraFlashType.equals(FLASH_ON)) {
            Camera.Parameters param = camera.getParameters();
            param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            camera.setParameters(param);
        } else if (cameraFlashType.equals(FLASH_OFF)) {
            Camera.Parameters param = camera.getParameters();
            param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(param);
        } else if (cameraFlashType.equals(FLASH_AUTO)) {
            Camera.Parameters param = camera.getParameters();
            param.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            camera.setParameters(param);
        }
    }

    /**
     * Get the camera rotation
     * @param   context The context to get the activity instance for
     * @return  The rotation degrees required
     */
    public static int getCameraRotation(int cameraType, Context context) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraType, info);
        int rotation = ((Activity)context).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
            case Surface.ROTATION_90: degrees = 90; break; //Landscape left
            case Surface.ROTATION_180: degrees = 180; break;//Upside down
            case Surface.ROTATION_270: degrees = 270; break;//Landscape right
        }
        return (info.orientation - degrees + 360) % 360;
    }

    /**
     * @param   value
     * @param	 supported	A list of supporte camera features
     * @return	Is the vamera feature supported?
     */
    private static boolean isCameraFeatureSupported(String value, List<String> supported) {
        return supported == null ? false : supported.contains(value);
    }
}