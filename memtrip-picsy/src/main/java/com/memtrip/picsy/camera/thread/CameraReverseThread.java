package com.memtrip.picsy.camera.thread;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

import com.memtrip.picsy.camera.CameraHolder;
import com.memtrip.picsy.camera.CameraProvider;

/**
 * The thread closes the camera ready for reverse
 */
public class CameraReverseThread extends Thread {
    private Handler mCameraHolderHandler;
    private CameraProvider mCameraProvider;
    private Camera mCamera;
    private int mCameraType;

    public CameraReverseThread(Handler handler, CameraProvider cameraProvider, Camera camera, int cameraType) {
        mCameraHolderHandler = handler;
        mCameraProvider = cameraProvider;
        mCamera = camera;
        mCameraType = cameraType;
    }

    @Override
    public void run() {
        mCameraProvider.stopPreview(mCamera);
        mCameraProvider.closeCamera(mCamera);

        Message message = new Message();
        message.what = CameraHolder.CAMERA_REVERSE;
        message.obj = new Response(mCameraType);
        mCameraHolderHandler.sendMessage(message);
    }

    public class Response {
        private int cameraType;

        public Response(int cameraType) {
            this.cameraType = cameraType;
        }

        public int getCameraType() {
            return cameraType;
        }
    }
}