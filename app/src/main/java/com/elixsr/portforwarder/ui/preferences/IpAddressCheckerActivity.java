package com.elixsr.portforwarder.ui.preferences;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elixsr.portforwarder.R;
import com.elixsr.portforwarder.ui.BaseActivity;
import com.elixsr.portforwarder.util.InterfaceHelper;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class IpAddressCheckerActivity extends BaseActivity {


    List<InterfaceHelper.InterfaceModel> interfaces = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipaddress_checker);

        Toolbar toolbar = getActionBarToolbar();
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            this.interfaces = InterfaceHelper.generateInterfaceModelList();
        } catch (SocketException e) {
            Toast.makeText(this, "Could not retrieve interfaces", Toast.LENGTH_SHORT).show();
            finish();
        }

        LinearLayout containerLayout = (LinearLayout) findViewById(R.id.container);
        containerLayout.removeAllViews();

        for (InterfaceHelper.InterfaceModel interfaceModel : interfaces) {

            // Set up the view
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.ip_address_status, containerLayout, false);
            containerLayout.addView(view);

            TextView interfaceNameView = (TextView) view.findViewById(R.id.interface_name);
            TextView interfaceIpAddressView = (TextView) view.findViewById(R.id.interface_ip_address);
            TextView interfaceStatusView = (TextView) view.findViewById(R.id.interface_status);

            interfaceNameView.setText(interfaceModel.getName());
            interfaceIpAddressView.setText(interfaceModel.getInetAddress().getHostAddress());
            if (interfaceModel.getInetAddress().isSiteLocalAddress() || interfaceModel.getInetAddress().isLoopbackAddress()) {
                interfaceStatusView.setText(R.string.ip_checker_tool_private_text);
            } else {
                interfaceStatusView.setText(R.string.ip_checker_tool_public_text);
            }

        }

    }
}