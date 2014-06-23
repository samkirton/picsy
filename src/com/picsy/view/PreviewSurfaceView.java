package com.picsy.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author samuelkirton
 */
public class PreviewSurfaceView extends SurfaceView {
    private int mCameraWidth;
    private int mCameraHeight;
    
    @SuppressWarnings("deprecation")
	public PreviewSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) 
        	getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    public void init(int width, int height) {
    	mCameraWidth = width;
    	mCameraHeight = height;
    	requestLayout();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	setMeasuredDimension(mCameraWidth, mCameraHeight);
    }
}