package com.hendrik.ledcontroller;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hendrik.ledcontroller.Bluetooth.BTService;

public class MainMenu extends BaseActivity {

//CONSTANTS

    private static final String TAG = "MainMenu";

//END CONSTANTS

//MEMBER

    private BTService mBTService;

//ENDMEMBER


//REGION ACTIVITY LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBTService = ((BTApplication) getApplicationContext()).getBTService();

        setupLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//ENDREGION ACTIVITY LIFECYCLE

//REGION INIT

    private void setupLayout() {

        setContentView(R.layout.activity_main_menu);

        Button onButton = (Button)findViewById(R.id.button_on);
        Button offButton = (Button)findViewById(R.id.button_off);

        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] on = {0x6F, 0x6E};
                mBTService.write(on);
            }
        });

        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] off = {0x6F, 0x66, 0x66};
                mBTService.write(off);
            }
        });


    }

//ENDREGION INIT






}
