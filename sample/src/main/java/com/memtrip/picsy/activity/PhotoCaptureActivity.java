package com.memtrip.picsy.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;

import com.memtrip.picsy.R;
import com.memtrip.picsy.camera.CameraHolder;
import com.memtrip.picsy.camera.CameraProvider;
import com.memtrip.picsy.view.ControlView;
import com.memtrip.picsy.view.PreviewView;

public class PhotoCaptureActivity extends Activity implements CameraHolder.OnPhotoCaptured {
    private PreviewView uiPreviewView;
    private ControlView uiControlView;

    private CameraHolder mCameraHolder;

    public static final int RESULT_CODE = 0x1;
    public static final String URI = "URI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_capture);
        getActionBar().hide();
        uiPreviewView = (PreviewView)findViewById(R.id.start_camera);
        uiControlView = (ControlView)findViewById(R.id.start_control);

        mCameraHolder = new CameraHolder(this, new CameraProvider(), uiPreviewView,getWindowManager().getDefaultDisplay());
        mCameraHolder.setOnPhotoCaptured(this);
        mCameraHolder.start(Camera.CameraInfo.CAMERA_FACING_BACK);
        uiControlView.setCameraHolder(mCameraHolder);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraHolder.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraHolder.start();
    }

    @Override
    public void onPhotoCaptured(Bitmap bitmap, String uri) {
        Intent intent = new Intent();
        intent.putExtra(URI,uri);
        setResult(RESULT_CODE,intent);
        finish();
    }
}