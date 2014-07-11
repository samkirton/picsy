package com.picsy.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;

import com.picsy.R;
import com.picsy.model.CropResult;

/**
 * @author samuelkirton
 */
public class ImageResizeView extends FrameLayout implements OnTouchListener {
	private View uiTopLeftPoint;
	private View uiTopRightPoint;
	private View uiBottomRightPoint;
	private View uiBottomLeftPoint;
	private CameraGridView uiCameraGridView;
	private FrameLayout uiResizeContainer;
	
	private float mLastTouchX;
	private float mLastTouchY;
	private int mActivePointerId;
	private int mMaxWidth;
	private int mMaxHeight;
	private int mMinWidth;
	private int mMinHeight;
	
	private static final int INVALID_POINTER_ID = 1785443198;
	
	public ImageResizeView(Context context) {
		super(context);
		setup();
	}
	
	public ImageResizeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}
	
	private void setup() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.com_picsy_view_image_resize, this, true);
		
		uiTopLeftPoint = findViewById(R.id.com_picsy_view_image_resize_top_left);
		uiTopRightPoint = findViewById(R.id.com_picsy_view_image_resize_top_right);
		uiBottomRightPoint = findViewById(R.id.com_picsy_view_image_resize_bottom_right);
		uiBottomLeftPoint = findViewById(R.id.com_picsy_view_image_resize_bottom_left);
		uiCameraGridView = (CameraGridView)findViewById(R.id.com_picsy_view_image_cameragridView);
		uiResizeContainer = (FrameLayout)findViewById(R.id.com_picsy_view_image_resize_container);
		
		uiTopLeftPoint.setOnTouchListener(this);
		uiTopRightPoint.setOnTouchListener(this);
		uiBottomRightPoint.setOnTouchListener(this);
		uiBottomLeftPoint.setOnTouchListener(this);
		uiResizeContainer.setOnTouchListener(this);
	}
	
	/**
	 * Initialise the view
	 * @param 	maxWidth	The maximum resize width
	 * @param	maxHeight	The maximum resize height
	 */
	public void init(int maxWidth, int maxHeight, int minWidth, int minHeight, int containerWidth) {
		mMaxWidth = maxWidth;
		mMaxHeight = maxHeight;
		mMinWidth = minWidth;
		mMinHeight = minHeight;
		
		FrameLayout.LayoutParams resizeParams = new FrameLayout.LayoutParams(minWidth, minHeight);
		resizeParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		uiResizeContainer.setLayoutParams(resizeParams);
		
		FrameLayout.LayoutParams gridViewParams = new FrameLayout.LayoutParams(containerWidth, containerWidth);
		gridViewParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		uiCameraGridView.setLayoutParams(gridViewParams);
		
		uiCameraGridView.init(
			containerWidth,
			containerWidth,
			containerWidth/3, 
			containerWidth/3
		);
	}
	
	/**
	 * @return	The dimensions of the crop result
	 */
	public CropResult crop() {
		CropResult cropResult = new CropResult();
		cropResult.setX(uiResizeContainer.getLeft());
		cropResult.setY(uiResizeContainer.getTop());
		cropResult.setWidth(uiResizeContainer.getWidth());
		cropResult.setHeight(uiResizeContainer.getHeight());
		return cropResult;
	}
	
	private void resizeLayout(int x, int y, View v) {
		if (v == uiBottomRightPoint) {
			// increase the width
			// increase the height
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)uiResizeContainer.getLayoutParams();
			params.width += x;
			params.height += y;
			setContainerParams(uiResizeContainer,params,x,y);
		} else if (v == uiResizeContainer) {
			// move the whole container
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)uiResizeContainer.getLayoutParams();
			params.leftMargin += x;
			params.topMargin += y;
			setContainerParams(uiResizeContainer,params,x,y);
		}
	}
	
	private void setContainerParams(FrameLayout container, FrameLayout.LayoutParams params, int x, int y) {
		if ((params.width + x) > mMaxWidth)
			params.width = mMaxWidth;
		
		if ((params.height + y) > mMaxHeight) 
			params.height = mMaxHeight;
		
		if ((params.width + x) < mMinWidth)
			params.width = mMinWidth;
		
		if ((params.height + y) < mMinHeight)
			params.height = mMinHeight;

		container.setLayoutParams(params);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int pointerIndex = -1;
		float x = -1;
		float y = -1;
		
		switch (event.getAction()) {
		    case MotionEvent.ACTION_DOWN: 
		        pointerIndex = MotionEventCompat.getActionIndex(event); 
		        x = MotionEventCompat.getX(event, pointerIndex); 
		        y = MotionEventCompat.getY(event, pointerIndex); 
		            
		        // Remember where we started (for dragging)
		        mLastTouchX = x;
		        mLastTouchY = y;
		        // Save the ID of this pointer (for dragging)
		        mActivePointerId = MotionEventCompat.getPointerId(event, 0);
		        break;
	            
			case MotionEvent.ACTION_MOVE:
			    // Find the index of the active pointer and fetch its position
			    pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);  
			        
			    x = MotionEventCompat.getX(event, pointerIndex);
			    y = MotionEventCompat.getY(event, pointerIndex);
			        
			    // Calculate the distance moved
			    final float dx = x - mLastTouchX;
			    final float dy = y - mLastTouchY;
			
			    // Remember this touch position for the next move event
			    mLastTouchX = x;
			    mLastTouchY = y;
		    
			    resizeLayout((int)dx,(int)dy,v);
			    break;
			            
		    case MotionEvent.ACTION_UP:
				mActivePointerId = INVALID_POINTER_ID;
				break;
		            
		    case MotionEvent.ACTION_CANCEL:
				mActivePointerId = INVALID_POINTER_ID;
				break;
		        
		    case MotionEvent.ACTION_POINTER_UP:
				pointerIndex = MotionEventCompat.getActionIndex(event); 
				final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex); 
				
				if (pointerId == mActivePointerId) {
				    // This was our active pointer going up. Choose a new
				    // active pointer and adjust accordingly.
				    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				    mLastTouchX = MotionEventCompat.getX(event, newPointerIndex); 
				    mLastTouchY = MotionEventCompat.getY(event, newPointerIndex); 
				    mActivePointerId = MotionEventCompat.getPointerId(event, newPointerIndex);
				}
	
				break;
		}
		
		return true;
	}
}
