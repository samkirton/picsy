package com.memtrip.picsy.camera.thread;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

import com.memtrip.picsy.camera.CameraHolder;
import com.memtrip.picsy.camera.CameraProvider;

/**
 * The thread retrieves a new instance of the device camera
 */
public class CameraStartThread extends Thread {
    private Handler mCameraHolderHandler;
    private CameraProvider mCameraProvider;
    private int mCameraType;

    public CameraStartThread(Handler handler, CameraProvider cameraProvider, int cameraType) {
        mCameraHolderHandler = handler;
        mCameraProvider = cameraProvider;
        mCameraType = cameraType;
    }

    @Override
    public void run() {
        Camera camera = mCameraProvider.getCamera(mCameraType);

        Message message = new Message();
        message.what = CameraHolder.CAMERA_OPEN;
        message.obj = new Response(camera,mCameraType);
        mCameraHolderHandler.sendMessage(message);
    }

    public class Response {
        private Camera camera;
        private int cameraType;

        public Response(Camera camera, int cameraType) {
            this.camera = camera;
            this.cameraType = cameraType;
        }

        public Camera getCamera() {
            return camera;
        }

        public int getCameraType() {
            return cameraType;
        }
    }
}