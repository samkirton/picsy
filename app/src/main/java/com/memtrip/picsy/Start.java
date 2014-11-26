package com.memtrip.picsy;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;

import com.memtrip.picsy.camera.CameraProvider;
import com.memtrip.picsy.camera.CameraHolder;
import com.memtrip.picsy.view.ControlView;
import com.memtrip.picsy.view.PreviewView;

/**
 * Demo camera activity
 */
public class Start extends Activity  {
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
}