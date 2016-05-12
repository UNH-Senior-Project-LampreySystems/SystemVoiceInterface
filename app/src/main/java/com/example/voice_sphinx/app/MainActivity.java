package com.example.voice_sphinx.app;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import edu.cmu.pocketsphinx.*;

public class MainActivity extends AppCompatActivity implements RecognitionListener {

    private static final String KWS_SEARCH = "wakeup";
    private static final String GRAMMAR_SEARCH = "grammar";
    private static final String ALL_SEARCH = "all";
    private static final String KEYPHRASE = "wakeup phone";

    private TextView textView;
    private SpeechRecognizer recognizer;

    private int node = 0;
    private SpeechUtils speechUtils;
    private String password = "";
    private String networkName = "";

    private boolean startup = true;
    //==============================================
    // Required setup for Activity extension
    //==============================================

    //------------------ onCreate ------------------
    /**
     * Set the layout and create the intent for speech recognition
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-------------- Testing Stuff --------------
        textView = (TextView) findViewById(R.id.textView);

        try
        {
            Assets assets = new Assets(MainActivity.this);
            File assetDir = assets.syncAssets();

            setupRecognizer(assetDir);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        switchSearch(KWS_SEARCH);

        speechUtils = new SpeechUtils(this);
    }

    private void setupRecognizer(File assetsDir)
    {
        // create the recognizer
        try {
            recognizer = defaultSetup()
                    .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                    .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                    .setRawLogDir(assetsDir).setKeywordThreshold(Float.MIN_VALUE)
                    .setBoolean("-allphone_ci",true)
                    .getRecognizer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // connect the recognizer with this app
        recognizer.addListener(this);

        // set a search for a keyphrase
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);

        // set a search for the grammar keywords
        File fileG = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(GRAMMAR_SEARCH, fileG);

        // set a search for all words in english
        File fileA = new File(assetsDir, "ssid.gram");
        recognizer.addGrammarSearch(ALL_SEARCH, fileA);
    }

    //------------------ onCreateOptionsMenu --------
    /**
     * Set to default
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //------------------ onOptionsItemSelected -----
    /**
     * Set to default
     */
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

