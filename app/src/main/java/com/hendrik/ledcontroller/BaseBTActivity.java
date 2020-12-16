package com.hendrik.ledcontroller;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hendrik.ledcontroller.Bluetooth.BTService;
import com.hendrik.ledcontroller.Bluetooth.Command.BTPackage;

import java.util.ArrayList;

/**
 * @author Hendrik Tjabben
 */

/**
 * Base class for all Activities that require bluetooth support
 */
public abstract class BaseBTActivity extends AppCompatActivity {

//CONSTANTS

    /** Class TAG */
    private static final String TAG = "BaseBTActivity";

//END CONSTANTS

// REGION MEMBER

    /** The BTService */
    protected BTService mBTService = null;

// ENDREGION MEMBER

// REGION ACTIVITY WORKFLOW

    /**
     * Bind to bluetooth service
     */
    @Override
    protected void onStart() {
        super.onStart();
        Intent bindServiceIntent = new Intent(this, BTService.class);
        bindService(bindServiceIntent, mBTServiceConnection, BTService.BIND_AUTO_CREATE);
    }

    /**
     * Unbind from bluetooth service and stop it
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(mBTServiceConnection);
        Intent intent = new Intent(this, BTService.class);
        stopService(intent);
    }

// ENDREGION ACTIVITY WORKFLOW

    /**
     * Abstract method to call right after binding to bluetooth service
     */
    protected abstract void onBTConnected();

    /** The service connection to talk to the Bluetooth service */
    protected ServiceConnection mBTServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e(TAG, "OnServiceConnected");

            BTService.LocalBinder binder = (BTService.LocalBinder)iBinder;

            mBTService = binder.getService();

            onBTConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBTService = null;
        }
    };

    /**
     * Querying previously pared devices
     */
    protected ArrayList<BluetoothDevice> QueryPairedDevices() {
        return this.mBTService.QueryPairedDevices();
    }

    /**
     * Connect to a certain bluetooth device using its mac address
     * @param macAddress the mac address of the device to connect to
     * @param onConnected callback to notify if connecting was successful or not
     */
    protected void connectToDevice(final String macAddress, final BTService.OnConnected onConnected) {
        this.mBTService.connectToDevice(macAddress, onConnected);
    }
}
