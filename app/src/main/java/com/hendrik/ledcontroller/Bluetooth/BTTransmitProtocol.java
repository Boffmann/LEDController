package com.hendrik.ledcontroller.Bluetooth;

import com.hendrik.ledcontroller.Bluetooth.Command.BTCommand;

import java.util.ArrayList;

/**
 * Created by hendr on 09.03.2018.
 */

public class BTTransmitProtocol {

    private final static byte[] valueSizeFlags = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20};

    public enum ActionType {
        ON,
        OFF,
        BRIGHTNESS,
        COLOR
    }

    private static byte actionTypeToByte(final ActionType type) {
        switch (type) {
            case ON:
                return 0x01;
            case OFF:
                return 0x02;
            case BRIGHTNESS:
                return 0x04;
            case COLOR:
                return 0x08;
            default:
                return 0x00;
        }
    }

    public static boolean isAnswerForCommand(final byte[] answer, final BTCommand command) {
        if (answer[0] == actionTypeToByte(command.getAction())) {
            return true;
        }
        return false;
    }

    public static boolean isUnaryAction(final ActionType action) {
        switch (action) {
            case ON:
            case OFF:
                return true;
            case BRIGHTNESS:
            case COLOR:
                return false;
            default:
                return false;
        }
    }

    public static boolean isBinaryAction(final ActionType action) {
        switch (action) {
            case ON:
            case OFF:
            case COLOR:
                return false;
            case BRIGHTNESS:
                return true;
            default:
                return false;
        }
    }

    public static byte[] getMetadataForCommand(final BTCommand command) {
        byte[] metadata = {0x00, 0x00};

        // Set bit flag for every value greater than 127, because somehow most valueable bit is flipped during transmission
        for (int i = 0; i < command.getValues().size(); i++) {
            if (command.getValues().get(i) > 127) {
                metadata[0] |= valueSizeFlags[i];
            }
        }

        // metadata[1] is the type of the command
        metadata[1] = actionTypeToByte(command.getAction());

        return metadata;
    }

}
