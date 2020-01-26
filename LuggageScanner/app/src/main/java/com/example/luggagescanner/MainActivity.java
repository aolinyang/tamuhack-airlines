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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private static final String SERVER_URL = "http://tamuflights.tech:5000/changestatus";
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

                try {
                    upload(deviceHardwareAddress);
                } catch (IOException e) {
                    Log.i("Failed to Upload: ", e.toString());
                }

                Log.i("Detected", "Friendly Name: " + deviceName + ": MAC Address: " + deviceHardwareAddress);
            }
            else {
                //Log.v("MY DEVICE", "Something else happened.");
            }
        }
    };

    //The set of luggage that has been successfully boarded on the aircraft
    private Set<String> boarded = new HashSet<>(300);

    private static String surround(String str) {
        return "\"" + str + "\"";
    }

    private void upload(String macAddress) throws IOException {
        if (boarded.contains(macAddress)) {
            return;
        }

        URL url = new URL(SERVER_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        con.setDoInput(true);

        Map<String, String> jsonMap = new LinkedHashMap<>();
        jsonMap.put("company", "????");
        jsonMap.put("flight", "????");
        jsonMap.put("name", macAddress);
        jsonMap.put("newstatus", "onboard");

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

        byte[] out = result.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        con.setFixedLengthStreamingMode(length);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.connect();

        try (OutputStream os = con.getOutputStream()) {
            os.write(out);
            os.flush();
        }

        StringBuilder response = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        con.disconnect();

        if ("success".equals(response.toString())) {
            boarded.add(macAddress);
            Log.i("Upload Successful: ", macAddress);
        }
    }


    private JSONObject getJsonObject(Map<String, String> map) {
        JSONObject json = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                json.put(entry.getKey(), entry.getValue());
            }
        } catch (JSONException e) {
            return null;
        }
        return json;
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

        listen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }
}
