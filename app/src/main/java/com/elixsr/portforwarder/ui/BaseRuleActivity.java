package com.elixsr.portforwarder.ui;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import com.elixsr.portforwarder.R;
import com.elixsr.portforwarder.models.RuleModel;
import com.elixsr.portforwarder.util.IpAddressValidator;
import com.elixsr.portforwarder.util.NetworkHelper;
import com.elixsr.portforwarder.util.RuleHelper;

/**
 * Created by Niall McShane on 02/03/2016.
 */
public abstract class BaseRuleActivity extends BaseActivity {

    private static final String TAG = "BaseRuleActivity";
    private static final String NO_PORT_INCLUDED_ERROR_MESSAGE = "Please enter a value greater than " + RuleHelper.MIN_PORT_VALUE;
    protected Spinner protocolSpinner;
    protected Spinner fromInterfaceSpinner;
    protected ArrayAdapter<String> fromSpinnerAdapter;
    protected ArrayAdapter<CharSequence> protocolAdapter;

    public void constructDetailUi(){

        //set up protocol spinner/dropdown
        protocolSpinner = (Spinner) findViewById(R.id.protocol_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        protocolAdapter = ArrayAdapter.createFromResource(this,
                R.array.rule_protocol_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        protocolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the protocolAdapter to the spinner
        protocolSpinner.setAdapter(protocolAdapter);

        //generate interfaces
        List<String> interfaces = null;
        try {
            interfaces = generateInterfaceList();
        } catch (SocketException e) {
            Log.i(TAG, "Error generating Interface list", e);

            //TODO: add better exception handling
        }

        //set up protocol spinner/dropdown
        fromInterfaceSpinner = (Spinner) findViewById(R.id.from_interface_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        fromSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.my_spinner, interfaces);

        // Specify the layout to use when the list of choices appears
        fromSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the protocolAdapter to the spinner
        fromInterfaceSpinner.setAdapter(fromSpinnerAdapter);

    }

    public List<String> generateInterfaceList() throws SocketException {

        List<String> interfaces = new LinkedList<String>();

        String address= null;
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface intf = en.nextElement();

            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {

                InetAddress inetAddress = enumIpAddr.nextElement();

                address = new String(inetAddress.getHostAddress().toString());

                if(address != null & address.length() > 0 && inetAddress instanceof Inet4Address){

                    Log.i(TAG, intf.getDisplayName() + " " + address);
                    interfaces.add(intf.getDisplayName());
                }
            }
        }
        return interfaces;
    }

    public RuleModel generateNewRule(){

        //TODO: create a service, and split this up - too many responsibilities
        //TODO: add validation checks - for input

        //create the blank rule object
        RuleModel ruleModel = new RuleModel();


        /*
            Protocol
         */
        Spinner protocolSpinner = (Spinner) findViewById(R.id.protocol_spinner);
        String selectedProtocol = protocolSpinner.getSelectedItem().toString();

        // determine the protocol
        switch(selectedProtocol){
            case NetworkHelper.TCP:
                ruleModel.setIsTcp(true);
                break;
            case NetworkHelper.UDP:
                ruleModel.setIsUdp(true);
                break;
            //if BOTH, or default - same thing I assume
            case NetworkHelper.BOTH:
            default:
                ruleModel.setIsTcp(true);
                ruleModel.setIsUdp(true);
                break;
        }

        /*
            Rule Name
         */
        TextInputEditText ruleNameText = (TextInputEditText) findViewById(R.id.new_rule_name);

        TextInputLayout ruleNameTextInputLayout = (TextInputLayout) findViewById(R.id.new_rule_name_input_layout);

        // validate the input, and show error message if wrong
        if(ruleNameText.getText() == null || ruleNameText.getText().toString().length() <= 0){
            ruleNameTextInputLayout.setErrorEnabled(true);
            ruleNameTextInputLayout.setError("You must enter a name");
            Log.e(TAG, "No rule name was included");
        }else{
            //if everything is correct, set the name
            ruleModel.setName(ruleNameText.getText().toString());
            ruleNameTextInputLayout.setErrorEnabled(false);
        }

        /*
            From port
         */
        TextInputEditText fromPortText = (TextInputEditText) findViewById(R.id.new_rule_from_port);

        // validate the input, and show error message if wrong
        if(fromPortText.getText() == null || fromPortText.getText().toString().length() <= 0){
            fromPortText.setError(NO_PORT_INCLUDED_ERROR_MESSAGE);
            Log.e(TAG, "No from port was included");
        }else if(Integer.valueOf(fromPortText.getText().toString()) < RuleHelper.MIN_PORT_VALUE){
            fromPortText.setError("Please ensure your port is above or equal to " + RuleHelper.MIN_PORT_VALUE);
            Log.e(TAG, "From port was below or equal to " + RuleHelper.MIN_PORT_VALUE);
        }else{
            //if everything is correct, set the name
            ruleModel.setFromPort(Integer.valueOf(fromPortText.getText().toString()));
        }


        /*
            Target
         */
        String targetIpAddress = null;
        int targetPort = 0;

        /*
            Target IP Address
         */
        TextInputEditText targetIpAddressText = (TextInputEditText) findViewById(R.id.new_rule_target_ip_address);


        // validate the input, and show error message if wrong
        if(targetIpAddressText.getText() == null || targetIpAddressText.getText().toString().length() <= 0){
            targetIpAddressText.setError("You must enter a target IP Address");
            Log.e(TAG, "No target IP address was included");
        }else if(!new IpAddressValidator().validate(targetIpAddressText.getText().toString())){
            //TODO: cleanup - disgusting

            //if the ip address is not valid
            targetIpAddressText.setError("Please enter a valid IP Address");
            Log.e(TAG, "Target IP address was not valid");
        }else{
            //if everything is correct, set the name
            targetIpAddress = targetIpAddressText.getText().toString();
        }

        /*
            Target port
         */
        TextInputEditText targetPortText = (TextInputEditText) findViewById(R.id.new_rule_target_port);

        // validate the input, and show error message if wrong
        if(targetPortText.getText() == null || targetPortText.getText().toString().length() <= 0){
            targetPortText.setError(NO_PORT_INCLUDED_ERROR_MESSAGE);
            Log.e(TAG, "No target port was included");
        }else if(Integer.valueOf(targetPortText.getText().toString()) < RuleHelper.MIN_PORT_VALUE){
            targetPortText.setError("Please ensure your port is above or equal to " + RuleHelper.MIN_PORT_VALUE);
            Log.e(TAG, "Target port was below or equal to " + RuleHelper.MIN_PORT_VALUE);
        }else{
            //if everything is correct, set the name
            targetPort = Integer.valueOf(targetPortText.getText().toString());
        }

        if(targetIpAddress != null && targetIpAddress.length() > 0 && targetPort >= 0){
            //create a InetSocketAddress object using data
            InetSocketAddress target = new InetSocketAddress(targetIpAddress, targetPort);
            ruleModel.setTarget(target);
        }else{
            Log.e(TAG, "Could not create Target InetSocketAddress Object");
        }

        Spinner fromInterfaceSpinner = (Spinner) findViewById(R.id.from_interface_spinner);
        String selectedFromInterface = fromInterfaceSpinner.getSelectedItem().toString();
        ruleModel.setFromInterfaceName(selectedFromInterface);

        return ruleModel;
    }


}
