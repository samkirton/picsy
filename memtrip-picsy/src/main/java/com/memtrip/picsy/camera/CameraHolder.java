package com.memtrip.picsy.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.hardware.Camera.PictureCallback;

import com.memtrip.picsy.camera.thread.CameraCaptureThread;
import com.memtrip.picsy.camera.thread.CameraReverseThread;
import com.memtrip.picsy.camera.thread.CameraStartThread;
import com.memtrip.picsy.utils.CameraUtils;
import com.memtrip.picsy.utils.DisplayUtils;
import com.memtrip.picsy.view.PreviewView;

/**
 * Manage the camera instance
 */
public class CameraHolder implements PictureCallback {
    private Context mContext;
    private Handler mCameraHandler;
    private Camera mCamera;
    private CameraProvider mCameraProvider;
    private PreviewView uiPreviewView;
    private Display mDisplay;
    private int mCurrentCamera;
    private OnPhotoCaptured mOnPhotoCaptured;

    private static final int PICTURE_QUALITY = 100;
    public static final int CAMERA_OPEN = 0x1;
    public static final int CAMERA_PHOTO_CAPTURE = 0x2;
    public static final int CAMERA_REVERSE = 0x3;

    public interface OnPhotoCaptured {
        public void onPhotoCaptured(String uri);
    }

    public void setOnPhotoCaptured(OnPhotoCaptured newVal) {
        mOnPhotoCaptured = newVal;
    }

    public CameraHolder(Context context, CameraProvider cameraProvider, PreviewView previewView, Display display) {
        mContext = context;
        mCameraHandler = new CameraHandler();
        mCameraProvider = cameraProvider;
        uiPreviewView = previewView;
        mDisplay = display;
        mCurrentCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
    }

    /**
     * Handle incoming thread messages
     */
    private class CameraHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CAMERA_OPEN:
                    CameraStartThread.Response openResponse = (CameraStartThread.Response)msg.obj;
                    linkCameraWithSurfaceView(openResponse.getCamera(), uiPreviewView, mDisplay, openResponse.getCameraType());
                    break;

                case CAMERA_REVERSE:
                    CameraReverseThread.Response reverseResponse = (CameraReverseThread.Response)msg.obj;
                    start(reverseResponse.getCameraType());
                    break;

                case CAMERA_PHOTO_CAPTURE:
                    mOnPhotoCaptured.onPhotoCaptured((String)msg.obj);
                    break;
            }
        }
    }

    /**
     * Link the hardware camera with the cameraPreviewView to display the output
     * @param   camera  The camera instance
     * @param   previewView   The view to show the camera preview
     * @param   display The device display
     * @param   cameraType  The hardware camera type being shown
     */
    private void linkCameraWithSurfaceView(Camera camera, PreviewView previewView, Display display, int cameraType) {
        if (camera != null) {
            mCamera = camera;
            mCameraProvider.setDefaultParameters(camera, cameraType, PICTURE_QUALITY, mContext);
            mCameraProvider.setOrientation(display, camera, cameraType);
            previewView.start(camera, mCameraProvider);
            mCurrentCamera = cameraType;
        }
    }

    /**
     * Capture a photo
     */
    public void capture() {
        mCamera.takePicture(null, null, this);
    }

    /**
     * Start the camera with the provided cameraType
     * @param   cameraType  The cameraType to start
     */
    public void start(int cameraType) {
        if (mCameraProvider.doesHardwareExists(mContext)) {
            new CameraStartThread(mCameraHandler, mCameraProvider, cameraType).start();
        } else {
            // TODO: display a "no camera" error message on the surface view
        }
    }

    /**
     * Start the camera with the default hardware device
     */
    public void start() {
        start(mCurrentCamera);
    }

    /**
     * Reverse the default hardware device
     */
    public void reverse() {
        if (mCameraProvider.doesHardwareExists(mContext)) {
            mCurrentCamera= (mCurrentCamera == Camera.CameraInfo.CAMERA_FACING_BACK) ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
            new CameraReverseThread(mCameraHandler,mCameraProvider,mCamera,mCurrentCamera).start();
        }
    }

    /**
     * Stop the hardware device
     */
    public void stop() {
        if (mCamera != null) {
            mCameraProvider.stopPreview(mCamera);
            mCameraProvider.closeCamera(mCamera);
            uiPreviewView.removeCallback();
        }
    }

    /**
     * Set the camera flash type
     * @param   flashType   The flash type of the camera
     */
    public void setFlashType(String flashType) {
        CameraUtils.setCameraFlashType(mCamera, flashType);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        int dimen = DisplayUtils.getDisplayWidth(mDisplay);
        new CameraCaptureThread(mCameraHandler,data,mCurrentCamera,dimen,mContext).start();
    }
}