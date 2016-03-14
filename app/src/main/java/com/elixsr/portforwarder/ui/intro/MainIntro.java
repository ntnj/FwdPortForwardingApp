/*
 * Fwd: the port forwarding app
 * Copyright (C) 2016  Elixsr Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
 *
 * @see <a href="https://github.com/PaoloRotolo/AppIntro">AppIntro - Github</a>
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