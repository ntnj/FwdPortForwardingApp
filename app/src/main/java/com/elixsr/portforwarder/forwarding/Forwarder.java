package com.elixsr.portforwarder.forwarding;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

/**
 * Created by Niall McShane on 21/02/2016.
 */
public abstract class Forwarder implements Callable<Void> {

    /**
     * Message to describe starting of port forwarding thread.
     */
    public static final String START_MESSAGE = "%s Port Forwarding Started from port %s to port %s";
    public static final String BIND_FAILED_MESSAGE = "Could not bind port %s for %s Rule '%s'";
    public static final String THREAD_INTERRUPT_CLEANUP_MESSAGE = "%s Thread interrupted, will perform cleanup";

    protected final InetSocketAddress from, to;
    protected final String ruleName;
    public final String protocol;

    public Forwarder(String protocol, InetSocketAddress form, InetSocketAddress to, String ruleName){
        this.protocol = protocol;
        this.from = form;
        this.to = to;
        this.ruleName = ruleName;
    }
}
