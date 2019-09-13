package com.hendrik.ledcontroller.Bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

import com.hendrik.ledcontroller.BTApplication;
import com.hendrik.ledcontroller.Bluetooth.Command.BTPackage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Service running in the background and providing bluetooth functionality
 * @author Hendrik Tjabben
 */

public class BTService extends Service {

//CONSTANTS

    /** Class TAG */
    private static final String TAG = "BTService";

    /** we're doing nothing */
    public static final int STATE_NONE = 0;

//END CONSTANTS

//REGION MEMBER

    /** The devices bluetooth adapter */
    private static BluetoothAdapter mBluetoothAdapter = null;
    /** Output stream to write data over bluetooth */
    private static OutputStream mOutputStream;
    /** Input stream to receive data over bluetooth */
    private static InputStream mInputStream;
    /** handler that gets info from Bluetooth service */
    private Handler mHandler;
    /** The connection state */
    public static int mState = STATE_NONE;
    /** BT Binder */
    private final Binder mBinder = new LocalBinder();
    /** Connect Thread */
    private ConnectThread mConnectThread = null;


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
        Log.d(TAG, "onBind");
        mHandler = ((BTApplication) getApplication()).getHandler();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

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
            paired.addAll(pairedDevices);
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
        } else {
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
        }
    }

    /**
     * Create a connection to a bluetooth device
     * @param btDevice The Bluetooth device to connect to
     * @return
     */
    public void connect(final BluetoothDevice btDevice, final OnConnected connectCallback) {

        // Reset all streams and socket.
        this.cancelConnection();

        mConnectThread = new ConnectThread(btDevice, new ManageConnectedSocket() {
            @Override
            public void manage(BluetoothSocket bluetoothSocket) {
                try {
                    mOutputStream = bluetoothSocket.getOutputStream();
                    mInputStream = bluetoothSocket.getInputStream();
                    connectCallback.onConnected(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    connectCallback.onConnected(false);
                }
            }
            @Override
            public void onFailure() {
                connectCallback.onConnected(false);
            }
        });

        mConnectThread.start();
    }

    /**
     * Connect to a certain bluetooth device
     * @param bluetoothDevice the device to connect to
     */
    public void connectToDevice(final BluetoothDevice bluetoothDevice, final OnConnected onConnected) {
        if (mBluetoothAdapter.isDiscovering()){
            // Cancel discovery just to make sure. Discovering is a resource consuming process
            mBluetoothAdapter.cancelDiscovery();
        }
        this.connect(bluetoothDevice, onConnected);
    }

    public void connectToDevice(final String macAddress, final OnConnected onConnected) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAddress);
        this.connectToDevice(device, onConnected);
    }

    public static void write(final BTPackage command, final int retries) {
        byte[] data = command.getData();
        for (int i = 0; i < data.length; i++) {
            System.out.println(data[i]);
        }
        try {
            mOutputStream.write(data, 0, data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write a byte stream to the connected BT Device
     * @param command The unserialized command to transmit to the BTDevice
     */
    public static void write(final BTPackage command) {
        write(command, 0);
    }

    public static byte[] read() {
        byte[] inputBuffer = new byte[1];
        try {
            int numBytes = mInputStream.read(inputBuffer);
            if (numBytes > 0) {
                return inputBuffer;
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.d(TAG, "Input stream was disconnected", e);
            return null;
        }
    }

    /**
     * Cancels the BT connection
     */
    public void cancelConnection() {
        if (mInputStream != null) {
            try {mInputStream.close();} catch (Exception e) {}
            mInputStream = null;
        }

        if (mOutputStream != null) {
            try {mOutputStream.close();} catch (Exception e) {}
            mOutputStream = null;
        }

        if (mConnectThread != null) {
            mConnectThread.cancel();
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

    private class ConnectThread extends Thread {
        private BluetoothSocket mBluetoothSocket;
        private final BluetoothDevice mDevice;
        private final ManageConnectedSocket mManageConnectedSocket;

        public ConnectThread(BluetoothDevice btDevice, final ManageConnectedSocket manageConnectedSocket) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mDevice = btDevice;
            mManageConnectedSocket = manageConnectedSocket;

            UUID uuid = null;
            try {
                ParcelUuid[] uuids = btDevice.getUuids();
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                if (uuids != null && uuids.length > 0) {
                    uuid = UUID.fromString(uuids[0].toString());
                } else {
                    Log.w(TAG, "No uuid possible. Use random uuid");
                    uuid = UUID.randomUUID();
                }
                tmp = btDevice.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
                manageConnectedSocket.onFailure();
                return;
            }
            mBluetoothSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mBluetoothSocket.connect();
            } catch (IOException e) {
                Log.e(TAG, "Unable to connect; close the socket and return. " + e.getMessage());
                // Unable to connect; close the socket and return.
                try {
                    Log.e("","trying fallback...");

                    mBluetoothSocket =(BluetoothSocket) mDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mDevice,1);
                    mBluetoothSocket.connect();
                    Log.e("","Connected");
                }
                catch (Exception e2) {
                    Log.e("", "Couldn't establish Bluetooth connection!");
                    try {
                        mBluetoothSocket.close();
                    } catch (IOException ex) {
                        Log.e(TAG, "Could not close the client socket", ex);
                    }

                    mBluetoothSocket = null;
                    mManageConnectedSocket.onFailure();
                    return;
                }
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            mManageConnectedSocket.manage(mBluetoothSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    public interface OnConnected {
        void onConnected(boolean success);
    }

    private interface ManageConnectedSocket {
        void manage(final BluetoothSocket bluetoothSocket);
        void onFailure();
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
     * Local Binder class
     */
    public class LocalBinder extends Binder {
        public BTService getService() {
            return BTService.this;
        }
    }

//ENDREGION CLASSES

}
