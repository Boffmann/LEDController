package com.hendrik.ledcontroller.Bluetooth;

import android.util.Log;

import com.hendrik.ledcontroller.Bluetooth.Command.BTCommand;

import java.util.ArrayList;

/**
 * Created by hendrik tjabben on 09.03.2018.
 */

public class BTSerializer {

    private final static String TAG = "BTSerializer";

    public BTSerializer() {

    }

    private static int[] serialzeMetaData(final ArrayList<Byte> metadata) {
        int [] result = new int[metadata.size()];
        for (int i = 0; i < metadata.size(); i++) {
            result[i] = metadata.get(i);
        }

        return result;
    }

    private static int[] serializeParameterData(final ArrayList<Integer> parameters) {
        if (parameters == null) {
            int[] empty = {};
            return empty;
        }
        int [] result = new int[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            result[i] = parameters.get(i);
        }

        return result;
    }

    public static int[] serialize(final BTCommand command) {

        ArrayList<Byte> commandMetaData = command.getMetaData();
        ArrayList<Integer> commandValues = command.getValues();

        int[] metadata = serialzeMetaData(commandMetaData);

        int[] paramters = serializeParameterData(commandValues);

        // merge metadata and parameters
        int[] result = new int[metadata.length + paramters.length];
        int resultIndex = 0;
        for (int i = 0; i < metadata.length; i++) {
            result[resultIndex] = metadata[i];
            resultIndex++;
        }
        for (int i = 0; i < paramters.length; i++) {
            result[resultIndex] = paramters[i];
            resultIndex++;
        }

        // Log result values
        for (int i = 0; i < result.length; i++) {
            Log.i(TAG, "Result array at: " + i + " = " + result[i]);
        }

        return result;
    }
}
