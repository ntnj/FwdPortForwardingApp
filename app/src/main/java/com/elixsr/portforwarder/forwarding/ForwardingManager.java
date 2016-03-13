package com.elixsr.portforwarder.forwarding;

import java.io.Serializable;

/**
 * The {@link ForwardingManager} class encapsulates all meta data related to the status of
 * forwarding throughout the application.
 *
 * The class is a singleton, and can be accessed by any object to query the current status of
 * forwarding.
 */
public class ForwardingManager implements Serializable {

    private static ForwardingManager instance = null;

    private ForwardingManager(){

    }

    /**
     * Return an instance of the {@link ForwardingManager} class.
     * @return
     */
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
