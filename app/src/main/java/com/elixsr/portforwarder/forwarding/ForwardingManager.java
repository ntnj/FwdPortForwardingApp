package com.elixsr.portforwarder.forwarding;

import java.io.Serializable;

/**
 * Created by Niall McShane on 04/03/2016.
 */
public class ForwardingManager implements Serializable {

    private static ForwardingManager instance = null;

    private ForwardingManager(){

    }

    public static ForwardingManager getInstance() {
        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
            synchronized (ForwardingManager.class) {
                if (instance == null) {
                    instance = new ForwardingManager();
                }
            }
        }
        return instance;
    }

    private boolean isEnabled = false;

    public boolean isEnabled() {
        return isEnabled;
    }

    protected void enableForwarding(){
        this.isEnabled = true;
    }

    protected void disableForwarding(){
        this.isEnabled = false;
    }

}
