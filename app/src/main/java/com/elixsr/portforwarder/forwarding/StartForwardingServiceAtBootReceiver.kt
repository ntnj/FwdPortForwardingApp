package com.elixsr.portforwarder.forwarding

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager

/**
 * Created by Niall McShane on 07/06/2016.
 *
 * @see [How to start an Application on startup?](http://stackoverflow.com/questions/6391902/how-to-start-an-application-on-startup)
 */
class StartForwardingServiceAtBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action && PreferenceManager.getDefaultSharedPreferences(context.applicationContext).getBoolean("pref_start_on_boot", false)) {
            val serviceIntent = Intent(context, ForwardingService::class.java)
            context.startService(serviceIntent)
        }
    }
}