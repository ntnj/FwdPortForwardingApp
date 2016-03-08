package com.elixsr.portforwarder.forwarding;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

/**
 * Created by Niall McShane on 21/02/2016.
 */
public abstract class Forwarder implements Callable<Void> {
    protected final InetSocketAddress from, to;
    protected final String ruleName;

    public Forwarder(InetSocketAddress form, InetSocketAddress to, String ruleName){
        this.from = form;
        this.to = to;
        this.ruleName = ruleName;
    }
}
