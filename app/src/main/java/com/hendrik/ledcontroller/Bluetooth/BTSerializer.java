package com.hendrik.ledcontroller.Bluetooth;

import android.graphics.Color;
import android.util.Log;

/**
 * Created by hendr on 09.03.2018.
 */

public class BTSerializer {

    public BTSerializer() {

    }

    public static byte[] serialize(final BTCommand command) {
        switch (command.getAction()) {
            case ON:
                byte[] retOn = new byte[2];
                retOn[0] = 0x6F;
                retOn[1] = 0x6E;
                return retOn;
            case OFF:
                byte[] retOff = new byte[3];
                retOff[0] = 0x6F;
                retOff[1] = 0x66;
                retOff[2] = 0x66;
                return retOff;
            default:
                return null;
        }
    }
}
