package com.hendrik.ledcontroller.Bluetooth;

/**
 * Created by hendr on 09.03.2018.
 */

public class BTTransmitProtocol {

    public enum ActionType {
        ON,
        OFF,
        BRIGHTNESS,
        COLOR;

        public static int toAscii(final ActionType type) {
            switch (type) {
                case ON:
                    return 48;
                case OFF:
                    return 49;
                case BRIGHTNESS:
                    return 50;
                case COLOR:
                    return 51;
                default:
                    return 0;
            }
        }
    }

    public static boolean isAnswerForCommand(final byte[] answer, final BTCommand command) {
        if (answer[0] == ActionType.toAscii(command.getAction())) {
            return true;
        }
        return false;
    }

}
