package com.hendrik.ledcontroller.Bluetooth.Command;

import com.hendrik.ledcontroller.Bluetooth.BTTransmitProtocol;

import java.util.ArrayList;

/**
 * Created by hendrik tjabben on 12.03.2018.
 */

public abstract class BTCommand {

    /**
     * Returns metadata bytes for this command
     * @return metadata bytes
     */
    public ArrayList<Byte> getMetaData() {
        byte[] metadata = BTTransmitProtocol.getMetadataForCommand(this);
        ArrayList<Byte> result = new ArrayList<>();
        result.add(metadata[0]);
        result.add(metadata[1]);

        return result;
    }

    /**
     * Get the action type for this command
     * @return the action type for this command
     */
    public abstract BTTransmitProtocol.ActionType getAction();

    /**
     * Get the value list for this command
     * @return value list for this command
     */
    public abstract ArrayList<Integer> getValues();
}
