package trial.voice.app;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends ActionBarActivity
{
    //--------------- instance variables ---------------------------
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private Intent intent;
    private SpeechUtils speechUtils;
    private String node = "blank";


    //--------------- instance variables for testing---------------------------

    Button startButton;
    TextView textView;

    //==================================================================
    // Required setup for Activity extension
    //==================================================================

    //--------------- onCreate ----------------------------------

    /**
     * Set the layout and create the intent for speech recognition
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-------------------- Testing stuff --------------------
        startButton = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);


        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechUtils = new SpeechUtils(this);
    }

    //--------------- onCreateOptionsMenu ----------------------------------

    /**
     * Set to default
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    //--------------- onOptionsItemSelected ----------------------------------

    /**
     * Set to default
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //==================================================================
    // Parsing speech to decide on an action
    //==================================================================

    //--------------- start ----------------------------------

    /**
     * Start the graph as though the user had just said Alexa or Ok Google
     */
    public void start(View v)
    {
        speechUtils.initialResponse();
    }

    //--------------- startSpeechActivity ----------------------------------

    /**
     * Start listening for speech input
     */
    public void startSpeechActivity()
    {
        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
    }

    //--------------- onActivityResult ----------------------------------

    /**
     * Get users speech back as a string and send it to the parser
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    String result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0).toLowerCase();
                    String delimiters = "[ ]+";
                    String[] tokens = result.split(delimiters);

                    switch(node)
                    {
                        case "blank":
                            speechParser(tokens);
                            break;
                        case "status":
                            parseStatus(tokens);
                            break;
                        case "bluetooth":
                            parseBluetooth(tokens);
                            break;
                        case "internet":
                            parseInternet(tokens);
                            break;
                        case "noise":
                            parseNoise(tokens);
                            break;
                        case "restart":
                            break;
                    }
                }
            }
        }
    }

    //--------------- speechParser ----------------------------------

    /**
     * Parse the speech looking for specific keyWords
     */
    private void speechParser(String[] tokens)
    {
        for(String token : tokens)
        {
            switch (token)
            {
                case "status":
                    node = token;
                    parseStatus(tokens);
                    break;
                case "bluetooth":
                    node = token;
                    parseBluetooth(tokens);
                    break;
                case "internet":
                    node = token;
                    parseInternet(tokens);
                    break;
                case "noise":
                    node = token;
                    parseNoise(tokens);
                    break;
                case "restart":
                    node = token;
                    break;
            }
        }
    }

    //--------------- parseStatus ----------------------------------

    /**
     * Parse the speech looking for specific keyWords
     */
    private void parseStatus(String[] tokens)
    {
        for(String token : tokens)
        {
            switch (token)
            {
                case "bluetooth":
                    node = "bluetoothStatus";
                    break;
                case "internet":
                    node = "internetStatus";
                    break;
                case "system":
                    node = "systemStatus";
                    break;
            }
            textView.setText(token);
        }
        if(node.equals("status"))
        {
            speechUtils.promptForStatusKeyword();
        }
    }

    //--------------- parseStatus ----------------------------------

    /**
     * Parse the speech looking for specific keyWords
     */
    private void parseBluetooth(String[] tokens)
    {
        for(String token : tokens)
        {
            switch (token)
            {
                case "status":
                    node = "bluetoothStatus";
                    break;
                case "pair":
                    node = "bluetoothPair";
                    break;
            }
        }
        if(node.equals("bluetooth"))
        {
            speechUtils.promptForBluetoothKeyword();
        }
    }

    //--------------- parseStatus ----------------------------------

    /**
     * Parse the speech looking for specific keyWords
     */
    private void parseInternet(String[] tokens)
    {
        for(String token : tokens)
        {
            switch (token)
            {
                case "status":
                    node = "internetStatus";
                    break;
                case "connect":
                    node = "internetConnect";
                    break;
            }
        }
        if(node.equals("internet"))
        {
            speechUtils.promptForInternetKeyword();
        }
    }

    //--------------- parseStatus ----------------------------------

    /**
     * Parse the speech looking for specific keyWords
     */
    private void parseNoise(String[] tokens)
    {
        for(String token : tokens)
        {
            switch (token)
            {
                case "verbose":
                    node = "noiseVerbose";
                    break;
                case "quiet":
                    node = "noiseQuiet";
                    break;
                case "off":
                    node = "noiseOff";
                    break;
            }
        }
        if(node.equals("noise"))
        {
            speechUtils.promptForNoiseLevel();
        }
    }

    //==================================================================
    // Actions have been decided on
    //==================================================================


}
