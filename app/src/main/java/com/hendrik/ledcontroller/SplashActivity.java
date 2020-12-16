package com.hendrik.ledcontroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hendrik.ledcontroller.Bluetooth.BTService;
import com.hendrik.ledcontroller.Utils.Settings;

/**
 * Activity to make initial checks and setups
 */
public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check bluetooth functionality before running the app
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        Settings.initSettings(getApplicationContext());

        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_CODE_ENABLE_BT);
        } else {
            startApplication();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_CODE_ENABLE_BT:
                if (resultCode == Activity.RESULT_CANCELED) {
                    finish();
                    System.exit(0);
                }
                startApplication();
                break;
        }
    }

    private void startApplication() {
        // Start Bluetooth Service
        Intent intent = new Intent(this, BTService.class);
        startService(intent);

        intent = new Intent(this, BluetoothConnectionActivity.class);
        startActivity(intent);
    }

}