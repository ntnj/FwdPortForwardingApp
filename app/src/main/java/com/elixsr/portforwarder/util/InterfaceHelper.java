package com.elixsr.portforwarder.util;

import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Cathan on 06/03/2017.
 */

public class InterfaceHelper {

    private static final String TAG = "InterfaceHelper";

    /**
     * Returns a list of all Network interfaces located on the device.
     * @return a String list containing the name of the network interfaces on the device.
     * @throws SocketException
     */
    public static List<String> generateInterfaceNamesList() throws SocketException {

        //create an empty list
        List<String> interfaces = new ArrayList<String>();

        String address = null;
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface intf = en.nextElement();

            //while we have more elements
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {

                //get the next address in from the iterator
                InetAddress inetAddress = enumIpAddr.nextElement();

                address = new String(inetAddress.getHostAddress().toString());

                if (address != null & address.length() > 0 && inetAddress instanceof Inet4Address) {

                    Log.i(TAG, intf.getDisplayName() + " " + address);
                    interfaces.add(intf.getDisplayName());
                }
            }
        }
        return interfaces;
    }

    public static List<InterfaceModel> generateInterfaceModelList() throws SocketException {

        //create an empty list
        List<InterfaceModel> interfaces = new ArrayList<>();

        String address = null;
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface intf = en.nextElement();

            //while we have more elements
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {

                //get the next address in from the iterator
                InetAddress inetAddress = enumIpAddr.nextElement();

                address = new String(inetAddress.getHostAddress().toString());

                if (address != null & address.length() > 0 && inetAddress instanceof Inet4Address) {
                    interfaces.add(new InterfaceModel(intf.getDisplayName(), inetAddress));
                }
            }
        }
        return interfaces;
    }

    public static class InterfaceModel {
        private String name;
        private InetAddress inetAddress;

        public InterfaceModel(String name, InetAddress inetAddress) {
            this.name = name;
            this.inetAddress = inetAddress;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public InetAddress getInetAddress() {
            return inetAddress;
        }

        public void setInetAddress(InetAddress inetAddress) {
            this.inetAddress = inetAddress;
        }
    }


}
