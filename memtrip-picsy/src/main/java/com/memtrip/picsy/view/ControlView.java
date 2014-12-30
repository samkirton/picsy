package com.memtrip.picsy.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.os.Handler;
import android.widget.LinearLayout;

import com.memtrip.picsy.R;
import com.memtrip.picsy.camera.CameraHolder;
import com.memtrip.picsy.utils.CameraUtils;
import com.memtrip.picsy.utils.DisplayUtils;
import com.memtrip.picsy.utils.ViewUtils;

import java.util.LinkedHashMap;

/**
 * UI components for managing the state of the camera
 */
public class ControlView extends FrameLayout implements View.OnClickListener, ToggleView.OnToggleSwitch, Animation.AnimationListener {
    private FrameLayout uiCameraSpacerLayout;
    private LinearLayout uiControlTabTrayLayout;
    private FrameLayout uiSwitchAnimationLayout;
    private GridView uiGridView;
    private ImageView uiSelectGridImageView;
    private ImageView uiSwitchImageView;
    private ToggleView uiFlashToggleView;
    private FrameLayout uiCaptureLayout;
    private ImageView uiCaptureImageView;
    private Animation uiSwitchAnimation;

    private CameraHolder mCameraHolder;

    private static final int ANIMATION_WAIT = 1000;

    public ControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public ControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public void setCameraHolder(CameraHolder cameraHolder) {
        mCameraHolder = cameraHolder;
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.com_picsy_view_controls,this);

        uiCameraSpacerLayout = (FrameLayout)findViewById(R.id.com_picsy_view_control_camera_spacer);
        uiSwitchAnimationLayout = (FrameLayout)findViewById(R.id.com_picsy_view_control_switch_animation_layout);
        uiGridView = (GridView)findViewById(R.id.com_picsy_view_control_grid);
        uiControlTabTrayLayout = (LinearLayout)findViewById(R.id.com_picsy_view_control_tab_tray_layout);
        uiSelectGridImageView = (ImageView)findViewById(R.id.com_picsy_view_control_show_hide_grid_imageview);
        uiSwitchImageView = (ImageView)findViewById(R.id.com_picsy_view_control_switch);
        uiFlashToggleView = (ToggleView)findViewById(R.id.com_picsy_view_control_flash_toggleview);
        uiCaptureLayout = (FrameLayout)findViewById(R.id.com_picsy_view_control_capture_layout);
        uiCaptureImageView = (ImageView)findViewById(R.id.com_picsy_view_control_capture_imageview);
        uiSwitchAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.com_picsy_camera_switch_camera);

        uiFlashToggleView.setOnToggleSwitch(this);
        uiSelectGridImageView.setOnClickListener(this);
        uiSwitchImageView.setOnClickListener(this);
        uiCaptureImageView.setOnClickListener(this);
        uiSwitchAnimation.setAnimationListener(this);

        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        int width = DisplayUtils.getDisplayWidth(display);

        setCaptureBackgroundColor(attrs);
        setTabTrayBackgroundColor(attrs);
        setCaptureIcon(attrs);

        buildFlashToggleView(uiFlashToggleView);
        setControlLayoutParams(uiCameraSpacerLayout,width,width);
    }

    private void setCaptureBackgroundColor(AttributeSet attrs) {
        int captureBackgroundColor = ViewUtils.getColorFromAttribute(
            this,
            attrs,
            R.styleable.attrs_view_control,
            R.styleable.attrs_view_control_captureBackgroundColor
        );

        if (captureBackgroundColor != -1) {
            uiCaptureLayout.setBackgroundColor(captureBackgroundColor);
        }
    }

    private void setTabTrayBackgroundColor(AttributeSet attrs) {
        int tabTrayColor = ViewUtils.getColorFromAttribute(
            this,
            attrs,
            R.styleable.attrs_view_control,
            R.styleable.attrs_view_control_tabTrayColor
        );

        if (tabTrayColor != -1) {
            uiControlTabTrayLayout.setBackgroundColor(tabTrayColor);
        }
    }

    private void setCaptureIcon(AttributeSet attrs) {
        Drawable drawable = ViewUtils.getDrawableFromAttribute(
            this,
            attrs,
            R.styleable.attrs_view_control,
            R.styleable.attrs_view_control_captureIcon
        );

        if (drawable != null) {
            uiCaptureImageView.setImageDrawable(drawable);
        }
    }

    private void setControlLayoutParams(ViewGroup controlLayout, int width, int height) {
        ViewGroup.LayoutParams params = controlLayout.getLayoutParams();
        params.width = width;
        params.height = height;
    }

    private void buildFlashToggleView(ToggleView toggleView) {
        LinkedHashMap<Integer, String> flashDrawableKeyHashMap = new LinkedHashMap<Integer, String>();
        flashDrawableKeyHashMap.put(R.drawable.com_picsy_view_control_flash_off, CameraUtils.FLASH_OFF);
        flashDrawableKeyHashMap.put(R.drawable.com_picsy_view_control_flash_on, CameraUtils.FLASH_ON);
        flashDrawableKeyHashMap.put(R.drawable.com_picsy_view_control_flash_auto, CameraUtils.FLASH_AUTO);
        toggleView.build(flashDrawableKeyHashMap);
    }

    private void animateSwitchCamera() {
        uiSwitchAnimationLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                uiSwitchAnimationLayout.startAnimation(uiSwitchAnimation);
            }
        }, ANIMATION_WAIT);
    }

    private void animateFlash() {
        uiSwitchAnimationLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                uiSwitchAnimationLayout.startAnimation(uiSwitchAnimation);
            }
        }, ANIMATION_WAIT);
    }

    /**
     * Toggle the grid view visibility
     */
    private void toggleGridView() {
        if (uiGridView.getVisibility() == View.VISIBLE) {
            uiGridView.setVisibility(View.GONE);
        } else {
            uiGridView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == uiSwitchImageView) {
            animateSwitchCamera();
            mCameraHolder.reverse();
        } else if (v == uiCaptureImageView) {
            animateFlash();;
            mCameraHolder.capture();
        } else if (v == uiSelectGridImageView) {
            toggleGridView();
        }
    }

    @Override
    public void onToggleSwitch(String value) {
        if (mCameraHolder != null) {
            mCameraHolder.setFlashType(value);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        uiSwitchAnimationLayout.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationStart(Animation animation) { }

    @Override
    public void onAnimationRepeat(Animation animation) { }
}