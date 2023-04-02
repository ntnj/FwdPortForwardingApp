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
package com.elixsr.portforwarder.ui.intro

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.elixsr.portforwarder.R
import com.elixsr.portforwarder.ui.MainActivity
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntroFragment

/**
 * Created by Niall McShane on 12/03/2016.
 *
 *
 *
 * @see [AppIntro - Github](https://github.com/PaoloRotolo/AppIntro)
 */
class MainIntro : AppIntro2() {
    // Please DO NOT override onCreate. Use init.
    override fun init(savedInstanceState: Bundle?) {

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance(getString(R.string.app_intro_1_title), getString(R.string.app_intro_1_text), R.drawable.appintro1, ContextCompat.getColor(this, R.color.colorPrimary)))
        addSlide(AppIntroFragment.newInstance(getString(R.string.app_intro_2_title), getString(R.string.app_intro_2_text), R.drawable.appintro2, ContextCompat.getColor(this, R.color.colorPrimary)))
        addSlide(AppIntroFragment.newInstance(getString(R.string.app_intro_3_title), getString(R.string.app_intro_3_text), R.drawable.appintro3, ContextCompat.getColor(this, R.color.colorPrimary)))
        showSkipButton(false)
    }

    fun showSkipButton(showButton: Boolean) {
        skipButtonEnabled = showButton
        setButtonState(skipButton, showButton)
    }

    private fun loadMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onDonePressed() {
        // Do something when users tap on Done button.
        loadMainActivity()
    }

    override fun onSlideChanged() {
        // Do something when the slide changes.
    }

    override fun onNextPressed() {
        // Do something when users tap on Next button.
    }

    override fun onBackPressed() {
        // Do something when users tap on Next button.
    }
}