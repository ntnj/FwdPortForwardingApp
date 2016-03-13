package com.elixsr.portforwarder.forwarding;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

/**
 * The {@link Forwarder} class represents all details shared by a protocol forwarding class.
 *
 * @author Niall McShane
 */
public abstract class Forwarder implements Callable<Void> {

    /**
     * Message to describe starting of port forwarding thread.
     */
    public static final String START_MESSAGE = "%s Port Forwarding Started from port %s to port %s";

    /**
     * Message to describe a failed binding from a Forwarding class.
     */
    public static final String BIND_FAILED_MESSAGE = "Could not bind port %s for %s Rule '%s'";

    /**
     * Message to describe a thread interruption.
     */
    public static final String THREAD_INTERRUPT_CLEANUP_MESSAGE = "%s Thread interrupted, will perform cleanup";

    /**
     * The from and target {@link InetSocketAddress} objects.
     */
    protected final InetSocketAddress from, to;

    /**
     * The name of the rule being forwarded.
     */
    protected final String ruleName;

    /**
     * The name of the protocol being used to forward.
     */
    protected final String protocol;

    public Forwarder(String protocol, InetSocketAddress form, InetSocketAddress to, String ruleName){
        this.protocol = protocol;
        this.from = form;
        this.to = to;
        this.ruleName = ruleName;
    }
}
