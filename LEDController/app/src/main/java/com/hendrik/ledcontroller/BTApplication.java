package com.hendrik.ledcontroller;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;

import com.hendrik.ledcontroller.Bluetooth.BTService;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Hendrik Tjabben
 */

public class BTApplication extends Application {

//REGION CONSTANTS

    private final static String TAG = "BTApplication";

//ENDREGION CONSTANTS

//REGION MEMBER

    private final AtomicInteger mRefCount = new AtomicInteger();

    //private BTService.LocalBinder mBinder;

    Handler.Callback realCallback = null;
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (realCallback != null) {
                realCallback.handleMessage(msg);
            }
        }
    };

//ENDREGION MEMBER

//REGION CONSTRUCTOR

    public BTApplication() {

    }

//ENDREGION CONSTRUCTOR



//REGION SET/GET

    private void startBTService() {

        Intent intent = new Intent(this, BTService.class);
        startService(intent);
    }

    public void stopBTService() {
        Intent intent = new Intent(this, BTService.class);
        stopService(intent);
    }

    public void releaseBinding() {
        if (mRefCount.get() == 0 || mRefCount.decrementAndGet() == 0) {
            // release binding
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void setCallBack(Handler.Callback callback) {
        this.realCallback = callback;
    }

//ENDREGION SET/GET


    @Override
    public void onCreate() {
        super.onCreate();

        startBTService();
    }
}
