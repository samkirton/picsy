package com.memtrip.picsy.utils;

import android.content.Context;

import android.content.pm.PackageManager;
import android.hardware.Camera;

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
     * @param   value
     * @param	 supported	A list of supporte camera features
     * @return	Is the vamera feature supported?
     */
    private static boolean isCameraFeatureSupported(String value, List<String> supported) {
        return supported == null ? false : supported.contains(value);
    }
}