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
import com.hendrik.ledcontroller.DataStructure.DeviceListAdapter;

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
    /** ListAdapter to list discovered devices */
    private DeviceListAdapter mDeviceListAdapter;
    /** List to list all discovered new devices */
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    /** List view to show all listed new devices */
    ListView mLvNewDevices;
    /** The BTService */
    private BTService mBTService = null;
    /** Create a BroadcastReceiver for ACTION_FOUND. Bluetooth*/
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter.notifyDataSetChanged();
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
                mBTDevices.add(device);
            }
            mDeviceListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBTService = null;
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


        mDeviceListAdapter = new DeviceListAdapter(getApplicationContext(), R.layout.device_adapter_view, mBTDevices);
        mLvNewDevices.setAdapter(mDeviceListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }

//ENDREGION ACTIVITY LIFECYCLE

//REGION INIT

    /**
     * Initialize Activity
     */
    private void init() {
        mBTDevices = new ArrayList<>();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

    }

    /**
     * Sets up the layout
     */
    private void setupLayout() {
        setContentView(R.layout.bt_activity_view);

        mLvNewDevices = (ListView) findViewById(R.id.lvNewDevices);

        mLvNewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice selectedDevice = (BluetoothDevice) adapterView.getItemAtPosition(i);
                connect(selectedDevice);
                startMainMenuActivity();
            }
        });

        final Button button = (Button)findViewById(R.id.search_device_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBTDevices.clear();
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
