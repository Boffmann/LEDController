package com.hendrik.ledcontroller.Bluetooth;

/**
 * Created by hendr on 09.03.2018.
 */

public class BTCommand {

    private final BTTransmitProtocol.ActionType mAction;
    private final Integer mValue;

    public BTCommand(final BTTransmitProtocol.ActionType action, final Integer value) {
        mAction = action;
        mValue = value;
    }

    public BTTransmitProtocol.ActionType getAction() {
        return mAction;
    }

    public Integer getValue() {
        return  mValue;
    }

    public String toString() {
        switch (mAction) {
            case ON:
                return "ON";
            case OFF:
                return "OFF";
            case BRIGHTNESS:
                return "BRIGHTNESS - Value: " + mValue.toString();
            default:
                return "Unqualified command";
        }
    }
}
