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
package com.elixsr.portforwarder.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.elixsr.portforwarder.R

/**
 * Created by Niall McShane on 08/03/2016.
 */
abstract class BaseWebActivity(private val url: String) : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_web_activity)

        // Set up toolbar
        val toolbar = actionBarToolbar
        setSupportActionBar(toolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_close_24dp)
        toolbar.setNavigationOnClickListener { v: View? -> onBackPressed() }
        val webView = findViewById<WebView>(R.id.help_webview)
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.webViewClient = MyWebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE

        // Hardware acceleration for web view
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView.loadUrl(url)
    }

    // Override default behaviour of the browser
    private class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }
}