    //------------------ onDestroy -----
    /**
     * Kill the recognizer
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        recognizer.cancel();
        recognizer.shutdown();
    }

    public void destroyRecognizer()
    {
        recognizer.cancel();
        recognizer.shutdown();
    }

    //======================================
    // stuffForTheListener
    //======================================

    public void switchSearch(String searchName)
    {
        // stop the recognizer
        recognizer.stop();

        // start the recognizer with a specific search target
        if(searchName.equals(KWS_SEARCH))
            recognizer.startListening(searchName);          // listen for begining keyphrase
        else
        {
            textView.setText("STARTED");
            recognizer.startListening(searchName, 5000);    // listen to everything
        }

    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {
        textView.setText("END");
        String s = recognizer.getSearchName();
        if (!s.equals(KWS_SEARCH))
        {
            recognizer.stop();
            switchSearch(s);
        }
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if(hypothesis == null)
            return;

        String text = hypothesis.getHypstr();
        if(text.equals(KEYPHRASE))
        {
            textView.setText("Start");
            speechUtils.helpMenu0();
            switchSearch(GRAMMAR_SEARCH);
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if(hypothesis == null) {
            return;
        }

        recognizer.stop();


        String result = hypothesis.getHypstr();
        String[] tokens = result.split("[ ]+");
        if(tokens[0].equals("cancel"))
            speechUtils.cancel();
        else
            parseToDepth(tokens);
        textView.setText(result);
    }

    @Override
    public void onError(Exception e) {
        textView.setText(e.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(GRAMMAR_SEARCH);
    }

    // ========================================
    // Required by speech Utils
    // ========================================
    public void start()
    {
        switchSearch(GRAMMAR_SEARCH);
    }

    public void startAmbiguous()
    {
        switchSearch(ALL_SEARCH);
    }

    public void resetNode()
    {
        node = 0;
        password = "";
        switchSearch(KWS_SEARCH);
    }

    //==============================================
    // More Parsing
    //==============================================

    public void parseToDepth(String[] tokens)
    {
        switch(node)
        {
            case 0:     // Speech Interaction is just starting
                parseToStart(tokens);
                return;
            case 1:     // Interact with the internet
                parseTo1(tokens);
                return;
            case 12:     // Connect to a new network
                parseTo12(tokens);
                return;
            case 1211:   // List through networks
                parseTo1211(tokens);
                return;
            case 122:   // The name of the network is known
                parseTo122(tokens);
                return;
            case 1221: // Confirm the known network name
                parseTo1221(tokens);
                return;
            case 1212: // Internet needs a password
                parseTo1212(tokens);
                return;
            case 3:     // Interact with the system
                parseTo3(tokens);
                return;
            case 32:
                parseTo32(tokens);
                return;
            case 33:
                parseTo33(tokens);
                return;
        }
    }

    public void parseToStart(String[] tokens)
    {
        for(String s : tokens)
        {
            switch(s)
            {
                case "internet":
                    node = 1;
                    parseTo1(tokens);
                    return;
                case "system":
                    node = 3;
                    parseTo3(tokens);
                    return;
            }
        }

        if(node == 0)
        {
                speechUtils.helpMenu0();
        }
    }

    //==============================================
    // Parse for the Internet
    //==============================================

    public void parseTo1(String[] tokens)
    {
        for(String s : tokens)
        {
            switch(s)
            {
                case "status":
                    node = 11;
                    speechUtils.reply11();
                    return;
                case "connect":
                    node = 12;
                    speechUtils.reply12();
                    return;
            }
        }

        if(node == 1)
        {
                speechUtils.helpMenu1();
        }
    }

    public void parseTo12(String[] tokens)
    {
        for(String s : tokens)
        {
            switch(s)
            {
                case "no":
                    node = 1211;
                    speechUtils.reply121();
                    return;
                case "yes":
                    node = 122;
                    networkName = "";
                    speechUtils.reply122();
                    return;
            }
        }

        if(node == 12)
        {
                speechUtils.helpMenu12();
        }
    }

    public void parseTo1211(String[] tokens)
    {
        for(String s : tokens)
        {
            switch(s)
            {
                case "skip":
                    node = 1211;
                    speechUtils.reply1211();
                    return;
                case "connect":
                    node = 1212;
                    speechUtils.reply1212();
                    return;
            }
        }

        if(node == 1211)
        {
                speechUtils.helpMenu121();
        }
    }

    public void parseTo122(String[] tokens)
    {
        for(String s : tokens)
        {
            if(s.equals("done"))
            {
                String temp = networkName;
                node = 1221;
                speechUtils.reply1221(temp);
                return;
            }
            else if(!isPunctuation(s).equals(""))
                networkName += isPunctuation(s);
            else if(!(isNumber(s) == -1))
                networkName += isNumber(s);
            else
                networkName += s.substring(0,1);
        }

        switchSearch(ALL_SEARCH);
    }

    public void parseTo1221(String[] tokens)
    {
        for(String s : tokens)
        {
            switch(s)
            {
                case "no":
                    node = 122;
                    speechUtils.reply122();
                    return;
                case "yes":
                    node = 1212;
                    speechUtils.reply1212();
                    return;
            }
        }

        if(node == 1221)
        {
                speechUtils.reply1221(password);
        }
    }

    public void parseTo1212(String[] tokens)
    {
        String s = tokens[0];

        switch(s)
        {
            case "capital":case "capitol":
            password += tokens[1].substring(0, 1).toUpperCase();
            speechUtils.reply12121Helper1(password);
            return;
            case "clear":
                password = "";
                speechUtils.reply12121Helper0();
                return;
            case "cancel":
                password = "";
                resetNode();
                return;
            case "back":
                if(password.length() >= 1)
                    password = password.substring(0,password.length()-1);
                speechUtils.reply12121Helper1(password);
                return;
            case "done":
                String temp = password;
                password = "";
                speechUtils.reply12121(temp);
                return;
            case "help":
                speechUtils.helpMenu12121();
                return;
            default:
                if(isNumber(tokens[0])!=-1)
                    password += isNumber(tokens[0]);
                else if(!isPunctuation(tokens[0]).equals(""))
                    password += isPunctuation(tokens[0]);
                else
                    password += s.substring(0, 1);
                speechUtils.reply12121Helper1(password);
                return;
        }
    }

    public int isNumber(String s)
    {
        switch(s)
        {
            case "zero":
            return 0;
            case "one":
            return 1;
            case "two":case "too":case "to":
            return 2;
            case "three":
            return 3;
            case "four":case "for":
            return 4;
            case "five":
            return 5;
            case "six":
            return 6;
            case "seven":
            return 7;
            case "eight":case"ate":
            return 8;
            case "nine":
            return 9;
            default:
                return -1;
        }
    }

    public String isPunctuation(String s)
    {
        if(s.length() == 1)
            return s;

        switch(s)
        {
            case "question":
                return "?";
            case "asterisk":
                return "*";
            case "underscore":
                return "_";
            case "plus":
                return "+";
            case "dash":
                return "-";
        }

        return "";
    }

    //==============================================
    // Parse for System
    //==============================================

    public void parseTo3(String[] tokens)
    {
        for(String s : tokens)
        {
            switch(s)
            {
                case "status":
                    node = 31;
                    speechUtils.reply31();
                    return;
                case "noise":case "verbosity":
                node = 32;
                speechUtils.reply32();
                return;
                case "reset":
                    node = 33;
                    speechUtils.reply33();
                    return;
            }
        }

        if(node == 3)
        {
                speechUtils.helpMenu3();
        }
    }

    public void parseTo32(String[] tokens)
    {
        for(String s : tokens)
        {
            switch(s)
            {
                case "quiet":
                    node = 321;
                    speechUtils.reply321(false);
                    return;
                case "verbose":
                    node = 321;
                    speechUtils.reply321(true);
                    return;
            }
        }

        if(node == 32)
        {
                speechUtils.helpMenu32();
        }
    }

    public void parseTo33(String[] tokens)
    {
        for(String s : tokens)
        {
            switch(s)
            {
                case "no":
                    node = 331;
                    speechUtils.reply331(false);
                    return;
                case "yes":
                    node = 331;
                    recognizer.cancel();
                    recognizer.shutdown();
                    recognizer.removeListener(MainActivity.this);
                    speechUtils.reply331(true);
                    return;
            }
        }

        if(node == 33)
        {
                speechUtils.helpMenu33();
        }
    }

}