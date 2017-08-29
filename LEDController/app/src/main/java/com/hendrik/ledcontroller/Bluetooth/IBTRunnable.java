package com.hendrik.ledcontroller.Bluetooth;

import android.bluetooth.BluetoothSocket;

/**
 * @author Hendrik Tjabben
 */

public interface IBTRunnable {

    /**
     * Run method
     */
    void BTCallback(final BluetoothSocket socket);
}
