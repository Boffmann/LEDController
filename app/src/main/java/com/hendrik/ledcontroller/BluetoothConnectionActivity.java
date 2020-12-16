package com.hendrik.ledcontroller;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hendrik.ledcontroller.Bluetooth.BTService.OnConnected;
import com.hendrik.ledcontroller.Utils.ScreenResolution;
import com.hendrik.ledcontroller.Utils.Settings;

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

    private Button connectToLastButton = null;
    private ProgressBar progressSpinner = null;
    private TextView progressTextView = null;
    private TextView versionTextView = null;
    private boolean mIsConnecting = false;

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // Disable back button
    }

    protected void onBTConnected() {
        checkSavedDevice();
    }

    /**
     * Create the menu in upper right corner
     * @param menu Menu to create
     * @return true on success false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_menu_bt_activity, menu);

        return true;
    }

    /**
     * Called when an menu option is selected
     * @param item the item that is selected
     * @return true if handled, false otherwise
     */
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.new_device:

                startAddDeviceActivity();
                return true;

            default:

                return super.onOptionsItemSelected(item);
        }

    }


//ENDREGION ACTIVITY LIFECYCLE

// REGION INIT

    /**
     * Check if device is saved where app can connect to. Adjusts "connect to last button" accordingly
     */
    private void checkSavedDevice() {
        SharedPreferences sharedPref = Settings.getSharedPreferences(getApplicationContext());
        final String macAddress = sharedPref.getString(Settings.DEVICE_MAC, Settings.getDefault(Settings.DEVICE_MAC));
        final String deviceName = sharedPref.getString(Settings.DEVICE_NAME, Settings.getDefault(Settings.DEVICE_MAC));

        // Get saved Mac address, check if device is currently reachable. If not, start background
        // thread to permanently check if device becomes reachable. Adapt view based on result.
        if (macAddress != null && !macAddress.equals("")) {
            ArrayList<BluetoothDevice> pairedDevices = QueryPairedDevices();
            boolean savedDevicePaired = false;
            for (BluetoothDevice device : pairedDevices) {
                if (device.getAddress().equals(macAddress)) {
                    savedDevicePaired = true;
                }
            }
            if (savedDevicePaired) {
                connectToLastButton.setBackground(getRoundedDrawableWithColor(Color.GREEN));
                connectToLastButton.setText(String.format(getResources().getString(R.string.ConnectToLast), deviceName));

                connectToLastButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_DOWN) {
                            connectToLastButton.setBackground(getRoundedDrawableWithColor(Color.DKGRAY));
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            connectToLastButton.setBackground(getRoundedDrawableWithColor(Color.GREEN));
                        }
                        return false;
                    }
                });

                connectToLastButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (mIsConnecting) {
                            return;
                        }
                        mIsConnecting = true;
                        progressSpinner.setVisibility(View.VISIBLE);
                        progressTextView.setVisibility(View.VISIBLE);

                        connectToDevice(macAddress, new OnConnected() {
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
                                mIsConnecting = false;
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

    /**
     * Position the views in this activity based on the devices screen resolution
     */
    private void positionViews() {
        int screenWidth = ScreenResolution.getScreenWidth(this);
        int screenHeight = ScreenResolution.getScreenHeight(this);

        progressSpinner.setY((int)(screenHeight / 12.0));

        progressTextView.setY((int)(screenHeight / 6.0));

        connectToLastButton.getLayoutParams().width = ((int)(screenWidth * 0.9));
        connectToLastButton.getLayoutParams().height = ((int)(screenHeight * 0.4));
    }

    /**
     * Setup activitie's layout
     */
    private void setupLayout() {
        setContentView(R.layout.bt_activity_view);

        progressSpinner = findViewById(R.id.progressBar_cyclic);
        progressSpinner.setVisibility(View.INVISIBLE);

        progressTextView = findViewById(R.id.progress_text);
        progressTextView.setVisibility(View.INVISIBLE);

        versionTextView = findViewById(R.id.version_text);

        connectToLastButton = findViewById(R.id.connect_to_last_button);

        positionViews();
    }

// ENDREGION INIT

    /**
     * Create a rounded drawable with a certain color
     * @param color color to create drawable in
     * @return the final rounded drawable
     */
    private Drawable getRoundedDrawableWithColor(int color) {
        Drawable roundDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.roundedbutton);
        roundDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

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
