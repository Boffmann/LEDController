package com.hendrik.ledcontroller.Bluetooth.Command;

import android.util.Log;

import com.hendrik.ledcontroller.Bluetooth.BTTransmitProtocol;

import java.util.ArrayList;

/**
 * Created by hendrik tjabben on 09.03.2018.
 */

public class BTUnaryCommand extends BTCommand {

    private final static String TAG = "BTUnaryCommand";

    private final BTTransmitProtocol.ActionType mAction;

    public BTUnaryCommand(final BTTransmitProtocol.ActionType action) {
        if (!BTTransmitProtocol.isUnaryAction(action)) {
            Log.e(TAG, "Required action type for unary command requires extra value parameters");
        }
        mAction = action;
    }

    public BTTransmitProtocol.ActionType getAction() {
        return mAction;
    }

    /**
     * Gets value for command. Since command is unary, has no value so null is returned
     * @return null, because command has no value
     */
    public ArrayList<Integer> getValues() {
        return null;
    }

    public String toString() {
        switch (mAction) {
            case ON:
                return "ON";
            case OFF:
                return "OFF";
            default:
                return "Unqualified command";
        }
    }
}
