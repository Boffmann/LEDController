package com.hendrik.ledcontroller.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Class to handle bluetooth connecting
 * @author Hendrik Tjabben
 */

public class BTConnector {

//CONSTANTS

    /** Class TAG */
    private final static String TAG = "BluetoothConnector";

//END CONSTANTS

//MEMBER

    private BluetoothAdapter mBluetoothAdapter;

//END MEMBER


    /**
     * Default constructor
     * @param bluetoothAdapter the device's bluetooth adapter
     */
    public BTConnector(final BluetoothAdapter bluetoothAdapter) {
        mBluetoothAdapter = bluetoothAdapter;
    }







}
