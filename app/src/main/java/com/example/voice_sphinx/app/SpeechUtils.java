package com.example.voice_sphinx.app;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.speech.tts.TextToSpeech;

import java.io.*;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nicholas on 2/10/2016.
 */
public class SpeechUtils{

    //----------------- instance Variables --------

    private TextToSpeech tts;
    private MainActivity ma;

    private InternetUtils internetUtils;
    private List<ScanResult> internetResultList;
    private int internetCurrentNode = 0;
    private ScanResult internetResult;


    private SystemUtils systemUtils;
    private boolean verbose = true;

    //==============================================
    // Required setup for Speech Utils
    //==============================================

    //------------------ onCreate ------------------
    /**
     * Set the layout and create the intent for speech recognition
     */
    public SpeechUtils(MainActivity mainActivity)
    {
        ma = mainActivity;

        tts = new TextToSpeech(ma.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status)
            {
                tts.setLanguage(Locale.getDefault());
                tts.setSpeechRate(0.9f);
                String s = "I have been Reset.";
                speak(s);
            }
        }
        );

        internetUtils = new InternetUtils(ma);
        systemUtils = new SystemUtils(ma);
    }

    public void speak(String string)
    {
           tts.speak(string, TextToSpeech.QUEUE_FLUSH, null);
           while(tts.isSpeaking());
    }

    //==============================================
    // The Initial Response
    //==============================================

    public void helpMenu0()
    {
        String s = "Internet, system.";
        if(verbose)
            s = "Please say internet, or system to interact with there respective settings. Say cancel to stop the interaction at any time.";//"Would you like to interact with your internet, or system settings. Please say internet, or system.";
        speak(s);
        ma.start();
    }

    public void cancel()
    {
        String s = "Action cancelled";
        speak(s);
        ma.resetNode();
    }

    //==============================================
    // Internet Responses
    //==============================================

    //----------------- help menus --------

    public void helpMenu1()
    {
        String s = "Status, connect.";
        if(verbose)
            s = "Please say status to here your internet status, or connect to connect to a new network.";//"Would you like to know your internet status or connect to a new network?";
        speak(s);
        ma.start();
    }

    public void helpMenu12()
    {
        String s = "yes, no.";
        if(verbose)
            s = "Do you know the name of the network that you want to connect to? Please say yes or no.";
        speak(s);
        ma.start();
    }

    public void helpMenu121()
    {
        String s = internetResult.SSID + ", skip, connect.";
        if(verbose) {
            s = "Please say connect to connect to " + internetResult.SSID + ", ";
            s += "say skip to move on to the next network";
        }
        speak(s);
        ma.start();
    }

    public void helpMenu12121()
    {
        String s = "Precede capital letters with the word capital, ";
        //s += "Precede numbers with the word number, ";
        //s += "Precede special characters with the word special. ";
        s += "Say back to delete the last character spoken. ";
        s += "Say clear to delete all characters spoken. ";
        s += "Say cancel to stop connecting to this network.";
        speak(s);
        //ma.start();
        reply12121Helper0();
    }

    //----------------- return internet status --------
    public void reply11()
    {
        String s = internetUtils.getStatus();
        speak(s);
        ma.resetNode();
    }


    //----------------- connect to the internet --------

    public void reply12()
    {
        String s = "Scanning for wireless networks please wait.";
        speak(s);
        internetResultList = internetUtils.scan();
        if(internetResultList.get(0) == null)
        {
            s = "No wireless networks were found.";
            speak(s);
            ma.resetNode();
        }
        else
        {
            s = "Do you know the name of the network to which you want to connect? Please say yes or no.";
            speak(s);
            ma.start();
        }
    }

    //----------------- connect using lists --------

    public void reply121()
    {
        String s = "I will now list all the networks 1 by 1, please say connect to connect to the network or skip to list the next network.";
        speak(s);
        reply1211();
    }

    public void reply1211()
    {
        internetResult = internetResultList.get(internetCurrentNode);
        String s = internetResult.SSID;
        speak(s);

        if(internetCurrentNode < internetResultList.size()-1)
            internetCurrentNode++;
        else
            internetCurrentNode = 0;

        ma.start();
    }

    //----------------- connect using known network --------

    public void reply122()
    {
        String s = "Please spell the name of the network and say done when finished.";
        speak(s);
        ma.startAmbiguous();
    }

    public void reply1221(String name)
    {
        int best = Integer.MAX_VALUE;
        for(ScanResult sr : internetResultList)
        {
            String srName = sr.SSID.toLowerCase();

            int temp = computeLevenshteinDistance(srName, name);

            if(temp < best && srName.length() > 0)
            {
                best = temp;
                internetResult = sr;
            }

            System.out.println("temp > " + temp + " ssid > " + internetResult.SSID );
        }

        String s = "Are you sure you want to connect to " + internetResult.SSID + "?";
        speak(s);

        System.err.println(s);
        System.out.println(s);

        ma.start();
    }

    public  int computeLevenshteinDistance(CharSequence lhs, CharSequence rhs)
    {
        int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];

        for (int i = 0; i <= lhs.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= rhs.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= lhs.length(); i++)
            for (int j = 1; j <= rhs.length(); j++)
                distance[i][j] = Math.min(Math.min(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1),
                        distance[i - 1][j - 1] + ((lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1));

        return distance[lhs.length()][rhs.length()];
    }

    //--------------- Connecting ------------------------------------
    public void reply1212()
    {
        if(internetResult.capabilities.contains("WEP") || internetResult.capabilities.contains("WPA"))
        {
            reply12121Helper0();
        }
        else
        {
            String s = "I was unable to connect to the network";
            if(internetUtils.connect(internetResult))
                s = "I have successfully connected to the network";
            speak(s);
            ma.resetNode();
        }
    }

    public void reply12121Helper0()
    {
        String s = "This network requires a password.";
                s += "Please say the first character of the password or help for more options.";
        speak(s);
        ma.start();
    }

    public void reply12121Helper1(String password)
    {
        String s = "";
        int length = password.length();

        if(length > 0)
            s = getCharacterSpeech(password.charAt(password.length()-1));

        if(length == 1)
            s = "I heard, "+ s + ", say the next character, or done if the password is complete.";
        else
            s = "I heard, "+ s + ", next character.";
        speak(s);
        ma.start();
    }

    public void reply12121(String password)
    {
        boolean bool;

        System.err.println(password);
        System.out.println(password);

        if(internetResult.capabilities.contains("WEP"))
            bool = internetUtils.connectWEP(internetResult, password);
        else
            bool = internetUtils.connectWPA(internetResult, password);

        String s = "I was unable to connect to the network";
        if(bool)
            s = "I have successfully connected to the network";
        speak(s);

        ma.resetNode();
    }

    public String getCharacterSpeech(char letter)
    {
        String s = " ";

        if(letter >= 65 && letter <= 90)
        {
            s += "capital " + letter;
        }
        else if(letter >= 97 && letter <= 122)
            s += letter;
        else if(letter >= 48 && letter <= 57)
            s += letter;
        else
            s += punctiationSwitch(letter);

        return s;
    }

    public String punctiationSwitch(char l)
    {
        switch(l)
        {
            case '!':
                return "exclamation point";
            case '_':
                return "underscore";
            case '?':
                return "question mark";
            case '+':
                return "plus";
        }
        return "";
    }

    //==============================================
    // System Responses
    //==============================================

    public void helpMenu3()
    {
        String s = "Status, verbosity, reset.";
        if(verbose)
            s = "Please say status to hear your system status, verbosity to set my response verbosity, or reset to reset the application."; //"Would you like to know your system status, reduce my verbosity, or reset the system?";
        speak(s);
        ma.start();
    }

    public void helpMenu32()
    {
        String s = "quiet, verbose.";
        if(verbose)
            s = "Would you like me to be quiet or verbose? Please say quiet or verbose.";
        speak(s);
        ma.start();
    }

    public void helpMenu33()
    {
        String s = "Yes, no.";
        if(verbose)
            s = "Would you like to reset the system? Please say yes or no.";
        speak(s);
        ma.start();
    }

    public void reply31()
    {
        String s = systemUtils.getStatus();
        speak(s);
        ma.resetNode();
    }

    public void reply32()
    {
        String s = "Replies are currently quiet, would you like them to remain quiet or switch to verbose Please say verbose or quiet.";
        if(verbose)
            s = "Replies are currently verbose, would you like them to remain verbose or switch to quiet. Please say verbose or quiet.";
        speak(s);
        ma.start();
    }

    public void reply321(boolean ver)
    {
        String s = "Replies will now be quiet";
        if(ver)
            s = "Replies will now be verbose";
        speak(s);
        verbose = ver;
        ma.resetNode();
    }

    public void reply33()
    {
        String s = "reseting the system is not recommended, are you sure that this is what you want to do? Please say yes or no.";
        speak(s);
        ma.start();
    }

    public void reply331(boolean reset) {
        String s = "you have chosen to not reset the system";
        if (reset) {
            ma.destroyRecognizer();
            s = "the system will now reset";
            speak(s);
            systemUtils.reset();
        }
        else {
            speak(s);
            ma.resetNode();
        }
    }

}