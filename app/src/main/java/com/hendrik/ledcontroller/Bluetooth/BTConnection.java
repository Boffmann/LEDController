package com.hendrik.ledcontroller.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Class representing a bluetooth connection
 * Created by hendr on 08.03.2018.
 */

public class BTConnection {

    private static final String TAG = "BTConnection";

    /** The device the current connection is established to */
    private BluetoothDevice mConnectedDevice;
    /** Socket of the established connection */
    private BluetoothSocket mBluetoothSocket;
    /** Thread running in background and making IO to the device */
    BtIOThread mIOThread;

    public BTConnection() {

    }

    /**
     * Create a connection to a bluetooth device
     * @param btDevice The Bluetooth device to connect to
     * @param btAdapter The bluetooth adapter of the device that initiates the connection
     * @return
     */
    public boolean connect(final BluetoothDevice btDevice, final BluetoothAdapter btAdapter) {


        // TODO Cancel any thread currently running a connection

        UUID uuid = null;
        try {
            // Fetch possible uuids
            ParcelUuid[] uuids = btDevice.getUuids();
            if (uuids != null && uuids.length > 0) {
                uuid = UUID.fromString(uuids[0].toString());
            } else {
                Log.w(TAG, "No uuid possible. Use random uuid");
                uuid = UUID.randomUUID();
            }
            mBluetoothSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            mBluetoothSocket = null;
            mConnectedDevice = null;
            Log.e(TAG, "Failed to establish connection", e);
            return false;
        }

        // Cancel discovery because it otherwise slows down the connection.
        btAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mBluetoothSocket.connect();
            mIOThread = new BtIOThread(mBluetoothSocket.getOutputStream());
        } catch (IOException e) {
            mBluetoothSocket = null;
            mConnectedDevice = null;
            Log.e(TAG, "Unable to connect; close the socket and return. " + e.getMessage());

            try {
                mBluetoothSocket.close();
            } catch (IOException ex) {
                Log.e(TAG, "Could not close the client socket", ex);
            }

            return false;
        }
        mIOThread.start();
        mConnectedDevice = btDevice;

        return true;
    }

    public boolean disconnect() {
        return true;
    }

    public void write(byte[] bytes) {
        mIOThread.addIO(bytes);
    }
}
