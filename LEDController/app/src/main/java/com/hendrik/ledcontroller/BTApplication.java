package com.hendrik.ledcontroller;

import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.hendrik.ledcontroller.Bluetooth.BTService;


/**
 * @author Hendrik Tjabben
 */

public class BTApplication extends Application {

    private final static String TAG = "BTApplication";

    private BTService mBluetoothService;


    public void createBTService(final BluetoothSocket bluetoothSocket) {


        mBluetoothService = new BTService(bluetoothSocket);
    }

    public BTService getBTService() {
        if(mBluetoothService == null) {
            Log.e(TAG, "No BTService created yet");
            return null;
        }
        return mBluetoothService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
