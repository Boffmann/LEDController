package com.hendrik.ledcontroller;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hendrik.ledcontroller.Bluetooth.BTService;
import com.hendrik.ledcontroller.DataStructure.DeviceArrayAdapter;

import java.util.ArrayList;

public class AddDeviceActivity extends BaseBTActivity {

    //CONSTANTS

    /** Class TAG */
    private static final String TAG = "AddDeviceActivity";


//END CONSTANTS

// REGION MEMBER

    /** ArrayAdapter to list paired devices */
    private DeviceArrayAdapter mPairedDeviceArrayAdapter;
    /** ArrayAdapter to list unpaired but near devices */
    private DeviceArrayAdapter mUnpairedDeviceArrayAdapter;
    /** List to list paired devices */
    public ArrayList<BluetoothDevice> mPairedBTDevices = new ArrayList<>();
    /** List to list unpaired devices */
    public ArrayList<BluetoothDevice> mUnpairedBTDevices = new ArrayList<>();
    /** List view to show all paired devices */
    ListView mLvPairedDevices;
    /** List view to show all unpaired devices */
    ListView mLvUnpairedDevices;

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

    /** Listener to handle action when clicked on device in list entry */
    private AdapterView.OnItemClickListener mOnListItemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice selectedDevice = (BluetoothDevice) adapterView.getItemAtPosition(i);
            SharedPreferences sharedPref= getSharedPreferences("BTSettings", 0);
            SharedPreferences.Editor editor= sharedPref.edit();
            editor.putString("mac_address", selectedDevice.getAddress());
            editor.putString("device_name", selectedDevice.getName());
            editor.apply();
            Toast toast = Toast.makeText(getApplicationContext(), R.string.DeviceSetToSaved, Toast.LENGTH_LONG);
            toast.show();
            startBluetoothConnectionActivity();
        }
    };

    @Override
    protected void onBTConnected() {
        mPairedBTDevices.addAll(mBTService.QueryPairedDevices());
        mPairedDeviceArrayAdapter.notifyDataSetChanged();
    }

// ENDREGION MEMBER

//REGION ACTIVITY LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setupLayout();

        //BTService.LocalBinder binder = ((BTApplication)getApplicationContext()).acquireBinding();

        Log.e(TAG, "OnCreateActivity");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent bindServiceIntent = new Intent(this, BTService.class);
        bindService(bindServiceIntent, mBTServiceConnection, BTService.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiverUnpairedFound);
        unbindService(mBTServiceConnection);
        Intent intent = new Intent(this, BTService.class);
        stopService(intent);
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
        mLvPairedDevices = findViewById(R.id.lvPairedDevices);
        mLvUnpairedDevices = findViewById(R.id.lvUnpairedDevices);

        mLvPairedDevices.setOnItemClickListener(mOnListItemClicked);
        mLvUnpairedDevices.setOnItemClickListener(mOnListItemClicked);

        mPairedBTDevices = new ArrayList<>();
        mUnpairedBTDevices = new ArrayList<>();

        mPairedDeviceArrayAdapter = new DeviceArrayAdapter(getApplicationContext(), R.layout.device_adapter_view, mPairedBTDevices);
        mLvPairedDevices.setAdapter(mPairedDeviceArrayAdapter);

        mUnpairedDeviceArrayAdapter = new DeviceArrayAdapter(getApplicationContext(), R.layout.device_adapter_view, mUnpairedBTDevices);
        mLvUnpairedDevices.setAdapter(mUnpairedDeviceArrayAdapter);
    }

    /**
     * Sets up the layout
     */
    private void setupLayout() {
        setContentView(R.layout.add_device_view);

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
     * Calls intent for BluetoothConnectionActivity
     */
    private void startBluetoothConnectionActivity() {
        Intent intent = new Intent(this, BluetoothConnectionActivity.class);
        startActivity(intent);
    }
}
