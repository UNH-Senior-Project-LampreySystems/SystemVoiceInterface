package trial.bluetooth.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Nick on 10/19/2015.
 */
public class SpeechUtils extends Activity
{
    private TextToSpeech tts;
    private Intent intent;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private MainActivity ma;
    private int device = 0;

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

    }

    public void listDevices1(ArrayList<String> deviceList) {
        tts.speak(deviceList.get(device), TextToSpeech.QUEUE_FLUSH, null);
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ma.getConfirmation();
    }

    public void listDevices2(ArrayList<String> deviceList)
    {
        for(String s : deviceList)
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
    }

    public int getDevice()
    {
        return device;
    }

    public void incrementDevice()
    {
        device++;
    }
}
