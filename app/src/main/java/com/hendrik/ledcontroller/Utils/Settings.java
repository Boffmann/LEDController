package com.hendrik.ledcontroller.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.hendrik.ledcontroller.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Settings {

    private static SharedPreferences mSharedPref = null;

    public static final String SETTINGS = "BTSettings";
    public static final String DEVICE_MAC = "mac_address";
    public static final String DEVICE_NAME = "device_name";

    public static final String FIRSTRUN = "firstrun";

    public static final String COLOR1 = "color_1";
    public static final String COLOR2 = "color_2";
    public static final String COLOR3 = "color_3";
    public static final String COLOR4 = "color_4";
    public static final String COLOR5 = "color_5";
    public static final String COLOR6 = "color_6";
    public static final String COLOR7 = "color_7";
    public static final String COLOR8 = "color_8";
    public static final String COLOR9 = "color_9";

    private static Map<String, String> defaultValues = new HashMap<>();

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

    public static String getDefault(final String field) {
        return defaultValues.get(field);
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        mSharedPref = context.getSharedPreferences(SETTINGS, 0);
        return mSharedPref;
    }

    public static String getString(final String settingIndicator) {
        return mSharedPref.getString(settingIndicator, getDefault(settingIndicator));
    }
}
