package com.memtrip.picsy.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.memtrip.picsy.utils.CameraUtils;

import java.io.IOException;
import java.util.List;

/**
 * Camera instance methods
 */
public class CameraProvider {
    private static final int OPTIMAL_WIDTH = 1280;

    /**
     * @param   context The context used to retrieve the package manager
     * @return  Does the device have a camera?
     */
    public boolean doesHardwareExists(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * @return  A hardware camera instance
     */
    public Camera getCamera(int cameraId) {
        Camera camera = null;

        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) { }

        return camera;
    }

    /**
     * Set orientation
     * @param   display The orientation of the device
     * @param   camera  The camera instance
     * @param   cameraId    Which camera?
     */
    public void setOrientation(Display display, Camera camera, int cameraId) {
        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = display.getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);
    }

    /**
     * Start the camera preview on the sureface holder
     * @param   holder  The surface holder where the camera preview will be displayed
     * @param   camera  The camera instance
     * @return  Was the preview started?
     */
    public boolean startPreview(SurfaceHolder holder, Camera camera) {
        boolean started = false;

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            started = true;
        } catch (IOException e) { }

        return started;
    }

    /**
     * Stop the camera preview
     * @param   camera  The camera instance
     */
    public void stopPreview(Camera camera) {
        try {
            camera.stopPreview();
        } catch (Exception e){ }
    }

    /**
     * Release the camera memory
     * @param   camera  The camera instance
     */
    public void closeCamera(Camera camera) {
        if (camera != null) {
            camera.release();
        }
    }

    /**
     * Set default camera parameters
     * @param   camera  The camera instance
     * @param   pictureQuality  The quality of the camera photo capture
     * @param   context The application context
     */
    public void setDefaultParameters(Camera camera, int cameraType, int pictureQuality, Context context) {
        Camera.Parameters cameraParameters = camera.getParameters();;
        cameraParameters.setPictureFormat(ImageFormat.JPEG);
        cameraParameters.setJpegQuality(pictureQuality);

        // pick the camera resolution
        List<Size> sizes = cameraParameters.getSupportedPictureSizes();
        Camera.Size size = sizes.get(0);
        for (int i = 0; i < sizes.size(); i++) {
            // choose optimal first
            if (sizes.get(i).width == OPTIMAL_WIDTH) {
                size = sizes.get(i);
                break;
            // choose the highest resolution second
            } else if (sizes.get(i).width > size.width) {
                size = sizes.get(i);
            }
        }
        cameraParameters.setPictureSize(size.width, size.height);

        // update the rotation of the image
        Camera.Parameters params = camera.getParameters();
        params.setRotation(CameraUtils.getCameraRotation(cameraType,context));
        camera.setParameters(params);

        // enabled continuous focus if it is available
        if (CameraUtils.isAutoFocusContinousPictureSupported(cameraParameters, context)) {
            cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else {
            cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        camera.setParameters(cameraParameters);
    }
}
