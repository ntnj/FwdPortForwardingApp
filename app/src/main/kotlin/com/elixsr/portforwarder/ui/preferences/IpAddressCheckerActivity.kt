package com.elixsr.portforwarder.ui.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.elixsr.portforwarder.R
import com.elixsr.portforwarder.ui.BaseActivity
import com.elixsr.portforwarder.util.InterfaceHelper.InterfaceModel
import com.elixsr.portforwarder.util.InterfaceHelper.generateInterfaceModelList
import java.net.SocketException

class IpAddressCheckerActivity : BaseActivity() {
    var interfaces: List<InterfaceModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ipaddress_checker)
        val toolbar = actionBarToolbar
        setSupportActionBar(toolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar.setNavigationOnClickListener { v: View? -> onBackPressed() }
    }

    override fun onStart() {
        super.onStart()
        try {
            interfaces = generateInterfaceModelList()
        } catch (e: SocketException) {
            Toast.makeText(this, "Could not retrieve interfaces", Toast.LENGTH_SHORT).show()
            finish()
        }
        val containerLayout = findViewById<LinearLayout>(R.id.container)
        containerLayout.removeAllViews()
        for (interfaceModel in interfaces) {

            // Set up the view
            val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(R.layout.ip_address_status, containerLayout, false)
            containerLayout.addView(view)
            val interfaceNameView = view.findViewById<TextView>(R.id.interface_name)
            val interfaceIpAddressView = view.findViewById<TextView>(R.id.interface_ip_address)
            val interfaceStatusView = view.findViewById<TextView>(R.id.interface_status)
            interfaceNameView.text = interfaceModel.name
            interfaceIpAddressView.text = interfaceModel.inetAddress.hostAddress
            if (interfaceModel.inetAddress.isSiteLocalAddress || interfaceModel.inetAddress.isLoopbackAddress) {
                interfaceStatusView.setText(R.string.ip_checker_tool_private_text)
            } else {
                interfaceStatusView.setText(R.string.ip_checker_tool_public_text)
            }
        }
    }
}