package com.memtrip.picsy.camera.thread;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

import com.memtrip.picsy.camera.CameraHolder;
import com.memtrip.picsy.camera.CameraProvider;
import com.memtrip.picsy.utils.CapturePhotoUtils;

/**
 * The thread processes the data captured by the camera
 */
public class CameraCaptureThread extends Thread {
    private Handler mCameraHolderHandler;
    private byte[] mPhotoData;
    private Context mContext;

    public CameraCaptureThread(Handler handler, byte[] photoData, Context context) {
        mCameraHolderHandler = handler;
        mPhotoData = photoData;
        mContext = context;
    }

    @Override
    public void run() {
        Bitmap bitmap = BitmapFactory.decodeByteArray(
            mPhotoData,
            0,
            mPhotoData.length
        );

        String uri = CapturePhotoUtils.insertImage(
            mContext.getContentResolver(),
            bitmap,
            "proffer",
            "description"
        );

        bitmap.recycle();

        Message msg = new Message();
        msg.what = CameraHolder.CAMERA_PHOTO_CAPTURE;
        msg.obj = uri;
        mCameraHolderHandler.sendMessage(msg);
    }
}