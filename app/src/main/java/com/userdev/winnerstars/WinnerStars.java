package com.userdev.winnerstars;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;

import com.userdev.winnerstars.utils.Constants;

public class WinnerStars extends MultiDexApplication {

    public static Context mContext;

    static SharedPreferences prefs;

    public static String WEBSERVICE = "";


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_MULTI_PROCESS);

        /*if (isDebugBuild()) {
            StrictMode.enableDefaults();
        }*/
    }

    public static Context getAppContext() {
        return WinnerStars.mContext;
    }

    @NonNull
    public static boolean isDebugBuild() {
        return BuildConfig.BUILD_TYPE.equals("debug");
    }

    public static SharedPreferences getSharedPreferences() {
        return getAppContext().getSharedPreferences(Constants.PREFS_NAME, MODE_MULTI_PROCESS);
    }

    public static void trocarWebservice(Activity activity) {
        activity.startActivity(new Intent(activity, RegistroActivity.class));
        activity.finish();
    }
}
