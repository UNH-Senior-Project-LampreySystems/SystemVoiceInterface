package trial.voice.app;

import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by Nick on 11/1/2015.
 */
public class SpeechUtils
{

    //--------------- instance variables ---------------------------

    private TextToSpeech tts;
    private MainActivity ma;

    //--------------- constructor ----------------------------------

    /**
     * Create the text to speech object and give it a reference to
     * the main activity for interaction
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
            }
        }
        );
    }

    //--------------- speak ----------------------------------

    /**
     * A method to encapsulate the quing model and null parameter in tts.speak
     */
    public void speak(String string)
    {
        tts.speak(string, TextToSpeech.QUEUE_FLUSH, null);
        while(tts.isSpeaking());
    }

    //==================================================================
    // This is the initial response, no action has been decided
    //==================================================================

    //--------------- initialResponse ----------------------------------

    /**
     * The default response when interaction is first started
     * Generally in response to a keyword such as "OK Google or Alexa"
     */
    public void initialResponse()
    {
        String s = "Yes?";
        speak(s);
        ma.startSpeechActivity();
    }

    //==================================================================
    // This are prompts, actions are being decided
    //==================================================================

    //--------------- promptForStatusKeyword ----------------------------------

    /**
     * The user has said status without specifying which type of status
     * Ask the user for a status type
     * Expected Results: Bluetooth, internet, system
     */
    public void promptForStatusKeyword()
    {
        String s = "Would you like to know your Bluetooth status, internet status, or system status?";
        speak(s);
        ma.startSpeechActivity();
    }

    //--------------- promptForBluetoothKeyword ----------------------------------

    /**
     * The user has said Bluetooth without specifying what to do with Bluetooth
     * Ask the user what to do with bluetooth
     * Expected Results: status, pair
     */
    public void promptForBluetoothKeyword()
    {
        String s = "Would you like to know your Bluetooth status or pair a new device?";
        speak(s);
        ma.startSpeechActivity();
    }

    //--------------- promptForInternetKeyword ----------------------------------

    /**
     * The user has said internet without specifying what to do with internet
     * Ask the user what to do with internet
     * Expected Results: status, connect
     */
    public void promptForInternetKeyword()
    {
        String s = "Would you like to know your Internet status or connect to a new network?";
        speak(s);
        ma.startSpeechActivity();
    }

    //--------------- promptForStatusKeyword ----------------------------------

    /**
     * The user has asked to change the noise level, but did not specify what to change it to.
     * Ask the user what to change the noise level to
     * Expected Results: verbose, quiet, off
     */
    public void promptForNoiseLevel()
    {
        String s = "Would you like me to be quiet, verbose, or turn off feedback altogether?";
        speak(s);
        ma.startSpeechActivity();
    }

    //--------------- restartVerification ----------------------------------

    /**
     * The user has said restart. This is generally ill advised so the user must verify their decision
     * Expected Results: yes, no
     */
    public void restartVerification()
    {
        String s = "Are you sure that you want to restart the device? Please say yes or no.";
        speak(s);
        ma.startSpeechActivity();
    }

    //==================================================================
    // These are responses, actions have been decided
    //==================================================================

}
