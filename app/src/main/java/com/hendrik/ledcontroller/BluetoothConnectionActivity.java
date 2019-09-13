package com.hendrik.ledcontroller;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hendrik.ledcontroller.Bluetooth.BTService;
import com.hendrik.ledcontroller.DataStructure.DeviceArrayAdapter;

import java.util.ArrayList;


/**
 * Activity to search for BT Devices and connect.
 * Starting activity
 * @author Hendrik Tjabben
 */

public class BluetoothConnectionActivity extends BaseBTActivity {

    //CONSTANTS

    /** Class TAG */
    private static final String TAG = "BTConnectionActivity";

//END CONSTANTS

//MEMBER

    Button connectToLastButton = null;
    ProgressBar progressSpinner = null;
    TextView progressTextView = null;

//ENDREGION MEMBER


//REGION ACTIVITY LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout();
    }

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

    @Override
    public void onBackPressed() {
        // Disable back button
    }

    protected void onBTConnected() {
        checkSavedDevice();
    }


//ENDREGION ACTIVITY LIFECYCLE

// REGION INIT

    private void checkSavedDevice() {
        SharedPreferences sharedPref= getSharedPreferences("BTSettings", 0);
        final String macAddress = sharedPref.getString("mac_address", "");
        final String deviceName = sharedPref.getString("device_name", "");

        // Get saved Mac address, check if device is currently reachable. If not, start background
        // thread to permanently check if device becomes reachable. Adapt view based on result.
        if (macAddress != null && !macAddress.equals("")) {
            ArrayList<BluetoothDevice> pairedDevices = mBTService.QueryPairedDevices();
            boolean savedDevicePaired = false;
            for (BluetoothDevice device : pairedDevices) {
                if (device.getAddress().equals(macAddress)) {
                    savedDevicePaired = true;
                }
            }
            if (savedDevicePaired) {
                connectToLastButton.setBackground(getRoundedDrawableWithColor(Color.GREEN));
                connectToLastButton.setText(String.format(getResources().getString(R.string.ConnectToLast), deviceName));
                connectToLastButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        progressSpinner.setVisibility(View.VISIBLE);
                        progressTextView.setVisibility(View.VISIBLE);

                        mBTService.connectToDevice(macAddress, new BTService.OnConnected() {
                            @Override
                            public void onConnected(boolean success) {
                                if (success) {
                                    startMainMenuActivity();
                                } else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.FailedToConnect), Toast.LENGTH_LONG);
                                            toast.show();

                                            progressSpinner.setVisibility(View.INVISIBLE);
                                            progressTextView.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            } else {
                connectToLastButton.setBackground(getRoundedDrawableWithColor(Color.RED));
                connectToLastButton.setClickable(false);
                connectToLastButton.setText(R.string.NoDeviceSaved);
            }

        } else {
            connectToLastButton.setBackground(getRoundedDrawableWithColor(Color.GRAY));
            connectToLastButton.setText(R.string.NoDeviceSaved);
            connectToLastButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startAddDeviceActivity();
                }
            });
        }
    }

    private void setupLayout() {
        setContentView(R.layout.bt_activity_view);

        progressSpinner = findViewById(R.id.progressBar_cyclic);
        progressSpinner.setVisibility(View.INVISIBLE);

        progressTextView = findViewById(R.id.progress_text);
        progressTextView.setVisibility(View.INVISIBLE);

        connectToLastButton = findViewById(R.id.connect_to_last_button);

        Button addDeviceButton = findViewById(R.id.add_device_button);
        addDeviceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startAddDeviceActivity();
            }
        });
    }

// ENDREGION INIT

    private Drawable getRoundedDrawableWithColor(int color) {
        Drawable roundDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.roundedbutton);
        roundDrawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);

        return roundDrawable;
    }

    /**
     * Calls intent for MainMenu
     */
    private void startMainMenuActivity() {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    /**
     * Calls intent for AddDeviceActivity
     */
    private void startAddDeviceActivity() {
        Intent intent = new Intent(this, AddDeviceActivity.class);
        startActivity(intent);
    }

}
