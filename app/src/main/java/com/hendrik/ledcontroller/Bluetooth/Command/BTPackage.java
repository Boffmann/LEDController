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

    /**
     * Convert the data into the Bluetooth protocol
     * Field 0: Start Byte (55)
     * Field 1: Type
     * Field 2: Data Field 1
     * Field 3: Data Field 2
     * Field 4: Data Field 3
     * Field 5: Checksum (Sum of all fields except start and stop)
     * Field 6: End Byte (54)
     * Pays attention that no other bytes than start and stop byte are 55/54.
     * Is implemented on Arduino side in the same way
     * @return
     */
    public byte[] getData() {
        byte[] result = new byte[7];

        result[0] = 55;
        result [1] = mType;
        for (int i = 0; i < mData.length; i++) {
            byte dataValue = mData[i];
            if (dataValue == 54 || dataValue == 55) {
                dataValue = 56;
            }
            result[2+i] = dataValue;
        }

        int sum = 0;
        for (int i = 1; i < result.length; i++) {
            sum = sum + result[i];
        }
        if (sum == 54 || sum == 55) {
            sum = 56;
        }
        result[5] = (byte)sum;
        result[6] = 54;

        return result;
    }
}
