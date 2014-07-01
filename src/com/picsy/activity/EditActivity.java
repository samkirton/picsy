package com.picsy.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.picsy.R;
import com.picsy.utils.BitmapUtils;
import com.picsy.utils.ContentUtils;
import com.picsy.view.CameraGridView;
import com.picsy.view.ImageResizeView;

/**
 * @author samuelkirton
 */
public class EditActivity extends Activity implements OnClickListener, OnSeekBarChangeListener {
	private ImageView uiImageDisplayView;
	private CameraGridView uiCameraGridView;
	private ProgressBar uiProgressBar;
	private ImageResizeView uiImageResizeView;
	private Button uiRotateButton;
	private Button uiDoneButton;
	private SeekBar uiScaleSeekBar;
	
	private Context mContext;
	private Bitmap mOriginalBitmap;
	private int mPhotoHeight;
	private String mPhotoUri;
	private int mPhotoYCrop;
	private int mPhotoHeightCrop;
	private Uri mUri;
	private String mRealPath;
	private int mPreviousProgress = 100;
	
	public static final String EXTRA_PHOTO_URI = "EXTRA_PHOTO_URI";
	public static final String EXTRA_PHOTO_HEIGHT = "EXTRA_PHOTO_HEIGHT";
	public static final String EXTRA_PHOTO_Y_CROP = "EXTRA_PHOTO_Y_CROP";
	public static final String EXTRA_PHOTO_HEIGHT_CROP = "EXTRA_PHOTO_HEIGHT_CROP";
	public static final String EXTRA_MAX_WIDTH = "EXTRA_MAX_WIDTH";
	public static final String EXTRA_MAX_HEIGHT = "EXTRA_MAX_HEIGHT";
	public static final String EXTRA_MIN_WIDTH = "EXTRA_MIN_WIDTH";
	public static final String EXTRA_MIN_HEIGHT = "EXTRA_MIN_HEIGHT";
	public static final String BROADCAST_PHOTO_CAPTURED = "BROADCAST_PHOTO_CAPTURED";
	
	private BroadcastReceiver mPhotoCapturedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mPhotoHeight = intent.getIntExtra(EXTRA_PHOTO_HEIGHT, -1);
			mPhotoUri = intent.getStringExtra(EXTRA_PHOTO_URI);
			mPhotoYCrop = intent.getIntExtra(EXTRA_PHOTO_Y_CROP,-1);
			mPhotoHeightCrop = intent.getIntExtra(EXTRA_PHOTO_HEIGHT_CROP,-1);
			
			mUri = Uri.parse(mPhotoUri);
			mRealPath = ContentUtils.getRealPathFromURI(mUri, mContext);
			new CameraStartThread().start();
		}
	};
	
	private class CameraStartThread extends Thread {
		@Override
		public void run() {
			try {				
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), mUri);
				int rotation = BitmapUtils.necessaryPortraitRotation(mRealPath);
				mOriginalBitmap = BitmapUtils.cropBitmap(bitmap, mPhotoYCrop, mPhotoHeightCrop, rotation);
				bitmap.recycle();
			} catch (IOException e) { }
			
			((Activity)mContext).runOnUiThread(new ShowImageRunnable(mOriginalBitmap));
		}
	};
	
	private class ShowImageRunnable implements Runnable {
		private Bitmap image;
		
		public ShowImageRunnable(Bitmap bitmap) {
			image = bitmap;
		}
		
		@Override
		public void run() {
			showImage(image);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		
		uiImageDisplayView = (ImageView)findViewById(R.id.activity_edit_imageview);
		uiCameraGridView = (CameraGridView)findViewById(R.id.activity_edit_gridView);
		uiProgressBar = (ProgressBar)findViewById(R.id.activity_edit_progressbar);
		uiImageResizeView = (ImageResizeView)findViewById(R.id.activity_edit_imageresizeview);
		uiRotateButton = (Button)findViewById(R.id.activity_edit_rotate);
		uiDoneButton = (Button)findViewById(R.id.activity_edit_done);
		uiScaleSeekBar = (SeekBar)findViewById(R.id.activity_edit_scale);
		
		uiRotateButton.setOnClickListener(this);
		uiScaleSeekBar.setOnSeekBarChangeListener(this);
		
		mContext = this;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(
			mPhotoCapturedReceiver, 
			new IntentFilter(BROADCAST_PHOTO_CAPTURED)
		);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mPhotoCapturedReceiver);
	}
	
	/**
	 * Display the bitmap
	 */
	private void showImage(Bitmap image) {
		uiImageDisplayView.setImageBitmap(image);
		uiProgressBar.setVisibility(View.GONE);
		
		FrameLayout.LayoutParams gridViewParams = new FrameLayout.LayoutParams(mPhotoHeight, mPhotoHeight);
		gridViewParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		uiCameraGridView.setLayoutParams(gridViewParams);
		uiCameraGridView.init(
			mPhotoHeight,
			mPhotoHeight,
			mPhotoHeight/3, 
			mPhotoHeight/3
		);
		
		FrameLayout.LayoutParams imageResizeParams = new FrameLayout.LayoutParams(1080, 318);
		imageResizeParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		uiImageResizeView.setLayoutParams(imageResizeParams);
		uiImageResizeView.init(1080, 318, 318, 318);
	}
	
	/**
	 * Rotate the image view
	 */
	private void rotate_Click() {
		mOriginalBitmap = BitmapUtils.rotateBitmap(90, mOriginalBitmap);
		uiImageDisplayView.setImageBitmap(mOriginalBitmap);
	}
	
	/**
	 * Return the image to the consuming activity
	 */
	private void done_Click() {
		//TODO: Send the result back to the activity that requested an image
	}
	
	/**
	 * Scale the image based on the progress of the seekbar
	 * @param	progress	The progress of the seekbar
	 */
	private void scaleImage(int progress) {
		Bitmap scaleBitmap = BitmapUtils.scaleBitmap(
			progress,
			mPhotoHeight,
			mPhotoHeight,
			mOriginalBitmap,
			(progress > mPreviousProgress)
		);
		
		uiImageDisplayView.setImageBitmap(scaleBitmap);
		mPreviousProgress = progress;
	}

	@Override
	public void onClick(View v) {
		if (v == uiRotateButton) {
			rotate_Click();
		} else if (v == uiDoneButton) {
			done_Click();
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (progress <= 20) {
			progress = 20;
			uiScaleSeekBar.setProgress(progress);
		}
		
		scaleImage(progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
}
