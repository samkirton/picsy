package com.picsy.activity;

import java.util.LinkedHashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.picsy.R;
import com.picsy.view.PreviewSurfaceView;
import com.picsy.view.ToggleView;

/**
 * @author samuelkirton
 */
public class CameraActivity extends BaseCameraActivity implements OnClickListener {
	private ImageView uiShowHideGridButton;
	private ImageView uiReverseCameraButton;
	private ToggleView uiFlashToggleView;
	private ImageView uiCaptureButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.com_picsy_activity_camera);
		super.onCreate(savedInstanceState);
		uiShowHideGridButton = (ImageView)findViewById(R.id.com_picsy_activity_camera_show_hide_grid_imageview);
		uiReverseCameraButton = (ImageView)findViewById(R.id.com_picsy_activity_camera_switch);
		uiFlashToggleView = (ToggleView)findViewById(R.id.com_picsy_activity_camera_flash_toggleview);
		uiCaptureButton = (ImageView)findViewById(R.id.com_picsy_activity_camera_capture_imageview);
		
		LinkedHashMap<Integer, String> flashDrawableKeyHashMap = new LinkedHashMap<Integer, String>();
		flashDrawableKeyHashMap.put(R.drawable.com_picsy_activity_camera_flash_off, BaseCameraActivity.PARAM_FLASH_OFF);
		flashDrawableKeyHashMap.put(R.drawable.com_picsy_activity_camera_flash_on, BaseCameraActivity.PARAM_FLASH_ON);
		flashDrawableKeyHashMap.put(R.drawable.com_picsy_activity_camera_flash_auto, BaseCameraActivity.PARAM_FLASH_AUTO);
		
		uiFlashToggleView.build(flashDrawableKeyHashMap);
		
		uiShowHideGridButton.setOnClickListener(this);
		uiReverseCameraButton.setOnClickListener(this);
		uiCaptureButton.setOnClickListener(this);
		uiFlashToggleView.setOnClickListener(this);
	}
	
	@Override
	protected FrameLayout getHeaderFrameLayout() {
		return (FrameLayout)findViewById(R.id.com_picsy_activity_camera_header_layout);
	}

	@Override
	protected FrameLayout getControlsFrameLayout() {
		return (FrameLayout)findViewById(R.id.com_picsy_activity_camera_controls_layout);
	}

	@Override
	protected PreviewSurfaceView getPreviewSurfaceView() {
		return (PreviewSurfaceView)findViewById(R.id.com_picsy_activity_camera_preview_surface_view);
	}
	
	@Override
	protected RelativeLayout getActivityParentLayout() {
		return (RelativeLayout)findViewById(R.id.activity_camera_parent_layout);
	}

	@Override
	public void onClick(View v) {
		if (v == uiShowHideGridButton) {
			showHideGrid();
		} else if (v == uiReverseCameraButton) {
			reverseCamera();
		} else if (v == uiCaptureButton) {
			capturePhoto();
		} else if (v == uiFlashToggleView) {
			uiFlashToggleView.toggle();
			flashToggle(uiFlashToggleView.getSelectedValue());
		}
	}
}
