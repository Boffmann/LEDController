package com.hendrik.ledcontroller.Bluetooth.Command;

import android.graphics.Color;
import android.util.Log;

import com.hendrik.ledcontroller.Bluetooth.BTTransmitProtocol;

import java.util.ArrayList;

/**
 * Created by hendrik tjabben on 12.03.2018.
 */

public class BTWidenedCommand extends BTCommand {

    private final static String TAG = "BTWidenedCommand";

    private final BTTransmitProtocol.ActionType mAction;
    private final ArrayList<Integer> mValues;

    public BTWidenedCommand(final BTTransmitProtocol.ActionType action, final ArrayList<Integer> values) {
        if (BTTransmitProtocol.isUnaryAction(action) || BTTransmitProtocol.isBinaryAction(action)) {
            Log.e(TAG, "Widened command initialized with unary or binary action");
        }
        mAction = action;
        mValues = values;
    }

    @Override
    public BTTransmitProtocol.ActionType getAction() {
        return mAction;
    }

    @Override
    public ArrayList<Integer> getValues() {
        return mValues;
    }

    @Override
    public String toString() {
        switch (mAction) {
            case COLOR:
                return "COLOR - Value: " + Color.red(mValues.get(0)) + " : " + Color.green(mValues.get(1)) + " : " + Color.blue(mValues.get(2));
            default:
                return "Unqualified command";
        }
    }
}
