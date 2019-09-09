package com.hendrik.ledcontroller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hendrik.ledcontroller.Bluetooth.BTService;
import com.hendrik.ledcontroller.Bluetooth.Command.BTPackage;

import java.io.Console;


public class MainMenu extends BaseBTActivity {

//CONSTANTS

    /** Class TAG */
    private static final String TAG = "MainMenu";

//END CONSTANTS

//MEMBER

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

        Button onButton = findViewById(R.id.button_on);
        Button offButton = findViewById(R.id.button_off);
        Button disconnectButton = findViewById(R.id.button_dissconnect);
        SeekBar brightnessSeekBar = findViewById(R.id.brightness_seekbar);
        //ColorPickerView colorPicker = findViewById((R.id.color_picker_view));
        final TextView brightnessSeekBarValueView = findViewById(R.id.brightness_seekbar_value);
        brightnessSeekBarValueView.setText(brightnessSeekBar.getProgress()+"%");

        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTService.write(new BTPackage(BTPackage.PackageType.ONOFF, (byte)1 ));
            }
        });

        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTService.write(new BTPackage(BTPackage.PackageType.ONOFF, (byte)0 ));
            }
        });

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                brightnessSeekBarValueView.setText(seekBar.getProgress()+"%");
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

        /*colorPicker.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int selectedColor) {
                byte color[] = new byte[3];
                color[0] = (byte)Color.red(selectedColor);
                color[1] = (byte)Color.green(selectedColor);
                color[2] = (byte)Color.blue(selectedColor);
                BTService.write(new BTPackage(BTPackage.PackageType.COLOR, color));
            }
        });*/

        disconnectButton.setOnClickListener(new View.OnClickListener() {
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
