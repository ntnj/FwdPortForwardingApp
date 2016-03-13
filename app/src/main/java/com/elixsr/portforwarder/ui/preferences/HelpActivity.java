package com.elixsr.portforwarder.ui.preferences;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.elixsr.portforwarder.R;
import com.elixsr.portforwarder.ui.BaseActivity;
import com.elixsr.portforwarder.ui.BaseWebActivity;

/**
 * Created by Niall McShane on 08/03/2016.
 */
public class HelpActivity extends BaseWebActivity {

    private static final String URL = "http://support.elix.sr/#!/products/fwd";
    private static final String TITLE = "Help";

    public HelpActivity() {
        super(URL, TITLE);
    }
}
