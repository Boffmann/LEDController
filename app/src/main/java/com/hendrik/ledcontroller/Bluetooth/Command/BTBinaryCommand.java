package com.hendrik.ledcontroller.Bluetooth.Command;

import android.util.Log;

import com.hendrik.ledcontroller.Bluetooth.BTTransmitProtocol;

import java.util.ArrayList;

/**
 * Created by hendrik tjabben on 12.03.2018.
 */

public class BTBinaryCommand extends BTCommand {

    private final static String TAG = "BTBinaryCommand";

    private final BTTransmitProtocol.ActionType mAction;
    private final Integer mValue;

    public BTBinaryCommand(final BTTransmitProtocol.ActionType action, final Integer value){
        if (!BTTransmitProtocol.isBinaryAction(action)) {
            Log.e(TAG, "Non binary action initialized for binary command");
        }

        mAction = action;
        mValue = value;
    }

    @Override
    public BTTransmitProtocol.ActionType getAction() {
        return mAction;
    }

    @Override
    public ArrayList<Integer> getValues() {
        ArrayList<Integer> result = new ArrayList();
        result.add(mValue);
        return result;
    }

    @Override
    public String toString() {
        switch (mAction) {
            case BRIGHTNESS:
                return "BRIGHTNESS - Value: " + mValue.toString();
            default:
                return "Unqualified command";
        }
    }
}
