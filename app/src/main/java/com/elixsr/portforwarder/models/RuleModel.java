package com.elixsr.portforwarder.models;

import android.util.Log;

import java.io.Serializable;
import java.net.InetSocketAddress;

import com.elixsr.portforwarder.util.RuleHelper;

/**
 * Created by Niall McShane on 01/03/2016.
 */
public class RuleModel implements Serializable {

    private static final String TAG = "RuleModel";

    private long id;

    private boolean isTcp;

    private boolean isUdp;

    private String name;

    //TODO: create a class? - worth the effort?
    private String fromInterfaceName;

    private int fromPort;

    private InetSocketAddress target;

    //Null constructor - for object building
    public RuleModel(){

    }

    public RuleModel(boolean isTcp, boolean isUdp, String name, String fromInterfaceName, int fromPort, InetSocketAddress target){
        this.isTcp = isTcp;
        this.isUdp = isUdp;
        this.name = name;
        this.fromInterfaceName = fromInterfaceName;
        this.fromPort = fromPort;
        this.target = target;
    }

    public RuleModel(boolean isTcp, boolean isUdp, String name, String fromInterfaceName, int fromPort, String targetIp, int targetPort){
        this(isTcp, isUdp, name, fromInterfaceName, fromPort, new InetSocketAddress(targetIp, targetPort));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isTcp() {
        return isTcp;
    }

    public void setIsTcp(boolean isTcp) {
        this.isTcp = isTcp;
    }

    public boolean isUdp() {
        return isUdp;
    }

    public void setIsUdp(boolean isUdp) {
        this.isUdp = isUdp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFromInterfaceName() {
        return fromInterfaceName;
    }

    public void setFromInterfaceName(String fromInterfaceName) {
        this.fromInterfaceName = fromInterfaceName;
    }

    public int getFromPort() {
        return fromPort;
    }

    public void setFromPort(int fromPort) {
        this.fromPort = fromPort;
    }

    public InetSocketAddress getTarget() {
        return target;
    }

    public void setTarget(InetSocketAddress target) {
        this.target = target;
    }

    public String protocolToString(){
        return RuleHelper.getRuleProtocolFromModel(this);
    }

    public String getTargetIpAddress(){
        return this.target.getAddress().getHostAddress();
    }

    public int getTargetPort(){
        return this.target.getPort();
    }

    public boolean isValid(){

        //ensure the rule has a name
        if(name == null || name.length() <= 0){
            return false;
        }

        // it must either be one or the other, or even both
        if(!isTcp && !isUdp){
            return false;
        }

        if(fromInterfaceName  == null || fromInterfaceName.length() <= 0){
            return false;
        }

        if(fromPort < RuleHelper.MIN_PORT_VALUE || fromPort > RuleHelper.MAX_PORT_VALUE){
            return false;
        }

        try{
            //ensure that the value is greater than the minimum, and smaller than max
            if(getTargetPort() <= 0 || getTargetPort() < RuleHelper.TARGET_MIN_PORT || getTargetPort() > RuleHelper.MAX_PORT_VALUE){
                return false;
            }
        }catch(NullPointerException e){
            Log.e(TAG, "Target object was null.", e);
            return false;
        }


        //the new rule activity should take care of IP address validation
        if(getTargetIpAddress() == null || name.length() <= 0){
            return false;
        }

        return true;

    }
}
