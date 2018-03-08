package com.hendrik.ledcontroller.Bluetooth;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by hendr on 08.03.2018.
 */

public class BtIOThread extends Thread {

    private final static String TAG = "BTIOThread";

    /** List to store all bluetooth commands in */
    private ArrayList<byte[]> mCommandList;
    /** Mutex to synchronize access on commandList */
    private Semaphore mSemaphore;
    /** Output stream to write data over */
    private OutputStream mOutputStream;

    private Object lock = new Object();

    public BtIOThread(final OutputStream outputStream) {
        mCommandList = new ArrayList();
        mSemaphore = new Semaphore(1);
        mOutputStream = outputStream;
    }

    public void run() {
        while(true) {
            synchronized (lock) {
                if (mCommandList.isEmpty()) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        mSemaphore.acquire();
                        for (int i = 0; i < mCommandList.size(); i++) {
                            write(mCommandList.get(i));
                            // TODO Add mechanism to check if command was transmitted correctly
                            mCommandList.remove(i);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        mSemaphore.release();
                    }
                }
            }
        }
    }

    public void addIO(final byte[] data){
        synchronized (lock) {
            try {
                mSemaphore.acquire();
                mCommandList.add(data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mSemaphore.release();
                lock.notify();
            }
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
