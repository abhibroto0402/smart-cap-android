package com.home.smartcap;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    public static final UUID LOG_TRANSFER_UUID = UUID.fromString("0795db4a-269d-341c-296a-8f6ac0e46000");
    public static final UUID LOG_COUNT_UUID = UUID.fromString("00000000-0000-1000-8000-00805f9b34fb");
    public static final UUID TIMESTAMP_UUID = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
    public static final UUID EVENT_TYPE_UUID = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb");
    public static final UUID EVENT_VALUE_UUID = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb");
    public static final UUID ACK_UUID = UUID.fromString("00000004-0000-1000-8000-00805f9b34fb");


    private ListView mListView;
    private ArrayAdapter mListAdapter;
    private int mIndex = 0;

    private Button mButtonUpdate;

    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattService mLogTransferService;

    private int mTimestamp;
    private int mEventType;
    private int mEventValue;
    private int mReadStatus = 0x00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.my_list_view);
        mListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        mListView.setAdapter(mListAdapter);

        mButtonUpdate = (Button) findViewById(R.id.button);
        mButtonUpdate.setOnClickListener(onUpdateClick);

        // Initializes Bluetooth adapter.
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
    }

    View.OnClickListener onUpdateClick = new View.OnClickListener() {
        public void onClick(View v) {

            mBluetoothGatt = mDevice.connectGatt(getApplicationContext(), false, btleGattCallback);


        }
    };


    //------------------------------------------------------------------------------------
    // Helper Functions to assist with GATT connection
    //------------------------------------------------------------------------------------
    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // this will get called anytime you perform a read or write characteristic operation
            Log.i("EVENT-TAG", "onCharacteristicChanged");
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects
            Log.i("EVENT-TAG", "onConnectionStateChange");
            if(status == BluetoothGatt.GATT_SUCCESS)
            {
                mBluetoothGatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            // this will get called after the client initiates a BluetoothGatt.discoverServices() call
            Log.i("EVENT-TAG", "onServicesDiscovered");
            List<BluetoothGattService> services = mBluetoothGatt.getServices();
            for (BluetoothGattService service : services) {
                if(service.getUuid().equals(LOG_TRANSFER_UUID)) {
                    mLogTransferService = service;
                    for(BluetoothGattCharacteristic c : service.getCharacteristics())
                    {
                        Log.i("TAG-CHARACTERISTICS", String.format("%s", c.getUuid()));
                    }
                    BluetoothGattCharacteristic logCount = service.getCharacteristic(LOG_COUNT_UUID);
                    mBluetoothGatt.readCharacteristic(logCount);

                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

            if(characteristic.getUuid().equals(LOG_COUNT_UUID))
            {
                int count = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32,0);
                if( count > 0)
                {
                    Log.i("EVENTLOG COUNT", String.format("%d", count));
                    mReadStatus = 0x00;
                    BluetoothGattCharacteristic timestamp = mLogTransferService.getCharacteristic(TIMESTAMP_UUID);


                    mBluetoothGatt.readCharacteristic(timestamp);


                } else
                {
                    mBluetoothGatt.disconnect();
                    mBluetoothGatt.close();
                }
            }
            else if (characteristic.getUuid().equals(TIMESTAMP_UUID))
            {
                mTimestamp = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32,0);
                mReadStatus |= 0x01;
                BluetoothGattCharacteristic eventType = mLogTransferService.getCharacteristic(EVENT_TYPE_UUID);
                mBluetoothGatt.readCharacteristic(eventType);
            }
            else if (characteristic.getUuid().equals(EVENT_TYPE_UUID))
            {
                mEventType = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32,0);
                mReadStatus |= 0x02;
                BluetoothGattCharacteristic eventValue = mLogTransferService.getCharacteristic(EVENT_VALUE_UUID);
                mBluetoothGatt.readCharacteristic(eventValue);
            }
            else if (characteristic.getUuid().equals(EVENT_VALUE_UUID))
            {
                mEventValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32,0);
                mReadStatus |= 0x04;
            }

            if(mReadStatus == 0x07)
            {
                mReadStatus = 0x00;

                final String event = String.format("[%d]<T:%d>:<E:%d>:<D:%d>", mIndex, mTimestamp, mEventType, mEventValue);
                Log.i("TAG-EVENT:", event);

                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        mListAdapter.add(event);
                        mIndex++;
                        //adapter.notifyDataSetChanged();

                    }
                });


                BluetoothGattCharacteristic ack = mLogTransferService.getCharacteristic(ACK_UUID);
                ack.setValue(1, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                mBluetoothGatt.writeCharacteristic(ack);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            if(characteristic.getUuid().equals(ACK_UUID)) {
                BluetoothGattCharacteristic logCount = mLogTransferService.getCharacteristic(LOG_COUNT_UUID);
                mBluetoothGatt.readCharacteristic(logCount);
            }

        }
    };

}
