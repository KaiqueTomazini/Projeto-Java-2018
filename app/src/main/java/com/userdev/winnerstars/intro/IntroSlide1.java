package com.userdev.winnerstars.intro;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.userdev.winnerstars.R;

public class IntroSlide1 extends IntroFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        background.setBackground(getResources().getDrawable(R.drawable.shape_radial));
        title.setText(R.string.intro1_titulo);
        image.setImageResource(R.drawable.logo);
        description.setText(R.string.intro1_desc);
    }
}