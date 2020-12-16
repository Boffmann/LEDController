package com.hendrik.ledcontroller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hendrik.ledcontroller.Bluetooth.BTService;
import com.hendrik.ledcontroller.Bluetooth.Command.BTPackage;
import com.hendrik.ledcontroller.Utils.ColorPicker;

/**
 * Class representing a remote to control the LED lights
 */
public class MainMenu extends BaseBTActivity {

//CONSTANTS

    /** Class TAG */
    private static final String TAG = "MainMenu";

//END CONSTANTS

//MEMBER

    private TextView mBrightnessSeekBarValueView = null;

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
        unbindService(mBTServiceConnection);
    }

    @Override
    public void onBackPressed() {
        // Disable back button
    }

    @Override
    protected void onBTConnected() {
        // DO nothing
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

        inflater.inflate(R.menu.main_menu_main_activity, menu);

        return true;
    }

    /**
     * Called when an menu option is selected
     * @param item the item that is selected
     * @return true if handled, false otherwise
     */
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.settings:

                startSettingsActivity();
                return true;

            default:

                return super.onOptionsItemSelected(item);
        }

    }

//ENDREGION ACTIVITY LIFECYCLE

//REGION INIT

    /**
     * Sets up the Layout
     */
    private void setupLayout() {

        setContentView(R.layout.activity_main_menu);

        Button mOnButton = findViewById(R.id.button_on);
        Button mOffButton = findViewById(R.id.button_off);
        Button mDisconnectButton = findViewById(R.id.button_dissconnect);
        SeekBar mBrightnessSeekBar = findViewById(R.id.brightness_seekbar);
        mBrightnessSeekBarValueView = findViewById(R.id.brightness_seekbar_value);
        ColorPicker mColorPickerView = findViewById(R.id.colorPickerMainMenu);
        mBrightnessSeekBarValueView.setText(mBrightnessSeekBar.getProgress()+"%");

        mOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTService.write(new BTPackage(BTPackage.PackageType.ONOFF, (byte)1 ));
            }
        });

        mOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTService.write(new BTPackage(BTPackage.PackageType.ONOFF, (byte)0 ));
            }
        });

        mBrightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mBrightnessSeekBarValueView.setText(seekBar.getProgress()+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                BTService.write(new BTPackage(BTPackage.PackageType.BRIGHTNESS, (byte) seekBar.getProgress()));
            }
        });

        mDisconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBTService.cancelConnection();
                startBTConnectionActivity();
            }
        });

        // Send color value to led lights when a color is selected
        mColorPickerView.setOnClickListener(new ColorPicker.OnClickListener() {
            @Override
            public void onClick(int color) {
                byte colorArray[] = new byte[3];
                colorArray[0] = (byte) Color.red(color);
                colorArray[1] = (byte) Color.green(color);
                colorArray[2] = (byte) Color.blue(color);
                BTService.write(new BTPackage(BTPackage.PackageType.COLOR, colorArray));
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

    /**
     * Calls intent for SettingsActivity
     */
    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


}
