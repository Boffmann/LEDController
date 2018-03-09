package com.hendrik.ledcontroller;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.hendrik.ledcontroller.Bluetooth.BTService;
import com.hendrik.ledcontroller.DataStructure.DeviceArrayAdapter;

import java.util.ArrayList;


/**
 * Activity to search for BT Devices and connect.
 * Starting activity
 * @author Hendrik Tjabben
 */

public class BluetoothConnectionActivity extends BaseActivity {

    //CONSTANTS

    /** Class TAG */
    private static final String TAG = "BTConnectionActivity";

//END CONSTANTS

//MEMBER
    /** ArrayAdapter to list paired devices */
    private DeviceArrayAdapter mPairedDeviceArrayAdapter;
    /** ArrayAdapter to list unpaired but near devices */
    private DeviceArrayAdapter mUnpairedDeviceArrayAdapter;
    /** ArrayAdapter to list near devices */
    private DeviceArrayAdapter mNearDeviceArrayAdapter;
    /** List to list paired devices */
    public ArrayList<BluetoothDevice> mPairedBTDevices = new ArrayList<>();
    /** List to list unpaired devices */
    public ArrayList<BluetoothDevice> mUnpairedBTDevices = new ArrayList<>();
    /** List to list near devices */
    public ArrayList<BluetoothDevice> mNearBTDevices = new ArrayList<>();
    /** List view to show all paired devices */
    ListView mLvPairedDevices;
    /** List view to show all unpaired devices */
    ListView mLvUnpairedDevices;
    /** List view to show all near devices */
    ListView mLvNearDevices;
    /** The BTService */
    private BTService mBTService = null;
    /** Create a BroadcastReceiver for ACTION_FOUND. Bluetooth*/
    private final BroadcastReceiver mReceiverUnpairedFound = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // TODO
                mUnpairedBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mUnpairedDeviceArrayAdapter.notifyDataSetChanged();
            }
        }
    };
    /** The service connection to talk to the Bluetooth service */
    private ServiceConnection mBTServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e(TAG, "OnServiceConnected");

            BTService.LocalBinder binder = (BTService.LocalBinder)iBinder;

            mBTService = binder.getService();

            ArrayList<BluetoothDevice> temp = mBTService.QueryPairedDevices();
            for(BluetoothDevice device : temp) {
                mPairedBTDevices.add(device);
            }
            mPairedDeviceArrayAdapter.notifyDataSetChanged();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBTService = null;
        }
    };
    /** Listener to handle action when clicked on device in list entry */
    private AdapterView.OnItemClickListener mOnListItemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice selectedDevice = (BluetoothDevice) adapterView.getItemAtPosition(i);
            connect(selectedDevice);
            startMainMenuActivity();
        }
    };

//ENDREGION MEMBER


//REGION ACTIVITY LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setupLayout();

        //BTService.LocalBinder binder = ((BTApplication)getApplicationContext()).acquireBinding();

        Log.e(TAG, "OnCreateActivity");

        //TODO Use acquireBind from Application
        Intent bindServiceIntent = new Intent(this, BTService.class);
        bindService(bindServiceIntent, mBTServiceConnection, BTService.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiverUnpairedFound);
        unbindService(mBTServiceConnection);
        ((BTApplication) getApplication()).stopBTService();
    }

//ENDREGION ACTIVITY LIFECYCLE

//REGION INIT

    /**
     * Initialize Activity
     */
    private void init() {

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiverUnpairedFound, filter);

    }

    /**
     * Setup Device lists with on click listener and ArrayAdapter to render entries to device lists
     * */
    private void setupDeviceLists() {
        mLvPairedDevices = (ListView) findViewById(R.id.lvPairedDevices);
        mLvUnpairedDevices = (ListView) findViewById(R.id.lvUnpairedDevices);
        mLvNearDevices = (ListView) findViewById(R.id.lvNearDevices);

        mLvPairedDevices.setOnItemClickListener(mOnListItemClicked);
        mLvUnpairedDevices.setOnItemClickListener(mOnListItemClicked);
        mLvNearDevices.setOnItemClickListener(mOnListItemClicked);

        mPairedBTDevices = new ArrayList<>();
        mUnpairedBTDevices = new ArrayList<>();
        mNearBTDevices = new ArrayList<>();

        mPairedDeviceArrayAdapter = new DeviceArrayAdapter(getApplicationContext(), R.layout.device_adapter_view, mPairedBTDevices);
        mLvPairedDevices.setAdapter(mPairedDeviceArrayAdapter);

        mUnpairedDeviceArrayAdapter = new DeviceArrayAdapter(getApplicationContext(), R.layout.device_adapter_view, mUnpairedBTDevices);
        mLvUnpairedDevices.setAdapter(mUnpairedDeviceArrayAdapter);

        mNearDeviceArrayAdapter = new DeviceArrayAdapter(getApplicationContext(), R.layout.device_adapter_view, mNearBTDevices);
        mLvNearDevices.setAdapter(mNearDeviceArrayAdapter);
    }

    /**
     * Sets up the layout
     */
    private void setupLayout() {
        setContentView(R.layout.bt_activity_view);

        setupDeviceLists();

        final Button button = (Button)findViewById(R.id.search_device_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBTService.BTDiscovery();
            }
        });

    }

//ENDREGION INIT

    /**
     * Calls intent for MainMenu
     */
    private void startMainMenuActivity() {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }



//REGION BLUETOOTH

    /**
     * Connect to a selected Device
     * @param bluetoothDevice the BTDevice to connect to
     */
    private void connect(final BluetoothDevice bluetoothDevice) {
        mBTService.connectToDevice(bluetoothDevice);
    }

//ENDREGION BLUETOOTH

}
