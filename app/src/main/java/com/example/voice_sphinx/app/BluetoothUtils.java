package com.example.voice_sphinx.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.Set;

/**
 * Created by Nicholas on 2/29/2016.
 */
public class BluetoothUtils
{
    //----------------- instance variables --------
    MainActivity ma;
    BluetoothAdapter bluetoothAdapter;

    //----------------- constructor --------
    BluetoothUtils(MainActivity mainActivity)
    {
        ma = mainActivity;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    //----------------- return the current bluetooth status --------
    public String getStatus()
    {
        String s = "Bluetooth is currently turned off.";

        if(bluetoothAdapter.isEnabled()) {
            s = "Bluetooth is currently turned on, and I am paired with:";

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            for (BluetoothDevice bd : pairedDevices)
                s += " " + bd.getName() + ",";
        }

        return s;
    }
}
