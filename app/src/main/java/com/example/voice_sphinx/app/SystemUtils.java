package com.example.voice_sphinx.app;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by Nicholas on 2/29/2016.
 */
public class SystemUtils
{
    //----------------- instance variables --------
    MainActivity ma;

    //----------------- constructor --------
    SystemUtils(MainActivity mainActivity)
    {
        ma = mainActivity;
    }

    public String getStatus()
    {
        String s = "This system's ID is R2D2";
        s += "This application's version is 0.2";
        return s;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void reset()
    {
        Intent mStartActivity = new Intent(ma.getBaseContext(), MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(ma.getBaseContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)ma.getBaseContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 6000, mPendingIntent);
        ma.finishAffinity();
    }
}