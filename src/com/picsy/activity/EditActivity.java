package com.picsy.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.picsy.R;
import com.picsy.utils.BitmapUtils;
import com.picsy.utils.ContentUtils;
import com.picsy.view.CameraGridView;

/**
 * @author samuelkirton
 */
public class EditActivity extends Activity {
	private ImageView uiImageDisplayView;
	private CameraGridView uiCameraGridView;
	private ProgressBar uiProgressBar;
	
	private Context mContext;
	private int mPhotoHeight;
	private String mPhotoUri;
	private int mPhotoYCrop;
	private int mPhotoHeightCrop;
	private Uri mUri;
	private String mRealPath;
	
	public static final String EXTRA_PHOTO_URI = "EXTRA_PHOTO_URI";
	public static final String EXTRA_PHOTO_HEIGHT = "EXTRA_PHOTO_HEIGHT";
	public static final String EXTRA_PHOTO_Y_CROP = "EXTRA_PHOTO_Y_CROP";
	public static final String EXTRA_PHOTO_HEIGHT_CROP = "EXTRA_PHOTO_HEIGHT_CROP";
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
			Bitmap croppedBitmap = null;
			try {				
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), mUri);
				int rotation = BitmapUtils.necessaryPortraitRotation(mRealPath);
				croppedBitmap = BitmapUtils.cropBitmap(bitmap, mPhotoYCrop, mPhotoHeightCrop,rotation);
				bitmap.recycle();
			} catch (IOException e) { }
			
			((Activity)mContext).runOnUiThread(new ShowImageRunnable(croppedBitmap));
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
		
		FrameLayout.LayoutParams gridViewparams = new FrameLayout.LayoutParams(mPhotoHeight, mPhotoHeight);
		gridViewparams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		uiCameraGridView.setLayoutParams(gridViewparams);
		uiCameraGridView.init(
			mPhotoHeight,
			mPhotoHeight,
			mPhotoHeight/3, 
			mPhotoHeight/3
		);
	}
}
