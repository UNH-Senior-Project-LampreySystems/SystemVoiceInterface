package com.example.voice_sphinx.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicholas on 2/10/2016.
 */
public class InternetUtils
{
    //----------------- instance variables --------
    ConnectivityManager connectivityManager;
    WifiManager wifiManager;
    MainActivity ma;

    //----------------- constructor --------
    InternetUtils(MainActivity mainActivity)
    {
        ma = mainActivity;
        wifiManager = (WifiManager) ma.getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) ma.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    //----------------- scan for available networks --------
    public List<ScanResult> scan()
    {
        wifiManager.startScan();
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<ScanResult> scanResultList = wifiManager.getScanResults();
        List<ScanResult> scanResultListUnique = new ArrayList<ScanResult>();
        for(ScanResult sr : scanResultList)
        {
            boolean in = false;

            for(ScanResult sru : scanResultListUnique)
            {
                if(sr.SSID.equals(sru.SSID))
                    in = true;
            }

            if(!in)
                scanResultListUnique.add(sr);
        }

        return scanResultListUnique;
    }

    //----------------- return the current internet status --------
    public String getStatus()
    {
        String ret = "You are not currently connected to a network";
        int connectionType = connectivityManager.getActiveNetworkInfo().getType();

        if(connectionType == ConnectivityManager.TYPE_WIFI) {
            ret = wifiManager.getConnectionInfo().getSSID();
            ret = "You are currently connected to " + ret + " .";
        }
        else if(connectionType == ConnectivityManager.TYPE_MOBILE){
            ret = "You are currently connected to a mobile network .";
        }

        return ret;
    }


    //----------------- connect to a given network --------
    public boolean connect(ScanResult sr)
    {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + sr.SSID + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        int netID = wifiManager.addNetwork(conf);

        wifiManager.disconnect();
        wifiManager.enableNetwork(netID, true);
        wifiManager.reconnect();

        double start = System.currentTimeMillis();

        while(! (wifiManager.getConnectionInfo().getSSID() != null && wifiManager.getConnectionInfo().getSSID().equals(conf.SSID)))
        {
            if(System.currentTimeMillis()-start > 20000)
            {
               return false;
            }
        }

        return true;
    }

    public boolean connectWEP(ScanResult sr, String password)
    {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + sr.SSID + "\"";
        conf.wepKeys[0] = "\"" + password + "\"";
        conf.wepTxKeyIndex = 0;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        int netID = wifiManager.addNetwork(conf);

        wifiManager.disconnect();
        wifiManager.enableNetwork(netID, true);
        wifiManager.reconnect();

        double start = System.currentTimeMillis();

        while(! (wifiManager.getConnectionInfo().getSSID() != null && wifiManager.getConnectionInfo().getSSID().equals(conf.SSID)))
        {
            if(System.currentTimeMillis()-start > 20000)
            {
               return false;
            }
        }

        return true;
    }

    public boolean connectWPA(ScanResult sr, String password)
    {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + sr.SSID + "\"";
        conf.preSharedKey = "\""+ password +"\"";

        int netID = wifiManager.addNetwork(conf);

        wifiManager.disconnect();
        wifiManager.enableNetwork(netID, true);
        wifiManager.reconnect();

        double start = System.currentTimeMillis();

        while(! (wifiManager.getConnectionInfo().getSSID() != null && wifiManager.getConnectionInfo().getSSID().equals(conf.SSID)))
        {
            if(System.currentTimeMillis()-start > 20000)
            {
                return false;
            }
        }

        return true;
    }
}
