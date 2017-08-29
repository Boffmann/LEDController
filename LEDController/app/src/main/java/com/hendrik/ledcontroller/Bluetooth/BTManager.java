package com.hendrik.ledcontroller.Bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Set;

/**
 * Class that manages bluetooth connection
 * @author Hendrik Tjabben
 */

public class BTManager {

//CONSTANTS

    /** Class string*/
    private final static String TAG = "BTManager";

//END CONSTANTS

//MEMBER

    /** The devices bluetooth adapter */
    private static BluetoothAdapter mBluetoothAdapter;
    /** The connector used to establish a bluetooth connection */
    private BTConnector mBTConnector;
    /** The parent activity */
    private Activity mParentActivity;

//END MEMBER

    /**
     * Constructor
     * @param bluetoothAdapter the device's bluetooth adapter
     */
    public BTManager(final BluetoothAdapter bluetoothAdapter, final Activity parentActivity) {
        mBluetoothAdapter = bluetoothAdapter;
        mParentActivity = parentActivity;
        mBTConnector = new BTConnector(mBluetoothAdapter);
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
     * Connect
     */
    public void connect(final BluetoothDevice bluetoothDevice, final IBTRunnable runnable) {
        mBTConnector.connect(bluetoothDevice, runnable);
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
            int permissionCheck = mParentActivity.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += mParentActivity.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                mParentActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }


}
