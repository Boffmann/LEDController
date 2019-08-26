package com.hendrik.ledcontroller.Bluetooth.Command;

public class BTPackage {

    public enum PackageType {
        ONOFF,
        BRIGHTNESS,
        COLOR
    }

    private final static String TAG = "BTPackage";
    private byte mType;
    private byte[] mData;

    public BTPackage (PackageType type, byte data) {
        init(type);
        mData = new byte[]{data};
    }

    public BTPackage (PackageType type, byte[] data) {
        assert data.length < 3;
        init(type);
        mData = data;
    }

    private void init(PackageType type) {
        switch (type) {
            case ONOFF:
                mType = 0;
                break;
            case BRIGHTNESS:
                mType = 1;
                break;
            case COLOR:
                mType = 2;
                break;
        }
    }

    public byte[] getData() {
        byte[] result = new byte[5];

        result [0] = mType;
        for (int i = 0; i < mData.length; i++) {
            result[1+i] = mData[i];
        }

        int sum = 0;
        for (byte data : result) {
            sum = sum + data;
        }
        result[4] = (byte)sum;

        return result;
    }
}
