package com.memtrip.picsy.camera.thread;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.memtrip.picsy.camera.CameraHolder;
import com.memtrip.picsy.utils.BitmapUtils;
import com.memtrip.picsy.utils.CapturePhotoUtils;

/**
 * The thread processes the data captured by the camera
 */
public class CameraCaptureThread extends Thread {
    private Handler mCameraHolderHandler;
    private byte[] mPhotoData;
    private int mCameraType;
    private int mDimen;
    private Context mContext;

    public CameraCaptureThread(Handler handler, byte[] photoData, int cameraType, int dimen, Context context) {
        mCameraHolderHandler = handler;
        mPhotoData = photoData;
        mCameraType = cameraType;
        mDimen = dimen;
        mContext = context;
    }

    @Override
    public void run() {
        Bitmap bitmap = BitmapFactory.decodeByteArray(
            mPhotoData,
            0,
            mPhotoData.length
        );

        boolean shouldFlip = (mCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT);
        int rotation = BitmapUtils.getManufactuerSpecificRotation(0);
        bitmap = BitmapUtils.cropBitmap(bitmap, rotation, shouldFlip);

        String title = "random";
        String uri = CapturePhotoUtils.insertImage(
            mContext.getContentResolver(),
            bitmap,
            title,
            "description"
        );

        bitmap.recycle();

        Message msg = new Message();
        msg.what = CameraHolder.CAMERA_PHOTO_CAPTURE;
        msg.obj = uri;
        mCameraHolderHandler.sendMessage(msg);
    }
}