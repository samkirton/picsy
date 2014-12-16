package com.memtrip.picsy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;

import com.memtrip.picsy.camera.CameraProvider;
import com.memtrip.picsy.camera.CameraHolder;
import com.memtrip.picsy.view.ControlView;
import com.memtrip.picsy.view.PreviewView;

/**
 * Demo usage
 */
public class Start extends Activity implements CameraHolder.OnPhotoCaptured {
    private PreviewView uiPreviewView;
    private ControlView uiControlView;

    private CameraHolder mCameraHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
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
        Intent intent = new Intent(this,Start.class);
        startActivity(intent);
        // return the intent
    }
}