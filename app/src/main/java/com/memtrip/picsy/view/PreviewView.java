package com.memtrip.picsy.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.memtrip.picsy.camera.CameraProvider;

/**
 * The camera preview display sureface view
 */
public class PreviewView extends SurfaceView implements SurfaceHolder.Callback {
    private Camera mCamera;
    private CameraProvider mCameraProvider;
    private int mCameraType;
    private SurfaceHolder mHolder;

    public PreviewView(Context context) {
        super(context);
        init();
    }

    public PreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PreviewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public void start(Camera camera, int cameraType, CameraProvider cameraProvider) {
        mCamera = camera;
        mCameraType = cameraType;
        mCameraProvider = cameraProvider;
        mCameraProvider.startPreview(getHolder(), mCamera);
        requestLayout();
    }

    /**
     * Remove the surface view callback
     */
    public void removeCallback() {
        mHolder.removeCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCamera != null) {
            mCameraProvider.startPreview(holder, mCamera);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            if (mHolder.getSurface() == null) {
                return;
            }

            mCameraProvider.stopPreview(mCamera);
            mCameraProvider.startPreview(holder, mCamera);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mCamera != null) {
            int layoutWidth = MeasureSpec.getSize(widthMeasureSpec);
            int width = mCamera.getParameters().getPreviewSize().height;
            int height = mCamera.getParameters().getPreviewSize().width;

            if (width < layoutWidth) {
                int difference = layoutWidth - width;
                width = layoutWidth;
                height += difference;
            } else if (width > layoutWidth) {
                int difference = width - layoutWidth;
                width = layoutWidth;
                height -= difference;
            }

            setMeasuredDimension(width, height);
        }
    }
}