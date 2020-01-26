package com.example.luggagescanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //Log.v("On Receive", "Action: " + action);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i("On Recieve", deviceName + ": " + deviceHardwareAddress);
            }
            else {
                //Log.v("MY DEVICE", "Something else happened.");
            }
        }
    };

    public void listen() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);

                while (true) {
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                    if (bluetoothAdapter == null) {
                        continue;
                    }

                    if (!bluetoothAdapter.isEnabled()) {
                        Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enable, REQUEST_ENABLE_BT);
                    }

                    if (bluetoothAdapter.isDiscovering()) {
                        continue;
                    }

                    if (bluetoothAdapter.startDiscovery()) {
                        Log.i("Listen Thread", "Started discovery.");
                    }
                    else {
                        Log.i("Listen Thread", "Failed to start discovery.");
                    }
                }
            }
        };

        thread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("On Create", "Registering Receiver with IntentFilter.");
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(receiver, filter);
        Log.v("On Create", "Registered Receiver with IntentFilter.");

        listen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }
}
