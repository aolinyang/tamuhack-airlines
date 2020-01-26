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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    //Server URL:
    private static final String ADD_FLIGHT_URL = "http://tamuflights.tech:5000/addflight";
    private static final String REGISTER_LUGGAGE_URL = "http://tamuflights.tech:5000/registerluggage";


    private static final int REQUEST_ENABLE_BT = 1;

    //The Aircraft and Flight of this LuggageScanner
    private String COMPANY = "AA";
    private String FLIGHT_NO = "225";

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                final String deviceName = device.getName();
                final String deviceHardwareAddress = device.getAddress(); // MAC address

                boolean sendToServer = true;

                if (sendToServer && deviceHardwareAddress != null) {
                    Thread thread = new Thread() {
                        public void run() {
                            detectLuggage(deviceHardwareAddress);
                        }
                    };
                    thread.start();
                }

                /*
                if (deviceName != null) {
                    if ("358456435678".equals(deviceName)) {
                        Log.i("Found: ", deviceName + " " + deviceHardwareAddress);
                        Thread thread = new Thread() {
                            public void run() {
                                try {
                                    detectLuggage(deviceHardwareAddress);
                                } catch (IOException e) {
                                    Log.i("Failed to Upload: ", e.toString());
                                }
                            }
                        };
                        thread.start();
                    }
                }
                */
            }
        }
    };

    //The set of luggage that has been successfully boarded on the aircraft
    private Set<String> boarded = new HashSet<>(300);


    private void detectLuggage(String macAddress) {
        if (boarded.contains(macAddress)) {
            Log.i("Duplicate Device: ", macAddress);
            return;
        }

        Map<String, String> jsonMap = new LinkedHashMap<>(4);
        jsonMap.put("company", COMPANY);
        jsonMap.put("flight", FLIGHT_NO);
        jsonMap.put("name", macAddress);
        jsonMap.put("newstatus", "onboard");

        String response = Poster.POST(REGISTER_LUGGAGE_URL, jsonMap);

        if (!response.startsWith("Error")) {
            boarded.add(macAddress);
            Log.i("Luggage Success: ", macAddress + " -> " + response);
        }
        else {
            Log.i("Luggage Failed: ", macAddress + " -> " + response);
        }
    }

    private void registerFlight() {
        Map<String, String> jsonMap = new LinkedHashMap<>(4);
        jsonMap.put("company", COMPANY);
        jsonMap.put("flight", FLIGHT_NO);

        String response = Poster.POST(ADD_FLIGHT_URL, jsonMap);

        if (!response.startsWith("Error")) {
            Log.i("Register Success: ", response);
        }
        else {
            Log.i("Register Failed: ", response);
        }
    }

    public void listen() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
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


        Thread thread = new Thread() {
            public void run() {
                registerFlight();
            }
        };
        thread.start();

        listen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }
}