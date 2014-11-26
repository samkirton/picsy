package com.memtrip.picsy.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.memtrip.picsy.R;
import com.memtrip.picsy.utils.DisplayUtils;

/**
 *
 */
public class GridView extends View {
    private int mWidth;
    private int mSection;
    private Paint mLinePaint;
    private boolean hasInit;

    private static final float HAIRLINE = 0f;

    public GridView(Context context) {
        super(context);
        setStyle();
        init(context);
    }

    public GridView(Context context, AttributeSet attrs) {
        super(context,attrs);
        setStyle();
        init(context);
    }

    public GridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setStyle();
        init(context);
    }

    /**
     * Set the view style
     */
    private void setStyle() {
        mLinePaint = new Paint();
        mLinePaint.setColor(getResources().getColor(R.color.com_picsy_view_camera_grid_border));
        mLinePaint.setStrokeWidth(HAIRLINE);
        mLinePaint.setStyle(Paint.Style.FILL);
    }

    /**
     * Set the width and heigth of the view
     * @param   context The activity context for the display instance
     */
    public void init(Context context) {
        mWidth = DisplayUtils.getDisplayWidth(((Activity)context).getWindowManager().getDefaultDisplay());
        mSection = mWidth / 3;
        hasInit = true;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (hasInit) {
            int xSections = (int)Math.floor(mWidth / mSection);
            for (int i = 1; i <= xSections; i++) {
                canvas.drawLine(
                        mSection *i,
                        0,
                        mSection *i,
                        mWidth,
                        mLinePaint
                );
            }

            int ySections = (int)Math.floor(mWidth / mSection);
            for (int i = 1; i <= ySections; i++) {
                canvas.drawLine(
                        0,
                        mSection*i,
                        mWidth,
                        mSection*i,
                        mLinePaint
                );
            }
        }
    }
}