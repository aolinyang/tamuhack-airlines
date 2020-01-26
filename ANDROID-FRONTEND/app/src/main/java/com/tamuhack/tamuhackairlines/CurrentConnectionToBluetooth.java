package com.tamuhack.tamuhackairlines;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.HashSet;
import java.util.Set;

public class CurrentConnectionToBluetooth {

    private Set<BluetoothDevice> connectedDevices;

    public CurrentConnectionToBluetooth() {

        this.connectedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();

    }

    public Set<String> getAddresses() {
        Set<String> result = new HashSet<>();

        for (BluetoothDevice device : connectedDevices) {
            result.add(device.getAddress());
        }
        return result;
    }



}
