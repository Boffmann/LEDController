package com.hendrik.ledcontroller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hendrik.ledcontroller.Utils.ColorPicker;
import com.hendrik.ledcontroller.Utils.ScreenResolution;
import com.hendrik.ledcontroller.Utils.Settings;

/**
 * Settings activity for this app. Allows to selelect a field in color picker to select a new color for it
 */
public class SettingsActivity extends AppCompatActivity {

// REGION CONSTANTS

    /** Class TAG */
    private static final String TAG = "SettingsActivity";

// ENDREGION CONSTANTS

// REGION MEMBERS

    ColorPicker mColorPicker = null;
    Button mRestoreDefaultButton = null;
    Button mConfirmButton = null;

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
        startMainActivity();
    }

// ENDREGION WORKFLOW

// REGION INIT

    /**
     * Position the views in this activity based on the devices screen resolution
     */
    private void positionViews() {
        int screenWidth = ScreenResolution.getScreenWidth(this);
        int screenHeight = ScreenResolution.getScreenHeight(this);

        mColorPicker.setY((int)(screenHeight / 8.0));
        mRestoreDefaultButton.setY((int)(screenHeight - (screenHeight / 3.0)));
        mConfirmButton.setY((int)(screenHeight - (screenHeight / 3.0)));

        mRestoreDefaultButton.setX((int)(screenWidth * 0.02));
        mConfirmButton.setX((int)(screenWidth * 0.52));

        mRestoreDefaultButton.getLayoutParams().width = ((int)(screenWidth * 0.46));
        mConfirmButton.getLayoutParams().width = ((int)(screenWidth * 0.46));
    }

    /**
     * Setup the layout of this activity
     */
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

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });

        positionViews();
    }

// ENDREGION INIT

    /**
     * Send intent to start SelectNewColorActivity and send respective parameters with the intent
     * @param color the current color of the selected colorPicker button
     * @param id the selected color picker button's id
     */
    private void startSelectNewColorActivity(final int color, final String id) {
        Intent intent = new Intent(this, SelectNewColorActivity.class);
        intent.putExtra("currentColor",color);
        intent.putExtra("ID",id);
        startActivity(intent);
    }

    /**
     * Send intent to start BluetoothConnectionActivity
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    /**
     * Send intent to start SettingsActivity and thereby restart the current activity
     */
    private void restartSelf() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
