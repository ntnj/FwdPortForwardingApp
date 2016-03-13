package com.elixsr.portforwarder.exceptions;

/**
 * Thrown when a required object is not found.
 */
public class ObjectNotFoundException extends Exception {

    /**
     * {@inheritDoc}
     */
    public ObjectNotFoundException() {
    }

    /**
     * {@inheritDoc}
     */
    public ObjectNotFoundException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * {@inheritDoc}
     */
    public ObjectNotFoundException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * {@inheritDoc}
     */
    public ObjectNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
