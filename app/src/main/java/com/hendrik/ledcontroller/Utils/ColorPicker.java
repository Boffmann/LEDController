package com.hendrik.ledcontroller.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.hendrik.ledcontroller.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ColorPicker extends LinearLayout {

    private final int NUMBER_OF_BUTTONS = 9;

// REGION CONSTRUCTOR

    public ColorPicker(Context context) {
        super(context, null);
        init();
    }

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

// ENDREGION CONSTRUCTOR

// REGION MEMBER

    private List<Button> mColorButtons = new ArrayList<>();
    private OnClickListener mOnClickListener;
    private OnClickColorAndSettingsListener mOnClickColorSettingsListener;

// ENDREGION MEMBER

    private int getSettingsColor(final String settingsColor) {
        return Integer.parseInt(Settings.getString(settingsColor));
    }

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

        mColorButtons.get(0).setBackgroundColor(getSettingsColor(Settings.COLOR1));
        mColorButtons.get(1).setBackgroundColor(getSettingsColor(Settings.COLOR2));
        mColorButtons.get(2).setBackgroundColor(getSettingsColor(Settings.COLOR3));
        mColorButtons.get(3).setBackgroundColor(getSettingsColor(Settings.COLOR4));
        mColorButtons.get(4).setBackgroundColor(getSettingsColor(Settings.COLOR5));
        mColorButtons.get(5).setBackgroundColor(getSettingsColor(Settings.COLOR6));
        mColorButtons.get(6).setBackgroundColor(getSettingsColor(Settings.COLOR7));
        mColorButtons.get(7).setBackgroundColor(getSettingsColor(Settings.COLOR8));
        mColorButtons.get(8).setBackgroundColor(getSettingsColor(Settings.COLOR9));

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

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    public void setOnClickColorAndISettingsListener(OnClickColorAndSettingsListener listener) {
        mOnClickColorSettingsListener = listener;
    }

// ENDREGION WORKFLOW

    public interface OnClickListener {
        void onClick(final int color);
    }

    public interface OnClickColorAndSettingsListener {
        void onClick(final int color, final String id);
    }

}
