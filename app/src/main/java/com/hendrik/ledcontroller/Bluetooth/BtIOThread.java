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
    private ArrayList<BTCommand> mCommandList;
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
                            write(BTSerializer.serialize(mCommandList.get(i)));
                            Log.e(TAG, "Write: " + mCommandList.get(i).toString());
                            // TODO Add mechanism to check if command was transmitted correctly. Transmit next command after receive notification occurs
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

    public void addIO(final BTCommand command){
        synchronized (lock) {
            try {
                mSemaphore.acquire();
                mCommandList.add(command);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mSemaphore.release();
                // Two notify calls will not wake up the thread twice, which is good
                // https://stackoverflow.com/questions/10684111/can-notify-wake-up-the-same-thread-multiple-times
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
