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

                if (deviceName != null && "OP6T".equals(deviceName.toUpperCase())) {
                    Log.i("Detected Larson", "Friendly Name: " + deviceName + ": MAC Address: " + deviceHardwareAddress);
                    return;
                }
                else if ("64:a2:f9:ec:f2:8b".toUpperCase().equals(deviceHardwareAddress.toUpperCase())) {

                    Log.i("Detected Larson", "Friendly Name: " + deviceName + ": MAC Address: " + deviceHardwareAddress);
                }

                if (true) {
                    return;
                }

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
    };

    //The set of luggage that has been successfully boarded on the aircraft
    private Set<String> boarded = new HashSet<>(300);

    private static String surround(String str) {
        return "\"" + str + "\"";
    }

    private static String getJSONString(Map<String, String> jsonMap) {
        String result = "{";
        for (Iterator<String> it = jsonMap.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            result += surround(key) + ": " + surround(jsonMap.get(key));
            if (it.hasNext()) {
                result += ",";
            } else {
                break;
            }
        }
        result += "}";
        return result;
    }

    private void detectLuggage(String macAddress) throws IOException {
        if (boarded.contains(macAddress)) {
            return;
        }

        URL url = new URL(REGISTER_LUGGAGE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        connection.setDoInput(true);

        Map<String, String> jsonMap = new LinkedHashMap<>(4);
        jsonMap.put("company", COMPANY);
        jsonMap.put("flight", FLIGHT_NO);
        jsonMap.put("name", macAddress);
        jsonMap.put("newstatus", "onboard");

        String result = getJSONString(jsonMap);

        byte[] out = result.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        connection.setFixedLengthStreamingMode(length);
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.connect();

        try (OutputStream os = connection.getOutputStream()) {
            os.write(out);
            os.flush();
        }

        StringBuilder response = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        connection.disconnect();

        if (response.length() == 0) {
            Log.i("Detect Luggage: ", "() " + macAddress);
        }
        else {
            boarded.add(macAddress);
            Log.i("Detect Luggage: ", response.toString() + " " + macAddress);
        }
    }

    private void registerFlight() throws IOException {
        URL url = new URL(ADD_FLIGHT_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        connection.setDoInput(true);

        Map<String, String> jsonMap = new LinkedHashMap<>();
        jsonMap.put("company", COMPANY);
        jsonMap.put("flight", FLIGHT_NO);

        String result = getJSONString(jsonMap);

        byte[] out = result.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        connection.setFixedLengthStreamingMode(length);
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.connect();

        try (OutputStream os = connection.getOutputStream()) {
            os.write(out);
            os.flush();
        }

        StringBuilder response = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        connection.disconnect();

        Log.i("Register Flight: ", response.toString());
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


                try {
                    registerFlight();
                } catch (IOException e) {

                }
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
