package com.hendrik.ledcontroller.Utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Settings {

    public static final String SETTINGS = "BTSettings";
    public static final String DEVICE_MAC = "mac_address";
    public static final String DEVICE_NAME = "device_name";

    private static final Map<String, String> defaultValues;
    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put(DEVICE_MAC, "one");
        aMap.put(DEVICE_NAME, "two");
        defaultValues = Collections.unmodifiableMap(aMap);
    }

    public static String getDefault(final String field) {
        return defaultValues.get(field);
    }
}
