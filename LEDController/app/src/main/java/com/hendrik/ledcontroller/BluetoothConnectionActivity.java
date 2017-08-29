package com.hendrik.ledcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.hendrik.ledcontroller.Bluetooth.BTManager;
import com.hendrik.ledcontroller.Bluetooth.IBTRunnable;
import com.hendrik.ledcontroller.DataStructure.DeviceListAdapter;

import java.util.ArrayList;


/**
 * @author Hendrik Tjabben
 */

public class BluetoothConnectionActivity extends BaseActivity {

    //CONSTANTS

    private static final String TAG = "BTConnectionActivity";

    private static final int REQUEST_ENABLE_BT = 1;

//END CONSTANTS

//MEMBER

    /** The devices own bluetooth adatper */
    private BluetoothAdapter mBluetoothAdapter;
    /** Flag indicating that bluetooth is set up */
    private boolean mIsBluetoothSetup = false;
    /** Class to manage Bluetooth connection */
    private BTManager mBTManager;
    /** ListAdapter to list discovered devices */
    private DeviceListAdapter mDeviceListAdapter;
    /** List to list all discovered new devices */
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    /** List view to show all listed new devices */
    ListView mLvNewDevices;
    /** Create a BroadcastReceiver for ACTION_FOUND */
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

//ENDMEMBER


//REGION ACTIVITY LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setUpBluetooth();
        setupLayout();

        // List already paired devices
        ArrayList<BluetoothDevice> temp = mBTManager.QueryPairedDevices();
        for(BluetoothDevice device : temp) {
            mBTDevices.add(device);
        }

        mDeviceListAdapter = new DeviceListAdapter(getApplicationContext(), R.layout.device_adapter_view, mBTDevices);
        mLvNewDevices.setAdapter(mDeviceListAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }

//ENDREGION ACTIVITY LIFECYCLE

//REGION INIT

    private void init() {
        mBTDevices = new ArrayList<>();
    }


    private void setUpBluetooth() {
        if( mIsBluetoothSetup ) {
            Log.w(TAG, "Bluetooth already setup");
            return;
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if ( mBluetoothAdapter == null ) {
            //Device does not support bluetooth
            //TODO Textbox to inform user
        }

        //Check if bluetooth is enabled and ask to enable it if it is not
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);


        mBTManager = new BTManager(mBluetoothAdapter, this);
        mIsBluetoothSetup = true;
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
            }
        });

        final Button button = (Button)findViewById(R.id.search_device_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBTDevices.clear();
                mBTManager.BTDiscovery();
            }
        });

    }

//ENDREGION INIT

    private void createBTService(final BluetoothSocket socket) {

        ((BTApplication) getApplicationContext()).createBTService(socket);
    }

    private void startMainMenuActivity() {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }



//REGION BLUETOOTH

    private void connect(final BluetoothDevice bluetoothDevice) {
        mBTManager.connect(bluetoothDevice, new IBTRunnable() {
            @Override
            public void BTCallback(BluetoothSocket socket) {
                Log.e(TAG, "SUCCESS");
                createBTService(socket);
                startMainMenuActivity();
            }
        });
    }

//ENDREGION BLUETOOTH

}
