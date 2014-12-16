package com.memtrip.picsy.camera.thread;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
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
    private Context mContext;

    public CameraCaptureThread(Handler handler, byte[] photoData, int cameraType, Context context) {
        mCameraHolderHandler = handler;
        mPhotoData = photoData;
        mCameraType = cameraType;
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
        bitmap = BitmapUtils.cropBitmap(bitmap,shouldFlip);

        String uri = CapturePhotoUtils.insertImage(
            mContext.getContentResolver(),
            bitmap,
            "proffer",
            "description"
        );

        bitmap.recycle();

        Message msg = new Message();
        msg.what = CameraHolder.CAMERA_PHOTO_CAPTURE;
        msg.obj = new Response(bitmap,uri);
        mCameraHolderHandler.sendMessage(msg);
    }

    public class Response {
        private String uri;
        private Bitmap bitmap;

        public Response(Bitmap bitmap,String uri) {
            this.bitmap = bitmap;
            this.uri = uri;
        }

        public String getUri() {
            return uri;
        }

        public Bitmap getBitmap() { return bitmap; }
    }
}