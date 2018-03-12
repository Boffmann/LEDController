package com.hendrik.ledcontroller;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.hendrik.ledcontroller.Bluetooth.Command.BTBinaryCommand;
import com.hendrik.ledcontroller.Bluetooth.Command.BTUnaryCommand;
import com.hendrik.ledcontroller.Bluetooth.BTService;
import com.hendrik.ledcontroller.Bluetooth.BTTransmitProtocol;
import com.hendrik.ledcontroller.Bluetooth.Command.BTWidenedCommand;

import java.util.ArrayList;


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
        unbindService(mBTServiceConnection);
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
        SeekBar brightnessSeekBar = (SeekBar)findViewById(R.id.brightness_seekbar);
        ColorPickerView colorPicker = (ColorPickerView)findViewById((R.id.color_picker_view));
        final TextView brightnessSeekBarValueView = (TextView)findViewById(R.id.brightness_seekbar_value);
        brightnessSeekBarValueView.setText(String.valueOf(brightnessSeekBar.getProgress())+"%");

        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTService.write(new BTUnaryCommand(BTTransmitProtocol.ActionType.ON));
            }
        });

        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTService.write(new BTUnaryCommand(BTTransmitProtocol.ActionType.OFF));
            }
        });

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                brightnessSeekBarValueView.setText(String.valueOf(seekBar.getProgress())+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                BTService.write(new BTBinaryCommand(BTTransmitProtocol.ActionType.BRIGHTNESS, seekBar.getProgress()));
            }
        });

        colorPicker.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int selectedColor) {
                ArrayList<Integer> color = new ArrayList<>();
                color.add(0, Color.red(selectedColor));
                color.add(1, Color.green(selectedColor));
                color.add(2, Color.blue(selectedColor));
                BTService.write(new BTWidenedCommand(BTTransmitProtocol.ActionType.COLOR, color));
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
