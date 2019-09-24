package com.hendrik.ledcontroller.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.hendrik.ledcontroller.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to manage the app's shared preferences
 */
public class Settings {

    /** Shared preferences singleton */
    private static SharedPreferences mSharedPref = null;

    /** Settings names */
    public static final String SETTINGS = "BTSettings";
    public static final String DEVICE_MAC = "mac_address";
    public static final String DEVICE_NAME = "device_name";

    /** Settings names for colors in color picker */
    public static final String COLOR1 = "color_1";
    public static final String COLOR2 = "color_2";
    public static final String COLOR3 = "color_3";
    public static final String COLOR4 = "color_4";
    public static final String COLOR5 = "color_5";
    public static final String COLOR6 = "color_6";
    public static final String COLOR7 = "color_7";
    public static final String COLOR8 = "color_8";
    public static final String COLOR9 = "color_9";

    /** Map holding all default values */
    private static Map<String, String> defaultValues = new HashMap<>();

    /**
     * Initialize the settings. This includes default values
     * @param applicationContext Application Context
     */
    public static void initSettings(final Context applicationContext) {
        defaultValues.put(DEVICE_MAC, "");
        defaultValues.put(DEVICE_NAME, "");

        defaultValues.put(COLOR1, Integer.toString(applicationContext.getResources().getColor(R.color.colorButton1)));
        defaultValues.put(COLOR2, Integer.toString(applicationContext.getResources().getColor(R.color.colorButton2)));
        defaultValues.put(COLOR3, Integer.toString(applicationContext.getResources().getColor(R.color.colorButton3)));
        defaultValues.put(COLOR4, Integer.toString(applicationContext.getResources().getColor(R.color.colorButton4)));
        defaultValues.put(COLOR5, Integer.toString(applicationContext.getResources().getColor(R.color.colorButton5)));
        defaultValues.put(COLOR6, Integer.toString(applicationContext.getResources().getColor(R.color.colorButton6)));
        defaultValues.put(COLOR7, Integer.toString(applicationContext.getResources().getColor(R.color.colorButton7)));
        defaultValues.put(COLOR8, Integer.toString(applicationContext.getResources().getColor(R.color.colorButton8)));
        defaultValues.put(COLOR9, Integer.toString(applicationContext.getResources().getColor(R.color.colorButton9)));
    }

    /**
     * Get default for a certain setting
     * @param field Settings field to get default value from
     * @return The default value
     */
    public static String getDefault(final String field) {
        return defaultValues.get(field);
    }

    /**
     * Initialize and get the shared preferences
     * @param context ApplicationContext
     * @return the shared preference
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        if (mSharedPref == null) {
            mSharedPref = context.getSharedPreferences(SETTINGS, 0);
        }
        return mSharedPref;
    }

    /**
     * Get a string from the settings
     * @param settingIndicator The settings string
     * @return the string behind the settings string or default
     */
    public static String getString(final String settingIndicator) {
        return mSharedPref.getString(settingIndicator, getDefault(settingIndicator));
    }
}
