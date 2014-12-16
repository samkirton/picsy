package com.memtrip.picsy.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class ViewUtils {

    /**
     * Returns a custom color attribute
     * @param	view	The view the attribute is associated with
     * @param	attrs	The AttributeSet to pull the attribute from
     * @param	attributeList	A list of attributes
     * @param	attributeIndex	The attribute index to retrieve from the list
     * @return	A color based on the arguments provided
     */
    public static int getColorFromAttribute(View view, AttributeSet attrs, int[] attributeList, int attributeIndex) {
        int id = getResourceId(view.getContext(),attrs,attributeList,attributeIndex);

        if (id > 0) {
            return view.getResources().getColor(id);
        } else {
            return -1;
        }
    }

    /**
     * Returns a custom drawable attribute
     * @param	view	The view the attribute is associated with
     * @param	attrs	The AttributeSet to pull the attribute from
     * @param	attributeList	A list of attributes
     * @param	attributeIndex	The attribute index to retrieve from the list
     * @return	A drawable based on the arguments provided
     */
    public static Drawable getDrawableFromAttribute(View view, AttributeSet attrs, int[] attributeList, int attributeIndex) {
        int id = getResourceId(view.getContext(),attrs,attributeList,attributeIndex);

        if (id > 0) {
            return view.getResources().getDrawable(id);
        } else {
            return null;
        }
    }

    /**
     * Retrieve the resourceId of custom attributes
     * @param	context	The context of the View
     * @param	attrs	The attribute set of the view
     * @param	attributeList	A list of attributes
     * @param	attributeIndex	The attribute index to retrieve from the list
     * @return	A resourceId that matches the arguments provided
     */
    private static int getResourceId(Context context, AttributeSet attrs, int[] attributeList, int attributeIndex) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,attributeList);
        int resourceId = typedArray.getResourceId(attributeIndex, 0);
        typedArray.recycle();

        return resourceId;
    }
}
