package com.elixsr.portforwarder.util;

/**
 * Created by Niall McShane on 07/03/2016.
 */
public class NetworkHelper {

    public static final String TCP = "TCP";
    public static final String UDP = "UDP";
    public static final String BOTH = "BOTH";

    public static boolean isValidIpv4Address(String address){
        if(address != null & address.length() > 0){
            return true;
        }

        return false;
    }
}
