package com.elixsr.portforwarder.ui.intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.elixsr.portforwarder.ui.MainActivity;
import com.elixsr.portforwarder.R;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Niall McShane on 12/03/2016.
 * <p/>
 * Uses: https://github.com/PaoloRotolo/AppIntro
 */
public class MainIntro extends AppIntro2 {

    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("Add Rules", "Create and edit port forwarding rules", R.drawable.ic_appintro1graphic, ContextCompat.getColor(this, R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Start and Stop", "Easily start and stop forwarding of all rules with a tap", R.drawable.ic_appintro2graphic, ContextCompat.getColor(this, R.color.colorPrimary)));
    }

    private void loadMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

//    @Override
//    public void onSkipPressed() {
//        // Do something when users tap on Skip button.
//        loadMainActivity();
//    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        loadMainActivity();
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

}