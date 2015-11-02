package trial.bluetooth.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


public class MainActivity extends ActionBarActivity {

    //---------------- Instance Variables ---------------
    Button b1,b2,b3,b4,b5;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    ListView lv;

    private ArrayList<BluetoothDevice> discovered = new ArrayList<BluetoothDevice>();
    private ArrayList<String> visibleDeviceList = new ArrayList<String>();

    private SpeechUtils sUtil;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private Intent intent;


    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                if(visibleDeviceList.isEmpty())
                {
                    visibleDeviceList.add(device.getName());
                    discovered.add(device);
                }
                else
                    for (String s : visibleDeviceList)
                    {
                        if (!s.equals(device.getName())) {
                            visibleDeviceList.add(device.getName());
                            discovered.add(device);
                        }
                    }

            }
        }
    };
    //---------------- onCreate -------------------------

    /**
     * Instantiate all of the instance variables
     * Create the application
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = (Button) findViewById(R.id.button);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);
        b5 = (Button) findViewById(R.id.button5);

        BA = BluetoothAdapter.getDefaultAdapter();

        lv = (ListView) findViewById(R.id.listView);

        sUtil = new SpeechUtils(this);

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver,filter);


    }

    //---------------- off --------------------------------

    /**
     * Turn off the Bluetooth adapter
     * Will not be able to pair devices or scan
     * @param v
     */
    public void off(View v) {
        BA.disable();
    }

    //---------------- on ----------------------------------

    /**
     * Tun on the Bluetooth adapter
     *
     */
    public void on(View v) {
        BA.enable();
    }

    //---------------- visible -----------------------------

    /**
     * Make the Bluetooth adapter visible to other devices
     * @param v
     */
    public void visible(View v) {
        visibleDeviceList = new ArrayList<String>();
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }

    //--------------- list ----------------------------------

    /**
     * Get all devices paired and report them in list format
     */
    public void list(View v)
    {
        /**
        for(int i = 0; i<visibleDeviceList.size(); i++)
        {
            if(visibleDeviceList.get(i).equals("NICK-PC"))
                pair(discovered.get(i));
        }
        */

        sUtil.listDevices1(visibleDeviceList);



        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, visibleDeviceList);
        lv.setAdapter(adapter);
    }

    //--------------- list ----------------------------------

    /**
     * Get all devices paired and report them in list format
     */
    /**
    public void listBonded() {
        pairedDevices = BA.getBondedDevices();
        for(BluetoothDevice bt : pairedDevices)
            visibleDeviceList.add(bt.getName());
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, visibleDeviceList);
        lv.setAdapter(adapter);
    }
    */

    public void pair(int i) {
        BluetoothDevice device = discovered.get(i);
        try {
            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {

        }
    }


    // EVERYTHING BELOW HERE IS NOT IMPORTANT ================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(result.get(0).equals("pair"))
                        pair(sUtil.getDevice());
                    else if(result.get(0).equals("ignore"))
                        sUtil.incrementDevice();
                    else
                        getConfirmation();

                }
                break;
            }

        }
    }

    public void getConfirmation()
    {
        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
    }
}
