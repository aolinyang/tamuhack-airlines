package com.tamuhack.tamuhackairlines;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DeviceAdderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_adder);

        final TextView currentConnection = (TextView)findViewById(R.id.currentConnectedDeviceTextView);


        Thread reader = new Thread() {
            public void run() {
                CurrentConnectionToBluetooth bluetooth = new CurrentConnectionToBluetooth();

                Set<String> myDevices = bluetooth.getAddresses();

                StringBuilder big = new StringBuilder();

                for (Iterator<String> it = myDevices.iterator(); it.hasNext(); ) {
                    String next = it.next();
                    big.append(next);
                    if (it.hasNext()) {
                        big.append("\n");
                    }
                    else {
                        break;
                    }
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                String email = "";

                if (user != null) {
                    email = user.getEmail();
                } else {

                }

                currentConnection.setText(big.toString());

                Map<String, Object> map = new LinkedHashMap<>();
                map.put("username", email);
                map.put("devicename", myDevices);

                Poster.POSTMULT("http://tamuflights.tech:5000/adddevice", map);
            }

        };

        reader.start();
    }
}
