package com.hendrik.ledcontroller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hendrik.ledcontroller.Utils.Settings;

/**
 * Class that allows to select and test new RGB Colors for ColorPicker. Colors are buffered in shared preferences
 */
public class SelectNewColorActivity extends BaseBTActivity {

// REGION CONSTANTS

    /** Class TAG */
    private static final String TAG = "SelectNewColorActivity";

// ENDREGION CONSTANTS

// REGION MEMBERS

    private int mCurrentColor;
    private String mID;

    private int mRed;
    private int mGreen;
    private int mBlue;

    private LinearLayout mRoot = null;

    private TextView mRedSeekbarTextView = null;
    private TextView mGreenSeekbarTextView = null;
    private TextView mBlueSeekbarTextView = null;

// ENDREGION MEMBERS

// REGIONS WORKFLOW

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent(); // gets the previously created intent
        mCurrentColor = intent.getIntExtra("currentColor", 0);
        mID = intent.getStringExtra("ID");
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
    protected void onBTConnected() {
        // Do nothing
    }


// ENDREGION WORKFLOW

// REGION INIT

    /**
     * Set the currently selected color as background of this activity
     */
    private void setCurrentColorAsActivityBackground() {

        Drawable drawble = new ColorDrawable(Color.rgb(mRed, mGreen, mBlue));
        mRoot.setBackground(drawble);
    }

    /**
     * Setup activity layout
     */
    private void setupLayout() {

        SharedPreferences sharedPref = Settings.getSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor= sharedPref.edit();

        setContentView(R.layout.select_new_color_view);

        SeekBar mRedSeekbar = findViewById(R.id.red_seekbar);
        SeekBar mGreenSeekbar = findViewById(R.id.green_seekbar);
        SeekBar mBlueSeekbar = findViewById(R.id.blue_seekbar);

        mRedSeekbarTextView = findViewById(R.id.red_seekbar_value);
        mGreenSeekbarTextView = findViewById(R.id.green_seekbar_value);
        mBlueSeekbarTextView = findViewById(R.id.blue_seekbar_value);

        Button mTestColorButton = findViewById(R.id.button_test_color);
        Button mConfirmButton = findViewById(R.id.button_confirm_color);
        Button mCancelButton = findViewById(R.id.button_cancel_color);

        mRed = Color.red(mCurrentColor);
        mGreen = Color.green(mCurrentColor);
        mBlue = Color.blue(mCurrentColor);

        mRedSeekbar.setProgress(mRed);
        mGreenSeekbar.setProgress(mGreen);
        mBlueSeekbar.setProgress(mBlue);

        mRedSeekbarTextView.setText(""+mRed);
        mGreenSeekbarTextView.setText(""+mGreen);
        mBlueSeekbarTextView.setText(""+mBlue);

        mRoot = (LinearLayout) findViewById(R.id.select_new_color_layout);

        setCurrentColorAsActivityBackground();

        mRedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRed = progress;
                mRedSeekbarTextView.setText(""+mRed);
                setCurrentColorAsActivityBackground();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mGreenSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mGreen = progress;
                mGreenSeekbarTextView.setText(""+mGreen);
                setCurrentColorAsActivityBackground();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mBlueSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBlue = progress;
                mBlueSeekbarTextView.setText(""+mBlue);
                setCurrentColorAsActivityBackground();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(getButtonsSettingID(), String.valueOf(Color.rgb(mRed, mGreen, mBlue)));
                editor.apply();
                startSettingsActivity();
            }
        });
    }

// ENDREGION INIT

    /**
     * Convert a button's id into the respective color id in the settings
     * @return the color id in the settings
     */
    private String getButtonsSettingID() {
        switch (mID) {
            case "colorPickerButton1":
                return Settings.COLOR1;
            case "colorPickerButton2":
                return Settings.COLOR2;
            case "colorPickerButton3":
                return Settings.COLOR3;
            case "colorPickerButton4":
                return Settings.COLOR4;
            case "colorPickerButton5":
                return Settings.COLOR5;
            case "colorPickerButton6":
                return Settings.COLOR6;
            case "colorPickerButton7":
                return Settings.COLOR7;
            case "colorPickerButton8":
                return Settings.COLOR8;
            case "colorPickerButton9":
                return Settings.COLOR9;
            default:
                return "";

        }
    }

    /**
     * Send intent to start SettingsActivity
     */
    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
