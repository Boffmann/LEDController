package com.hendrik.ledcontroller;

import android.app.Application;
import android.os.Handler;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Hendrik Tjabben
 *
 * The bluetooth application
 */
public class BTApplication extends Application {

//REGION CONSTANTS

    /** Class TAG */
    private final static String TAG = "BTApplication";

//ENDREGION CONSTANTS

//REGION MEMBER

    /** Counter for bound activities */
    private final AtomicInteger mRefCount = new AtomicInteger();

    Handler.Callback realCallback = null;
    /** BT Connection Handler */
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (realCallback != null) {
                realCallback.handleMessage(msg);
            }
        }
    };

//ENDREGION MEMBER

//REGION CONSTRUCTOR

    /**
     * Default constructor
     */
    public BTApplication() {

    }

//ENDREGION CONSTRUCTOR



//REGION SET/GET

    /**
     * Release a BT Binding
     */
    public void releaseBinding() {
        if (mRefCount.get() == 0 || mRefCount.decrementAndGet() == 0) {
            // release binding
        }
    }

    /**
     * Accessor for BT Connection Handler
     * @return the BT Connection handler
     */
    public Handler getHandler() {
        return handler;
    }

    /**
     * Set handler callback
     * @param callback the handler callback to set
     */
    public void setCallBack(Handler.Callback callback) {
        this.realCallback = callback;
    }

//ENDREGION SET/GET


    @Override
    public void onCreate() {
        super.onCreate();
    }
}
