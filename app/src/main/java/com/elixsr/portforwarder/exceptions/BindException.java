package com.elixsr.portforwarder.exceptions;

/**
 * Thrown when an attempt to bind a socket fails.
 */
public class BindException extends Exception {

    /**
     * {@inheritDoc}
     */
    public BindException() {
    }

    /**
     * {@inheritDoc}
     */
    public BindException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * {@inheritDoc}
     */
    public BindException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * {@inheritDoc}
     */
    public BindException(Throwable throwable) {
        super(throwable);
    }
}
