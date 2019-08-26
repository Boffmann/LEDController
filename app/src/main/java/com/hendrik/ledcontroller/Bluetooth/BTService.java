package com.hendrik.ledcontroller.Bluetooth;

import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.hendrik.ledcontroller.BTApplication;
import com.hendrik.ledcontroller.Bluetooth.Command.BTCommand;
import com.hendrik.ledcontroller.Bluetooth.Command.BTUnaryCommand;

import java.util.ArrayList;
import java.util.Set;

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
    /** The currently active bluetooth connection */
    private static BTConnection mBTConnection;
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
     * Connect to a certain bluetooth device
     * @param bluetoothDevice the device to connect to
     */
    public void connectToDevice(final BluetoothDevice bluetoothDevice) {
        if (mBluetoothAdapter.isDiscovering()){
            // Cancel discovery just to make sure. Discovering is a resource consuming process
            mBluetoothAdapter.cancelDiscovery();
        }
        mBTConnection = new BTConnection();
        if (!mBTConnection.connect(bluetoothDevice, mBluetoothAdapter)) {
            // TODO Handle failed connection
            return;
        }
    }

    private static Object obj = new Object();

    /**
     * Write a byte stream to the connected BT Device
     * @param command The unserialized command to transmit to the BTDevice
     */
    public static void write(final BTCommand command) {
        mBTConnection.write(command);
    }

    /**
     * Cancels the BT connection
     */
    public void cancelConnection() {
        mBTConnection.disconnect();
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
