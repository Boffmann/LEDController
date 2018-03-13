package com.hendrik.ledcontroller.Bluetooth;

import android.util.Log;

import com.hendrik.ledcontroller.Bluetooth.Command.BTCommand;
import com.hendrik.ledcontroller.Bluetooth.Command.BTUnaryCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by hendrik tjabben on 08.03.2018.
 */

public class BtIOThread extends Thread {

    private final static String TAG = "BTIOThread";
    /** Time to wait until assume timeout */
    private final long mBluetoothTimeoutValueMilliSeconds = 5000;

    private final int mTimesToRetryToSend = 1;

    /** List to store all bluetooth commands in */
    private ArrayList<BTCommand> mCommandList;
    /** Mutex to synchronize access on commandList */
    private Semaphore mSemaphore;
    /** Output stream to write data over bluetooth */
    private OutputStream mOutputStream;
    /** Input stream to receive data over bluetooth */
    private InputStream mInputStream;
    /** Notification object for io processing thread to inform about queued send data */
    private Object lock = new Object();
    /** Flag indicating if bluetooth receive timeout occurs */
    private boolean mBluetoothReceiveTimeout = false;

    private boolean mIsDataReceived = false;

    public BtIOThread(final InputStream inputStream, final OutputStream outputStream) {
        mCommandList = new ArrayList();
        mSemaphore = new Semaphore(1);
        mInputStream = inputStream;
        mOutputStream = outputStream;
    }

    private enum ReceiveAnswer {
        SUCCESS,
        TIMEOUT,
        FAILURE
    }



    /***
     * Wait until bt device confirms received data or timeout
     * @param command the command to wait until confirmed
     * @return ReceiveAnswer about status of answer
     */
    private ReceiveAnswer waitForReceiveConfirmation(final BTCommand command) {

        int bytesAvailable;
        mIsDataReceived = false;
        mBluetoothReceiveTimeout = false;
        boolean correctAnswer = false;

        Thread observTimeoutThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final long startTime = System.currentTimeMillis();
                while (!mIsDataReceived && !mBluetoothReceiveTimeout) {
                    long passedTime = System.currentTimeMillis() - startTime;
                    if (passedTime >= mBluetoothTimeoutValueMilliSeconds) {
                        mBluetoothReceiveTimeout = true;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        observTimeoutThread.start();

        while(!mIsDataReceived && !mBluetoothReceiveTimeout) {
            try {
                bytesAvailable = mInputStream.available();
                if (bytesAvailable > 0) {
                    // Receive bytes
                    mIsDataReceived = true;
                    byte[] receivedData = new byte[bytesAvailable];
                    mInputStream.read(receivedData);
                    Log.i(TAG, "Data received: " + receivedData[0]);
                    correctAnswer = BTTransmitProtocol.isAnswerForCommand(receivedData, command);
                }
                Thread.sleep(200);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (mIsDataReceived && correctAnswer) {
            return ReceiveAnswer.SUCCESS;
        } else if (mIsDataReceived && !correctAnswer) {
            return ReceiveAnswer.FAILURE;
        } else if(mBluetoothReceiveTimeout) {
            return ReceiveAnswer.TIMEOUT;
        }

        return ReceiveAnswer.FAILURE;
    }

    public void run() {
        int timesRetryToSend = 0;
        while(true) {
            synchronized (lock) {
                if (mCommandList.isEmpty()) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    mSemaphore.acquire();
                    for (int i = 0; i < mCommandList.size(); i++) {
                        boolean writeSuccess = writeDataStream(BTSerializer.serialize(mCommandList.get(i)), mCommandList.get(i));

                        if (!writeSuccess) {
                            Log.e(TAG, "Write not succeeded");
                            if (timesRetryToSend >= mTimesToRetryToSend) {
                                Log.e(TAG, "Failed to write data to serial port after " + timesRetryToSend + " retries");
                                mCommandList.remove(i);
                                timesRetryToSend = 0;
                                continue;
                            }
                            timesRetryToSend++;
                            continue;
                        }

                        Log.e(TAG, "Write: " + mCommandList.get(i).toString());
                        ReceiveAnswer answer = waitForReceiveConfirmation(mCommandList.get(i));
                        if (answer == ReceiveAnswer.SUCCESS) {
                            mCommandList.remove(i);
                        } else if(answer == ReceiveAnswer.FAILURE) {
                            Log.e(TAG, "Failure receiving confirmation");
                            if (timesRetryToSend >= mTimesToRetryToSend) {
                                mCommandList.remove(i);
                                timesRetryToSend = 0;
                                // TODO Inform user
                            }
                            timesRetryToSend++;
                        } else if (answer == ReceiveAnswer.TIMEOUT) {
                            Log.e(TAG, "Timeout receiving answer");
                            if (timesRetryToSend >= mTimesToRetryToSend) {
                                mCommandList.remove(i);
                                timesRetryToSend = 0;
                                // TODO Inform user
                            }
                            timesRetryToSend++;
                        } else {
                            Log.e(TAG, "Something went wrong while receiving data");
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mSemaphore.release();
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

    /**
     * Sends byte containing command type to serial port and waits for confirmation.
     * The other side (Arduino) must know the command type to prepare for size of byte stream
     * */
    private boolean confirmCommandType(final BTCommand command) {
        ReceiveAnswer receiveAnswer = waitForReceiveConfirmation(command);

        if (receiveAnswer == ReceiveAnswer.SUCCESS){
            return true;
        } else {
            return false;
        }
    }

    private boolean writeDataStream(final int[] data, final BTCommand command) {
        if (data.length < 2) {
            Log.e(TAG, "To send data is to small");
            return false;
        }

        // Write type of command and check if device accepted command
        String s1 = String.format("%8s", Integer.toBinaryString(data[0] & 0xFF)).replace(' ', '0');
        Log.w(TAG, "Send command type: " + data[0]);
        Log.w(TAG, "Binary: " + s1);
        byte[] metadata = new byte[2];
        metadata[0] = (byte)data[0];
        metadata[1] = (byte)data[1];
        write(metadata);
        //boolean commandAccepted = confirmCommandType(command);
        boolean commandAccepted = true;
        if (!commandAccepted) {
            Log.e(TAG, "Command not accepted");
            return false;
        }
        byte[] paramData = new byte[data.length - 2];
        for (int i = 2; i < data.length; i++) {
            paramData[i-2] = (byte)data[i];
        }
        write(paramData);

        return true;
    }

    private void write(final byte[] data) {
        try {
            mOutputStream.write(data, 0, data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
