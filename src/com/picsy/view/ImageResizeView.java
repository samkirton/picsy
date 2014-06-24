package com.picsy.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;

import com.picsy.R;

/**
 * @author samuelkirton
 */
public class ImageResizeView extends FrameLayout implements OnTouchListener {
	private View mTopLeftPoint;
	private View mTopRightPoint;
	private View mBottomRightPoint;
	private View mBottomLeftPoint;
	
	private float mLastTouchX;
	private float mLastTouchY;
	private int mActivePointerId;
	private float mPosX;
	private float mPosY;
	
	private static final int DIRECTION_TOP_LEFT = 0x0;
	private static final int DIRECTION_TOP_RIGHT = 0x1;
	private static final int DIRECTION_BOTTOM_RIGHT = 0x2;
	private static final int DIRECTION_BOTTOM_LEFT = 0x3;
	private static final int INVALID_POINTER_ID = 1785443198;
	
	public ImageResizeView(Context context) {
		super(context);
		init();
	}
	
	public ImageResizeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.view_image_resize, this, true);
		
		mTopLeftPoint = findViewById(R.id.view_image_resize_top_left);
		mTopRightPoint = findViewById(R.id.view_image_resize_top_right);
		mBottomRightPoint = findViewById(R.id.view_image_resize_bottom_right);
		mBottomLeftPoint = findViewById(R.id.view_image_resize_bottom_left);
		
		mTopLeftPoint.setOnTouchListener(this);
		mTopRightPoint.setOnTouchListener(this);
		mBottomRightPoint.setOnTouchListener(this);
		mBottomLeftPoint.setOnTouchListener(this);
	}
	
	private void resizeLayout(int x, int y, View v) {
		if (v == mBottomRightPoint) {
			// increase the width
			// increase the height
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)getLayoutParams();
			params.width += x;
			params.height += y;
			setLayoutParams(params);
		} else if (v == this) {
			System.out.println("MOVE!!");
		}
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
			
			    mPosX += dx;
			    mPosY += dy;
			
			    resizeLayout((int)dx,(int)dy,v);
			
			    // Remember this touch position for the next move event
			    mLastTouchX = x;
			    mLastTouchY = y;
			
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
