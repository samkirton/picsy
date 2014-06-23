package com.picsy.activity;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.picsy.R;
import com.picsy.provider.CameraProvider;
import com.picsy.utils.BitmapUtils;
import com.picsy.utils.CameraUtils;
import com.picsy.utils.CapturePhotoUtils;
import com.picsy.view.CameraGridView;
import com.picsy.view.PreviewSurfaceView;

/**
 * Capture photo
 * @author samuelkirton
 */
public abstract class BaseCameraActivity extends Activity implements 
		PictureCallback, 
		AnimationListener,
		OnGlobalLayoutListener,
		SurfaceHolder.Callback {
	
	private RelativeLayout uiActivityParentLayout;
	private PreviewSurfaceView uiCameraPreviewSurfaceView;
	private CameraGridView uiCameraGridView;
	private FrameLayout uiCapturePhotoHeaderLayout;
	private FrameLayout uiCaptureAnimationLayout;
	private FrameLayout uiControlsLayout;
	private int mCameraState = CAMERA_STOPPED;
	private int mCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
	private String mCameraResponseType;
	private Camera mCamera;
	private Camera.Parameters mCameraParameters;
	private CameraStartThread mCameraStartThread;
	private CameraHandler mCameraHandler;
	private boolean mIsGridShowing;
	private int mPhotoYCrop;
	private int mPhotoHeightCrop;
	private int mPhotoHeight;
	private byte[] mPhotoData;
	private Animation mCaptureAnimation;

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final String PARAM_FLASH_ON = "PARAM_FLASH_ON";
	public static final String PARAM_FLASH_AUTO = "PARAM_FLASH_AUTO";
	public static final String PARAM_FLASH_OFF = "PARAM_FLASH_OFF";
	
	public static final String EXTRA_CAMERA_RESPONSE_TYPE = "EXTRA_CAMERA_RESPONSE_TYPE";
	public static final String EXTRA_PHOTO_BYTE_DATA = "EXTRA_PHOTO_BYTE_DATA";
	public static final int EXTRA_CAMERA_RESULT_CODE = 0x41548387;
	/**
	 * The camera should be closed and the data sent back to the consuming activity
	 * as soon as the photo has been taken
	 */
	public static final String EXTRA_CAMERA_IMMEDIATE = "EXTRA_CAMERA_IMMEDIATE";
	/**
	 * When the photo has been taken the EditActivity should be opened with the photo
	 */
	public static final String EXTRA_CAMERA_RESIZE = "EXTRA_CAMERA_RESIZE";
	
	private static final int CAMERA_STOPPED = 0x1;
	private static final int CAMERA_OPEN_SUCCESS = 0x2;
	private static final int CAMERA_OPEN_FAIL = 0x3;
	private static final int CAMERA_PHOTO_CAPTURE = 0x4;
	
	/**
	 * Stop the UI being blocked when the camera starts in onCreate and onResume
	 */
	private class CameraStartThread extends Thread {
		@Override
		public void run() {
			mCamera = CameraProvider.getCamera(mCameraType);
			mCameraHandler.sendEmptyMessage(CAMERA_OPEN_SUCCESS);
		}
	}
	
	/**
	 * Speed up the resizing image by running it in a seperate process
	 */
	private class ResizeImageThread extends Thread {
		@Override
		public void run() {
			Bitmap bitmap = BitmapFactory.decodeByteArray(
				mPhotoData, 
				0, 
				mPhotoData.length
			);
			
			String uri = CapturePhotoUtils.insertImage(
				getContentResolver(), 
				BitmapUtils.cropBitmap(bitmap, mPhotoYCrop, mPhotoHeightCrop), 
				"proffer", 
				"description"
			);
			
			Message msg = new Message();
			msg.what = CAMERA_PHOTO_CAPTURE;
			msg.obj = uri;
			mCameraHandler.sendMessage(msg);
		}
	}
	
	/**
	 * Post camera thread messages back to the ui thread
	 */
	private class CameraHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case CAMERA_OPEN_SUCCESS:
					init();
					break;
					
				case CAMERA_OPEN_FAIL:
					break;
					
				case CAMERA_PHOTO_CAPTURE:
					photoCaptured((String)msg.obj);
					break;
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCameraResponseType = getIntent().getStringExtra(EXTRA_CAMERA_RESPONSE_TYPE);
		
		if (mCameraResponseType == null)
			throw new IllegalStateException("The CameraActivity requires a value for EXTRA_CAMERA_RESPONSE_TYPE");
		
		mCameraHandler = new CameraHandler();
		
		if (checkCameraHardwareExists(this)) {
			// set view references
			uiActivityParentLayout = getActivityParentLayout();
			uiCameraPreviewSurfaceView = getPreviewSurfaceView();
			uiCapturePhotoHeaderLayout = getHeaderFrameLayout();
			uiControlsLayout = getControlsFrameLayout();
			
			uiCaptureAnimationLayout = new FrameLayout(this);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, 
				FrameLayout.LayoutParams.MATCH_PARENT
			);
			uiCaptureAnimationLayout.setLayoutParams(params);
			
			uiCapturePhotoHeaderLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);
			uiCameraPreviewSurfaceView.getHolder().addCallback(this);
			
			// load animations
			mCaptureAnimation = AnimationUtils.loadAnimation(this, R.anim.camera_photo_taken);
		} else {
			// TODO: Handle camera error
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mCameraState == CAMERA_STOPPED && mCameraStartThread == null) {
			mCameraStartThread = new CameraStartThread();
			mCameraStartThread.start();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		stopPreview();
		closeCamera();
	}
	
	/**
	 * Attach the camera holder to the surface view
	 */
	private void init() {
		mCameraStartThread = null;
		updateCameraParameters();
		
		if (mCameraType == Camera.CameraInfo.CAMERA_FACING_BACK) {
			Size size = mCameraParameters.getPreviewSize();
			// rotate the preview for the front facing camera
			uiCameraPreviewSurfaceView.init(size.height,size.width);
			setCameraHolder(uiCameraPreviewSurfaceView.getHolder());
		} else {
			Point point = CameraUtils.getDefaultDisplaySize(this);
			int displayWidth = point.x;
			int displayHeight = point.y;
			
			uiCameraPreviewSurfaceView.init(displayWidth,displayHeight);
			setCameraHolder(uiCameraPreviewSurfaceView.getHolder());
		}
	}
	
	/**
	 * Handle the photo capture
	 */
	private void photoCaptured(String uri) {
		Intent intent = new Intent(this, EditActivity.class);
		intent.putExtra(EditActivity.EXTRA_PHOTO_HEIGHT, mPhotoHeight);
		intent.putExtra(EditActivity.EXTRA_PHOTO_URI, uri);
		startActivity(intent);
	}
	
	/**
	 * Start the camera preview
	 */
	private void startPreview() {
		if (mCameraState != CAMERA_STOPPED)
			stopPreview();
		
		mCamera.startPreview();
		mCameraState = CAMERA_OPEN_SUCCESS;
	}
	
	/**
	 * Stop the camera preview
	 */
	private void stopPreview() {
        if (mCamera != null && mCameraState != CAMERA_STOPPED) {
        	mCamera.stopPreview();
        	mCameraState = CAMERA_STOPPED;
        }
	}
	
	/**
	 * Close the camera
	 */
	private void closeCamera() {
		if (mCamera != null && mCameraState == CAMERA_STOPPED) {
			mCamera.release();
			mCamera = null;
		}
	}
	
	/**
	 * Attach the camera to the provided SurfaceHolder
	 * @param	holder	The surface holder to attach the camera to
	 */
	private void setCameraHolder(SurfaceHolder holder) {		
		if (mCamera != null) {
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException e) { }
			
			if (mCameraState == CAMERA_STOPPED) {
				startPreview();
			}
		}
	}
	
	/**
	 * Update the camera parameters
	 */
	private void updateCameraParameters() {
		mCameraParameters = mCamera.getParameters();
		
		// select the correct orientation
		CameraUtils.setDisplayOrientation(mCamera, mCameraType, this);
		
		// calculate the preview size
		Size size = mCameraParameters.getPictureSize();
        List<Size> sizes = mCameraParameters.getSupportedPreviewSizes();
        Size optimalSize = CameraUtils.getOptimalPreviewSize(
        	this, 
        	sizes,
        	(double) size.width / size.height
        );
        
        mCameraParameters.setPreviewSize(optimalSize.width, optimalSize.height);
        
		// enabled continuous focus if it is available
		if (CameraUtils.isAutoFocusContinousPictureSupported(mCameraParameters, getApplicationContext())) {
			mCameraParameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		}
		
		mCamera.setParameters(mCameraParameters);
	}
	
	/**
	 * Save the photo that has been captured to the media store
	 */
	protected void capturePhoto() {
		if (mCamera != null && mCameraState == CAMERA_OPEN_SUCCESS) {
			mCaptureAnimation.setAnimationListener(this);
			uiCaptureAnimationLayout.setVisibility(View.VISIBLE);
			uiCaptureAnimationLayout.setAnimation(mCaptureAnimation);
			mCaptureAnimation.start();
			
			mCamera.takePicture(null, null, this);
		}
	}
	
	/**
	 * Reverse the between front and back facing (if more than 1 camera exists)
	 */
	protected void reverseCamera() {
		if (mCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			mCameraType = Camera.CameraInfo.CAMERA_FACING_BACK;
		} else {
			mCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
		}
		
		stopPreview();
		closeCamera();
		mCameraStartThread = new CameraStartThread();
		mCameraStartThread.start();
	}
	
	/**
	 * Toggle through the flash states
	 */
	protected void flashToggle(String flashType) {
		if (mCamera != null && mCameraState == CAMERA_OPEN_SUCCESS)
			CameraUtils.setCameraFlashType(mCamera, flashType);
	}
	
	/**
	 * Show or hide the grid
	 */
	protected void showHideGrid() {
		if (mIsGridShowing) {
			uiCameraGridView.setVisibility(View.GONE);
			mIsGridShowing = false;
		} else {
			uiCameraGridView.setVisibility(View.VISIBLE);
			mIsGridShowing = true;
		}
	}
	
	/**
	 * Check whether a camera exists on the device
	 * @param	context	The application context
	 * @return	Does a camera exist
	 */
	private boolean checkCameraHardwareExists(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        return true;
	    } else {
	        return false;
	    }
	}
	
	/**
	 * Build the ui
	 */
	@SuppressWarnings("deprecation")
	private void buildUi() {
		// get the device screen size
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		
		int headerHeight = uiCapturePhotoHeaderLayout.getMeasuredHeight();
		int remaindingScreen = (height - width) - headerHeight;
		int gridViewHeight = height - remaindingScreen - headerHeight;
		int gridSectionWidth = width / 3;
		
		mPhotoYCrop = headerHeight;
		mPhotoHeightCrop = remaindingScreen;
		mPhotoHeight = gridViewHeight;
		
		// add grid view
		uiCameraGridView = new CameraGridView(this);
		uiCameraGridView.init(
			width,
			gridViewHeight,
			gridSectionWidth, 
			gridSectionWidth
		);
		
		RelativeLayout.LayoutParams gridViewparams = new RelativeLayout.LayoutParams(width, gridViewHeight);
		gridViewparams.topMargin = headerHeight;
		
		uiCameraGridView.setVisibility(View.GONE);
		uiActivityParentLayout.addView(uiCameraGridView);
		uiCameraGridView.setLayoutParams(gridViewparams);
		
		// setup the control layout to give the camera the impression of a 1:1 aspect ratio
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,remaindingScreen);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		uiControlsLayout.setLayoutParams(params);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			uiCapturePhotoHeaderLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
		} else {
			uiCapturePhotoHeaderLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		}
	}

	@Override
	public void onPictureTaken(final byte[] data, Camera camera) {
		if (mCameraResponseType.equals(EXTRA_CAMERA_IMMEDIATE)) {
			Intent intent = new Intent();
			intent.putExtra(EXTRA_PHOTO_BYTE_DATA, data);
			setResult(EXTRA_CAMERA_RESULT_CODE, intent);
		} else if (mCameraResponseType.equals(EXTRA_CAMERA_RESIZE)) {
			mPhotoData = data;
			new ResizeImageThread().start();
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (animation == mCaptureAnimation) {
			uiCaptureAnimationLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onAnimationStart(Animation animation) { }
	
	@Override
	public void onAnimationRepeat(Animation animation) { }

	@Override
	public void onGlobalLayout() {
		buildUi();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setCameraHolder(holder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		stopPreview();
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }
	
	protected abstract RelativeLayout getActivityParentLayout();
	protected abstract FrameLayout getHeaderFrameLayout();
	protected abstract FrameLayout getControlsFrameLayout();
	protected abstract PreviewSurfaceView getPreviewSurfaceView();
}
