package com.hendrik.ledcontroller.Bluetooth;

/**
 * Created by hendr on 09.03.2018.
 */

public class BTTransmitProtocol {

    public enum ActionType {
        ON,
        OFF,
        BRIGHTNESS;

        public static int toAscii(final ActionType type) {
            switch (type) {
                case ON:
                    return 48;
                case OFF:
                    return 49;
                case BRIGHTNESS:
                    return 50;
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
