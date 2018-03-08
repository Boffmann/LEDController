package com.hendrik.ledcontroller.Bluetooth;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;

/**
 * Created by hendr on 08.03.2018.
 */

public class BtIOThread extends Thread {

    private final static String TAG = "BTIOThread";

    /** List to store all bluetooth commands in */
    private Map<String, byte[]> mCommandList;
    /** Mutex to synchronize access on commandList */
    private Semaphore mSemaphore;
    /** Output stream to write data over */
    private OutputStream mOutputStream;

    public BtIOThread(final OutputStream outputStream) {
        mCommandList = new HashMap();
        mSemaphore = new Semaphore(1);
        mOutputStream = outputStream;
    }

    public void run() {
        while(true) {
            try {
                Thread.sleep(10000);
                mSemaphore.acquire();
                for (Map.Entry<String, byte[]> pair : mCommandList.entrySet()) {
                    write(pair.getValue());
                    Thread.sleep(10000);
                }
                mCommandList.clear();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mSemaphore.release();
            }
        }
    }

    public void addIO(final byte[] data){
        UUID uuid = UUID.randomUUID();
        try {
            mSemaphore.acquire();
            mCommandList.put(uuid.toString(), data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mSemaphore.release();
        }

    }

    private boolean write(final byte[] data) {
        try {
            mOutputStream.write(data);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);
            return false;
        }
        return true;
    }

}
