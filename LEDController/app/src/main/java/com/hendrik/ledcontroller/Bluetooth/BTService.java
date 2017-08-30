package com.hendrik.ledcontroller.Bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;

import com.hendrik.ledcontroller.BTApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * @author Hendrik Tjabben
 */

public class BTService extends Service {

//CONSTANTS

    /** Class TAG */
    private static final String TAG = "BTService";

    /** we're doing nothing */
    public static final int STATE_NONE = 0;
    /** now listening for incoming */
    public static final int STATE_LISTEN = 1;
    /** now initiating an outgoing */
    public static final int STATE_CONNECTING = 2;
    /** now connected to a remote */
    public static final int STATE_CONNECTED = 3;

//END CONSTANTS

//REGION MEMBER

    /** The devices bluetooth adapter */
    private static BluetoothAdapter mBluetoothAdapter;
    /** Thread to connect to a BT Device */
    private BluetoothConnectThread mConnectThread;
    /** That that manages a BT Connection */
    private static ConnectedThread mConnectedThread;
    /** handler that gets info from Bluetooth service */
    private Handler mHandler;
    /** The connection state */
    public static int mState = STATE_NONE;
    /** BT Binder */
    private final Binder mBinder = new LocalBinder();


//ENDREGION MEMBER

//REGION CONSTRUCTOR

    /**
     * Default constructor
     */
    public BTService() {

    }

//ENDREGION CONSTRUCTOR

//REGION LIFECYCLE

    @Override
    public void onCreate() {
        Log.d(TAG, "Service started");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        mHandler = ((BTApplication) getApplication()).getHandler();
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //TODO stopservice
        /*String stopservice = intent.getStringExtra("stopservice");
        if (stopservice != null && stopservice.length() > 0) {
            stopSelf();
        }*/
        return START_STICKY;

    }

//ENDREGION LIFECYCLE

    /**
     * Sets the connection state
     * @param state the connection state to set
     */
    private void setState(int state) {
        mState = state;
        if (mHandler != null) {
           //TODO mHandler.obtainMessage(AbstractActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
        }
    }

    /**
     * Makes the device discoverable for other bluetooth devices
     * @param time defines how long the device is discoverable
     * @param context the context
     */
    public void setDiscoverable(final int time, final Context context) {
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
        context.startActivity(discoverableIntent);
    }

    /**
     * Querying previously pared devices
     */
    public ArrayList<BluetoothDevice> QueryPairedDevices() {

        ArrayList<BluetoothDevice> paired = new ArrayList<>();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                paired.add(device);
            }
        }

        return paired;
    }

    /**
     * Discover unpaired Devices
     */
    public void BTDiscovery() {
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
        }
        if(!mBluetoothAdapter.isDiscovering()){

            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
        }
    }

    /**
     * Connect to a certain bluetooth device
     * @param bluetoothDevice the device to connect to
     */
    public void connectToDevice(final BluetoothDevice bluetoothDevice) {
        //TODO Handle state connecting
       /* if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }*/

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectThread = new BluetoothConnectThread(bluetoothDevice);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }


    /**
     * Method that is called after a connection is established
     * @param socket
     */
    private synchronized void connected(BluetoothSocket socket) {
        //TODO cancel thread that completed the connection
        // Cancel the thread that completed the connection
       /* if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }*/

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Message msg =
        // mHandler.obtainMessage(AbstractActivity.MESSAGE_DEVICE_NAME);
        // Bundle bundle = new Bundle();
        // bundle.putString(AbstractActivity.DEVICE_NAME, "p25");
        // msg.setData(bundle);
        // mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);

    }

    private static Object obj = new Object();

    /**
     * Write a byte stream to the connected BT Device
     * @param out
     */
    public static void write(byte[] out) {
        //TODO Better and safer write
        // Create temporary object
        /*ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (obj) {
            if (mState != STATE_CONNECTED)
                return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);*/

        mConnectedThread.write(out);
    }

    /**
     * Cancels the BT connection
     */
    public static void cancelConnection() {
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
        } else {
            Log.w(TAG, "Connection is not established. Cancel disconnection");
        }
    }


    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                //TODO Show dialog that permission is missing
                Log.d(TAG, "Bluetooth Permission missing");
                //this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }


//REGION CLASSES


    /**
     * Class to establish a BT Connection
     */
    private class BluetoothConnectThread extends Thread {

        /** Socket of the established connection */
        private final BluetoothSocket mBluetoothSocket;
        /** The device to connect to */
        private final BluetoothDevice mBluetoothDevice;


        /**
         * Constructor
         * @param device The device to connect to
         */
        public BluetoothConnectThread(final BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mBluetoothDevice = device;

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

        /**
         * Run method of Thread
         */
        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();
            if (mBluetoothSocket == null) {
                Log.e(TAG, "Bluetooth Socket is null");
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
            connected(mBluetoothSocket);
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

    /**
     * Defines several constants used when transmitting messages between the
     * service and the UI.
     */
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
    }

    /**
     * Class to manaage an established connection
     */
    private class ConnectedThread extends Thread {
        /** The BT Socket of the connection*/
        private final BluetoothSocket mBluetoothSocket;
        /** Input stream from the BT Device */
        private final InputStream mInStream;
        /** Output stream to sent to the BT Device */
        private final OutputStream mOutStream;
        /** Buffer to store for the stream */
        private byte[] mmBuffer;

        /**
         * Constructor
         * @param socket the connection socket
         */
        public ConnectedThread(BluetoothSocket socket) {
            mBluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mInStream = tmpIn;
            mOutStream = tmpOut;
        }

        /**
         * Run method of thread
         */
        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            /*while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }*/
        }


        /**
         * Call this from the main activity to send data to the remote device.
         * @param bytes the bytestrean to write
         */
        public void write(byte[] bytes) {
            try {
                mOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }

        /**
         * Call this method from the main activity to shut down the connection.
         */
        public void cancel() {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    /**
     * Local Binder class
     */
    public class LocalBinder extends Binder {
        public BTService getService() {
            return BTService.this;
        }
    }

//ENDREGION CLASSES

}
