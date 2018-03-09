package com.hendrik.ledcontroller.Bluetooth;

/**
 * Created by hendr on 09.03.2018.
 */

public class BTTransmitProtocol {

    public enum ActionType {
        ON,
        OFF,
        BRIGHTNESS
    }

    public static boolean isAnswerForCommand(final byte[] answer, final BTCommand command) {
        switch (command.getAction()) {
            case ON:
                if (answer[0] == 48) {
                    return true;
                }
                return false;
            case OFF:
                if (answer[0] == 49) {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

}
