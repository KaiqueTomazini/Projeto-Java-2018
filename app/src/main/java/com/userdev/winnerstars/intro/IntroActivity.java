package com.userdev.winnerstars.intro;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro2;
import com.userdev.winnerstars.WinnerStars;
import com.userdev.winnerstars.utils.Constants;

public class IntroActivity extends AppIntro2 {

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(new IntroSlide1(), getApplicationContext());
        addSlide(new IntroSlide1(), getApplicationContext());
        /*addSlide(new IntroSlide2(), getApplicationContext());
        addSlide(new IntroSlide3(), getApplicationContext());
        addSlide(new IntroSlide4(), getApplicationContext());
        addSlide(new IntroSlide5(), getApplicationContext());
        addSlide(new IntroSlide6(), getApplicationContext());*/
    }


    @Override
    public void onDonePressed() {
        WinnerStars.getSharedPreferences().edit()
                .putBoolean(Constants.PREF_TOUR_COMPLETE, true).apply();
        finish();
    }


    public static boolean mustRun() {
        return  //!WinnerStars.isDebugBuild() &&
                !WinnerStars.getSharedPreferences().getBoolean(Constants.PREF_TOUR_COMPLETE, false);
    }


    @Override
    public void onBackPressed() {
        // Does nothing, you HAVE TO SEE THE INTRO!
    }
}
