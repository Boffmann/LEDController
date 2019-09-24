package com.hendrik.ledcontroller.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Utility class to get the screen resolution of the device where this app is running on
 */
public class ScreenResolution {

    /**
     * Get the screen width of the currently used device
     * @param context Application Context
     * @return The screen width
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * Get the screen height of the currently used device
     * @param context Application Context
     * @return The screen height
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}
