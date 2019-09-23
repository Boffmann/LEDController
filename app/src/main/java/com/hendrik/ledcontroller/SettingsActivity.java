package com.hendrik.ledcontroller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hendrik.ledcontroller.Bluetooth.BTService;
import com.hendrik.ledcontroller.Utils.ColorPicker;
import com.hendrik.ledcontroller.Utils.Settings;

public class SettingsActivity extends AppCompatActivity {

// REGION CONSTANTS

// ENDREGION CONSTANTS

// REGION MEMBERS

    private ColorPicker mColorPicker = null;
    private Button mRestoreDefaultButton = null;
    private Button mConfirmButton = null;

// ENDREGION MEMBERS

// REGIONS WORKFLOW

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        startBluetoothConnectActivity();
    }

// ENDREGION WORKFLOW

// REGION INIT

    private void setupLayout() {
        setContentView(R.layout.settings_activity_view);

        mColorPicker = findViewById(R.id.colorPickerSettings);
        mRestoreDefaultButton = findViewById(R.id.restore_default);
        mConfirmButton = findViewById(R.id.confirm_settings);

        mColorPicker.setOnClickColorAndISettingsListener(new ColorPicker.OnClickColorAndSettingsListener() {
            @Override
            public void onClick(int color, String id) {
                startSelectNewColorActivity(color, id);
            }
        });

        mRestoreDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = Settings.getSharedPreferences(getApplicationContext());
                final SharedPreferences.Editor editor= sharedPref.edit();
                editor.putString(Settings.COLOR1, String.valueOf(getResources().getColor(R.color.colorButton1)));
                editor.putString(Settings.COLOR2, String.valueOf(getResources().getColor(R.color.colorButton2)));
                editor.putString(Settings.COLOR3, String.valueOf(getResources().getColor(R.color.colorButton3)));
                editor.putString(Settings.COLOR4, String.valueOf(getResources().getColor(R.color.colorButton4)));
                editor.putString(Settings.COLOR5, String.valueOf(getResources().getColor(R.color.colorButton5)));
                editor.putString(Settings.COLOR6, String.valueOf(getResources().getColor(R.color.colorButton6)));
                editor.putString(Settings.COLOR7, String.valueOf(getResources().getColor(R.color.colorButton7)));
                editor.putString(Settings.COLOR8, String.valueOf(getResources().getColor(R.color.colorButton8)));
                editor.putString(Settings.COLOR9, String.valueOf(getResources().getColor(R.color.colorButton9)));
                editor.apply();
                restartSelf();
            }
        });
    }

// ENDREGION INIT

    private void startSelectNewColorActivity(final int color, final String id) {
        Intent intent = new Intent(this, SelectNewColorActivity.class);
        intent.putExtra("currentColor",color);
        intent.putExtra("ID",id);
        startActivity(intent);
    }

    private void startBluetoothConnectActivity() {
        Intent intent = new Intent(this, BluetoothConnectionActivity.class);
        startActivity(intent);
    }

    private void restartSelf() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
