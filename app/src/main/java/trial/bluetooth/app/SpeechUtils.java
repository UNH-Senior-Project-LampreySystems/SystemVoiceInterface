package trial.bluetooth.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Nick on 10/19/2015.
 */
public class SpeechUtils extends Activity
{
    private TextToSpeech tts;
    private Intent intent;
    private MainActivity ma;

    public SpeechUtils(MainActivity mainActivity)
    {
        ma = mainActivity;

        tts = new TextToSpeech(ma.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status)
            {
                tts.setLanguage(Locale.getDefault());
            }
        }
        );

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
    }

    public void listDevices1(ArrayList<String> deviceList) {
        for (int i = 0; i< deviceList.size(); i++) {
            tts.speak(deviceList.get(i), TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
