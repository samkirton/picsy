package com.picsy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.picsy.R;

/**
 * @author samuelkirton
 */
public class CameraGridView extends View {
	private int mWidth;
	private int mHeight;
	private int mSectionWidth;
	private int mSectionHeight;
	private Paint mLinePaint;
	private boolean hasInit;
	
	private static final float HAIRLINE = 0f;
	
	public CameraGridView(Context context) {
		super(context);
		setStyle();
	}
	
	public CameraGridView(Context context, AttributeSet attrs) {
		super(context,attrs);
		setStyle();
	}
	
	/**
	 * Set the view style
	 */
	private void setStyle() {
		mLinePaint = new Paint();
		mLinePaint.setColor(getResources().getColor(R.color.view_camera_grid_border));
		mLinePaint.setStrokeWidth(HAIRLINE);
		mLinePaint.setStyle(Paint.Style.FILL);
	}
	
	/**
	 * Set the width and heigth of the view
	 * @param	width	The width of the view
	 * @param 	height	The height of the view
	 */
	public void init(int width, int height, int sectionWidth, int sectionHeight) {
		mWidth = width;
		mHeight = height;
		mSectionWidth = sectionWidth;
		mSectionHeight = sectionHeight;
		hasInit = true;
		invalidate();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {		
		setMeasuredDimension(mWidth, mHeight);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (hasInit) {
			int xSections = (int)Math.floor(mWidth / mSectionWidth);
			for (int i = 1; i <= xSections; i++) {
				canvas.drawLine(
					mSectionWidth*i,
					0, 
					mSectionWidth*i, 
					mHeight, 
					mLinePaint
				);
			}
			
			int ySections = (int)Math.floor(mHeight / mSectionHeight);
			for (int i = 1; i <= ySections; i++) {
				canvas.drawLine(
					0,
					mSectionHeight*i, 
					mWidth, 
					mSectionHeight*i, 
					mLinePaint
				);
			}
		}
	}
}
