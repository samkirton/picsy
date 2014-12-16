package com.memtrip.picsy.utils;

import android.graphics.Point;
import android.view.Display;

/**
 *
 */
public class DisplayUtils {

    public static int getDisplayWidth(Display display) {
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getDisplayHeight(Display display) {
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }
}
