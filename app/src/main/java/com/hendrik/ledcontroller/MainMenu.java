package com.hendrik.ledcontroller;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hendrik.ledcontroller.Bluetooth.BTCommand;
import com.hendrik.ledcontroller.Bluetooth.BTService;
import com.hendrik.ledcontroller.Bluetooth.BTTransmitProtocol;


public class MainMenu extends BaseActivity {

//CONSTANTS

    /** Class TAG */
    private static final String TAG = "MainMenu";

//END CONSTANTS

//MEMBER


    /** The BTService */
    private BTService mBTService = null;

    /** The service connection to talk to the Bluetooth service */
    private ServiceConnection mBTServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e(TAG, "OnServiceConnected");

            BTService.LocalBinder binder = (BTService.LocalBinder)iBinder;

            mBTService = binder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBTService = null;
        }
    };

//ENDMEMBER


//REGION ACTIVITY LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent bindServiceIntent = new Intent(this, BTService.class);
        bindService(bindServiceIntent, mBTServiceConnection, BTService.BIND_AUTO_CREATE);

        setupLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//ENDREGION ACTIVITY LIFECYCLE

//REGION INIT

    /**
     * Sets up the Layout
     */
    private void setupLayout() {

        setContentView(R.layout.activity_main_menu);

        Button onButton = (Button)findViewById(R.id.button_on);
        Button offButton = (Button)findViewById(R.id.button_off);
        Button disconnectButton = (Button)findViewById(R.id.button_dissconnect);

        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] on = {0x6F, 0x6E};
                BTService.write(new BTCommand(BTTransmitProtocol.ActionType.ON, 1));
            }
        });

        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] off = {0x6F, 0x66, 0x66};
                BTService.write(off);
            }
        });

        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBTService.cancelConnection();
                startBTConnectionActivity();
            }
        });
    }

//ENDREGION INIT

    /**
     * Starts the BluetoothConnectionActivity
     */
    private void startBTConnectionActivity() {
        Intent intent = new Intent(this, BluetoothConnectionActivity.class);
        startActivity(intent);
    }


}
