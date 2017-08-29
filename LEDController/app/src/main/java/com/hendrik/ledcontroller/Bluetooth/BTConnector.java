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

    /**
     * Connect to a certain bluetooth device
     * @param bluetoothDevice the device to connect to
     * @param runable callback to execute after successfull connection
     */
    public void connect(final BluetoothDevice bluetoothDevice, final IBTRunnable runable) {
        BluetoothConnectThread connectThread = new BluetoothConnectThread(mBluetoothAdapter, bluetoothDevice, runable);
        connectThread.start();
    }



    private class BluetoothConnectThread extends Thread {

        /** Socket of the established connection */
        private final BluetoothSocket mBluetoothSocket;
        /** The device to connect to */
        private final BluetoothDevice mBluetoothDevice;
        /** The device's bluetooth adapter */
        private final BluetoothAdapter mBluetoothAdapter;
        /** Runnable contains code to execute after successfull connection */
        private IBTRunnable mBTRunnable;


        /**
         * Constructor
         * @param adapter The device's bluetooth adapter
         * @param device The device to connect to
         * @param runable Runnable contains code to execute after successfull connection
         */
        public BluetoothConnectThread(final BluetoothAdapter adapter, final BluetoothDevice device, final IBTRunnable runable) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mBluetoothAdapter = adapter;
            mBluetoothDevice = device;
            mBTRunnable = runable;

            try {
                // Fetch possible uuids
                ParcelUuid[] idArray = mBluetoothDevice.getUuids();
                UUID uuid = null;
                if (idArray != null) {
                    uuid = UUID.fromString(idArray[0].toString());
                } else {
                    uuid = UUID.randomUUID();
                    Log.w(TAG, "No uuid possible. Use random uuid");
                }

                tmp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mBluetoothSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();
            if (mBluetoothSocket == null) {
                return;
            }

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mBluetoothSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                Log.e(TAG, "Unable to connect; close the socket and return. " + connectException.getMessage());
                try {
                    mBluetoothSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            mBTRunnable.BTCallback(mBluetoothSocket);
        }

        /**
         * Closes the client socket and causes the thread to finish.
         */
        public void cancel() {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }


    }

}
