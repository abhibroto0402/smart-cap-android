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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private Button mButtonUpdate, mButtonUpload;
    private int mcount;
    private boolean tcount, hcount;
    private JSONObject jsonBody;
    private String mDeviceName, email, talert, halert;
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

        mButtonUpload = (Button) findViewById(R.id.upload);
        mButtonUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            /*
            * Create JSON File for the request body
            * {
	            "email":"abi@abi.com",
	            "date":"12-02-2016",
	            "num":"3"
               }
            *
            */
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                String edate = sdf.format(new Date());
                jsonBody = new JSONObject();
                try {
                    jsonBody.put("email", email);
                    jsonBody.put("date", edate);
                    jsonBody.put("num", String.valueOf(mcount));
                    if (tcount)
                        jsonBody.put("temp_alert", "true");
                    else
                        jsonBody.put("temp_alert", talert);
                    if (hcount)
                        jsonBody.put("humidity_alert", "true");
                    else
                        jsonBody.put("humidity_alert", halert);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new UploadEvent().execute(ServerUtil.getBaseEndpoint() + "event");
            }
        });

        // Initializes Bluetooth adapter.
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mcount = Integer.parseInt(intent.getStringExtra("mcount"));
        email = intent.getStringExtra("email");
        talert = intent.getStringExtra("talert");
        halert = intent.getStringExtra("halert");

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
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBluetoothGatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            // this will get called after the client initiates a BluetoothGatt.discoverServices() call
            Log.i("EVENT-TAG", "onServicesDiscovered");
            List<BluetoothGattService> services = mBluetoothGatt.getServices();
            for (BluetoothGattService service : services) {
                if (service.getUuid().equals(LOG_TRANSFER_UUID)) {
                    mLogTransferService = service;
                    for (BluetoothGattCharacteristic c : service.getCharacteristics()) {
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

            if (characteristic.getUuid().equals(LOG_COUNT_UUID)) {
                int count = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                if (count > 0) {
                    Log.i("EVENTLOG COUNT", String.format("%d", count));
                    mReadStatus = 0x00;
                    BluetoothGattCharacteristic timestamp = mLogTransferService.getCharacteristic(TIMESTAMP_UUID);


                    mBluetoothGatt.readCharacteristic(timestamp);


                } else {
                    mBluetoothGatt.disconnect();
                    mBluetoothGatt.close();

                }
            } else if (characteristic.getUuid().equals(TIMESTAMP_UUID)) {
                mTimestamp = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                mReadStatus |= 0x01;
                BluetoothGattCharacteristic eventType = mLogTransferService.getCharacteristic(EVENT_TYPE_UUID);
                mBluetoothGatt.readCharacteristic(eventType);
            } else if (characteristic.getUuid().equals(EVENT_TYPE_UUID)) {
                mEventType = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                mReadStatus |= 0x02;
                BluetoothGattCharacteristic eventValue = mLogTransferService.getCharacteristic(EVENT_VALUE_UUID);
                mBluetoothGatt.readCharacteristic(eventValue);
            } else if (characteristic.getUuid().equals(EVENT_VALUE_UUID)) {
                mEventValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                mReadStatus |= 0x04;
            }

            if (mReadStatus == 0x07) {
                mReadStatus = 0x00;

                final String event = String.format("[%d]<T:%d>:<E:%d>:<D:%d>", mIndex, mTimestamp, mEventType, mEventValue);
                Log.i("TAG-EVENT:", event);
                switch (mEventType) {
                    case 0:
                        mcount++;
                        break;
                    case 2:
                        if (mEventValue > 30000)
                            tcount=true;
                        break;
                    case 3:
                        if (mEventValue > 40000)
                            hcount=true;
                        break;


                }

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

            if (characteristic.getUuid().equals(ACK_UUID)) {
                BluetoothGattCharacteristic logCount = mLogTransferService.getCharacteristic(LOG_COUNT_UUID);
                mBluetoothGatt.readCharacteristic(logCount);
            }

        }


    };

    public class UploadEvent extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();
            StringBuilder info = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.connect();
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(jsonBody.toString());
                wr.close();
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }

    ;
}
