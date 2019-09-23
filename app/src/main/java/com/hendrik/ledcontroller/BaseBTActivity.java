package com.hendrik.ledcontroller;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hendrik.ledcontroller.Bluetooth.BTService;

/**
 * @author Hendrik Tjabben
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

    @Override
    protected void onStart() {
        super.onStart();
        Intent bindServiceIntent = new Intent(this, BTService.class);
        bindService(bindServiceIntent, mBTServiceConnection, BTService.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(mBTServiceConnection);
        Intent intent = new Intent(this, BTService.class);
        stopService(intent);
    }

// ENDREGION ACTIVITY WORKFLOW

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
}
