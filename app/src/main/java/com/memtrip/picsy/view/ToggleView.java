package com.memtrip.picsy.view;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * A view that toggles between image states
 */
public class ToggleView extends ImageView implements View.OnClickListener {
	private Map<Integer,String> mDrawableKeyHashmap;
	private int mSelectedItem = -1;
	private String mSelectedValue;
    private OnToggleSwitch mOnToggleSwitch;

    public interface OnToggleSwitch {
        public void onToggleSwitch(String value);
    }
	
	public String getSelectedValue() {
		return mSelectedValue;
	}

    public void setOnToggleSwitch(OnToggleSwitch newVal) {
        mOnToggleSwitch = newVal;
    }
	
	public ToggleView(Context context, AttributeSet attrs) {
		super(context, attrs);
        setOnClickListener(this);
	}

	/**
	 * Build the view based on the provided TreeMap
	 * @param	drawableKeyHashmap	A drawable / key pair hashmap
	 */
	public void build(LinkedHashMap<Integer,String> drawableKeyHashmap) {
		mDrawableKeyHashmap = drawableKeyHashmap;
		toggle();
	}
	
	/**
	 * Toggle the view to use the next drawable and key
	 */
	public void toggle() {
		if (mSelectedItem < mDrawableKeyHashmap.size()-1) {
			mSelectedItem++;
		} else {
			mSelectedItem = 0;
		}
		
		int count = 0;
	    for (Map.Entry<Integer,String> entry : mDrawableKeyHashmap.entrySet()) {
	    	if (count == mSelectedItem) {
	    		setImageDrawable(getResources().getDrawable(entry.getKey()));
	    		mSelectedValue = entry.getValue();
                break;
	    	}
	    	
	    	count++;
	    }

        if (mOnToggleSwitch != null) {
            mOnToggleSwitch.onToggleSwitch(mSelectedValue);
        }
	}

    @Override
    public void onClick(View v) {
        toggle();
    }
}