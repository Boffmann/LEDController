package com.hendrik.ledcontroller.Utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hendrik.ledcontroller.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom color picker view consisting of nine buttons to select a color
 */
public class ColorPicker extends LinearLayout {

    /** The number of total buttons */
    private final int NUMBER_OF_BUTTONS = 9;

// REGION CONSTRUCTOR

    /**
     * Default view constructor
     * @param context The application context
     */
    public ColorPicker(Context context) {
        super(context, null);
        init();
    }

    /**
     * View constructor with attributeSet
     * @param context The application context
     * @param attrs Attribute set to initialize view
     */
    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

// ENDREGION CONSTRUCTOR

// REGION MEMBER

    /** List of all color buttons */
    private List<Button> mColorButtons = new ArrayList<>();
    /** Listener to call when a button is clicked */
    private OnClickListener mOnClickListener;
    /** Listener to call when button is clicked. Not only passed color but also button id */
    private OnClickColorAndSettingsListener mOnClickColorSettingsListener;

// ENDREGION MEMBER

    /**
     * Get the color of a button from the settings
     * @param settingsColor the color to get from the settings
     * @return the color from the settings
     */
    private int getSettingsColor(final String settingsColor) {
        return Integer.parseInt(Settings.getString(settingsColor));
    }

    /**
     * Initialize the view
     */
    private void init() {
        Settings.getSharedPreferences(getContext());

        inflate(getContext(), R.layout.color_picker, this);
        mColorButtons.add((Button)findViewById(R.id.colorPickerButton1));
        mColorButtons.add((Button)findViewById(R.id.colorPickerButton2));
        mColorButtons.add((Button)findViewById(R.id.colorPickerButton3));
        mColorButtons.add((Button)findViewById(R.id.colorPickerButton4));
        mColorButtons.add((Button)findViewById(R.id.colorPickerButton5));
        mColorButtons.add((Button)findViewById(R.id.colorPickerButton6));
        mColorButtons.add((Button)findViewById(R.id.colorPickerButton7));
        mColorButtons.add((Button)findViewById(R.id.colorPickerButton8));
        mColorButtons.add((Button)findViewById(R.id.colorPickerButton9));

        // Set background color depending on colors in settings. These colors can be altered by the app's settings
        mColorButtons.get(0).setBackgroundColor(getSettingsColor(Settings.COLOR1));
        mColorButtons.get(1).setBackgroundColor(getSettingsColor(Settings.COLOR2));
        mColorButtons.get(2).setBackgroundColor(getSettingsColor(Settings.COLOR3));
        mColorButtons.get(3).setBackgroundColor(getSettingsColor(Settings.COLOR4));
        mColorButtons.get(4).setBackgroundColor(getSettingsColor(Settings.COLOR5));
        mColorButtons.get(5).setBackgroundColor(getSettingsColor(Settings.COLOR6));
        mColorButtons.get(6).setBackgroundColor(getSettingsColor(Settings.COLOR7));
        mColorButtons.get(7).setBackgroundColor(getSettingsColor(Settings.COLOR8));
        mColorButtons.get(8).setBackgroundColor(getSettingsColor(Settings.COLOR9));

        // Call respecive on click functions
        for (int i = 0; i < NUMBER_OF_BUTTONS; ++i) {
            mColorButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Drawable background = v.getBackground();
                    if (background instanceof ColorDrawable) {
                        int color = ((ColorDrawable) background).getColor();
                        if (mOnClickListener != null) {
                            mOnClickListener.onClick(color);
                        }
                        if (mOnClickColorSettingsListener != null) {
                            String ID = getResources().getResourceEntryName(v.getId());;
                            mOnClickColorSettingsListener.onClick(color, ID);
                        }
                    }
                }
            });
        }
    }

// REGION WORKFLOW

    /**
     * Register an on click listener to get color when a button is clicked
     * @param listener the listener to register
     */
    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    /**
     * Register an on click listener to get color and button id when a button is clicked
     * @param listener the listener to register
     */
    public void setOnClickColorAndISettingsListener(OnClickColorAndSettingsListener listener) {
        mOnClickColorSettingsListener = listener;
    }

// ENDREGION WORKFLOW

    /**
     * OnClickListener interface
     */
    public interface OnClickListener {
        /**
         * Called when a button is clicked
         * @param color the color that the button represents
         */
        void onClick(final int color);
    }

    /**
     * OnClickColorAndSettingsListener interface
     */
    public interface OnClickColorAndSettingsListener {
        /**
         * Called when a button is clicked
         * @param color the color that the button represents
         * @param id the clicked button's id
         */
        void onClick(final int color, final String id);
    }

}
