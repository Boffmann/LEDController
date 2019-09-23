package com.hendrik.ledcontroller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hendrik.ledcontroller.Bluetooth.BTService;
import com.hendrik.ledcontroller.Bluetooth.Command.BTPackage;
import com.hendrik.ledcontroller.Utils.ColorPicker;


public class MainMenu extends BaseBTActivity {

//CONSTANTS

    /** Class TAG */
    private static final String TAG = "MainMenu";

//END CONSTANTS

//MEMBER

    private Button mOnButton = null;
    private Button mOffButton = null;
    private Button mDisconnectButton = null;
    private SeekBar mBrightnessSeekBar = null;
    private TextView mBrightnessSeekBarValueView = null;
    private ColorPicker mColorPickerView = null;

    @Override
    protected void onBTConnected() {
        // DO nothing
    }

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

//ENDREGION ACTIVITY LIFECYCLE

//REGION INIT

    /**
     * Sets up the Layout
     */
    private void setupLayout() {

        setContentView(R.layout.activity_main_menu);

        mOnButton = findViewById(R.id.button_on);
        mOffButton = findViewById(R.id.button_off);
        mDisconnectButton = findViewById(R.id.button_dissconnect);
        mBrightnessSeekBar = findViewById(R.id.brightness_seekbar);
        mBrightnessSeekBarValueView = findViewById(R.id.brightness_seekbar_value);
        mColorPickerView = findViewById(R.id.colorPickerMainMenu);
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
                BTService.write(new BTPackage(BTPackage.PackageType.ONOFF, (byte)0));
                mBTService.cancelConnection();
                startBTConnectionActivity();
            }
        });
    }

//ENDREGION INIT

    public void onColorButtonClicked(View v) {
        Drawable background = v.getBackground();
        if (background instanceof ColorDrawable) {
            int selectedColor = ((ColorDrawable) background).getColor();
            byte color[] = new byte[3];
            color[0] = (byte) Color.red(selectedColor);
            color[1] = (byte) Color.green(selectedColor);
            color[2] = (byte) Color.blue(selectedColor);
            BTService.write(new BTPackage(BTPackage.PackageType.COLOR, color));
        }
    }

    /**
     * Starts the BluetoothConnectionActivity
     */
    private void startBTConnectionActivity() {
        Intent intent = new Intent(this, BluetoothConnectionActivity.class);
        startActivity(intent);
    }


}
