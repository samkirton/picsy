package com.picsy.view;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author samuelkirton
 */
public class ToggleView extends ImageView {
	private Map<Integer,String> mDrawableKeyHashmap;
	private int mSelectedItem = -1;
	private String mSelectedValue;
	
	public String getSelectedValue() {
		return mSelectedValue;
	}
	
	public ToggleView(Context context, AttributeSet attrs) {
		super(context, attrs);
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
	    	}
	    	
	    	count++;
	    }
	}
}
