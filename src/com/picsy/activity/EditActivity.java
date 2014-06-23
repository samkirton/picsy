package com.picsy.activity;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.picsy.R;
import com.picsy.view.CameraGridView;

/**
 * @author samuelkirton
 */
public class EditActivity extends Activity {
	private ImageView uiImageDisplayView;
	private CameraGridView uiCameraGridView;
	private ProgressBar uiProgressBar;
	
	private Context mContext;
	private int mYCrop;
	private int mHeightCrop;
	private int mPhotoHeight;
	private String mPhotoUri;
	
	public static final String EXTRA_PHOTO_URI = "EXTRA_PHOTO_URI";
	public static final String EXTRA_PHOTO_HEIGHT = "EXTRA_PHOTO_HEIGHT";
	
	private class CameraStartThread extends Thread {
		@Override
		public void run() {
			Bitmap bitmap = null;
			try {
				bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(mPhotoUri));
			} catch (IOException e) { }
			
			((Activity)mContext).runOnUiThread(new ShowImageRunnable(bitmap));
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
		mPhotoHeight = getIntent().getIntExtra(EXTRA_PHOTO_HEIGHT, -1);
		mPhotoUri = getIntent().getStringExtra(EXTRA_PHOTO_URI);
		
		FrameLayout.LayoutParams gridViewparams = new FrameLayout.LayoutParams(mPhotoHeight, mPhotoHeight);
		uiCameraGridView.setLayoutParams(gridViewparams);
		uiCameraGridView.init(
			mPhotoHeight,
			mPhotoHeight,
			mPhotoHeight/3, 
			mPhotoHeight/3
		);
		
		new CameraStartThread().start();
	}
	
	/**
	 * Display the bitmap
	 */
	private void showImage(Bitmap image) {
		uiImageDisplayView.setImageBitmap(image);
		uiProgressBar.setVisibility(View.GONE);
	}
}
