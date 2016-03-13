package com.elixsr.portforwarder.util;

/**
 * The {@link NetworkHelper} class provides static objects and methods related to network metadata.
 *
 * @author Niall McShane
 */
public class NetworkHelper {

    /**
     * A String denoting TCP.
     */
    public static final String TCP = "TCP";

    /**
     * A String denoting UDP.
     */
    public static final String UDP = "UDP";

    /**
     * A String denoting BOTH.
     */
    public static final String BOTH = "BOTH";

    /**
     * Return whether or not an IPv4 Address is valid.
     * @param address The IPv4 Address
     * @return true if valid, false if not valid.
     */
    @Deprecated
    public static boolean isValidIpv4Address(String address){
        if(address != null & address.length() > 0){
            return true;
        }

        return false;
    }
}
