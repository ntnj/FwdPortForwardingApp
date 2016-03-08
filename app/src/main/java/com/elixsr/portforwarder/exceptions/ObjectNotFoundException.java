package com.elixsr.portforwarder.exceptions;

/**
 * Created by Niall McShane on 07/03/2016.
 */
public class ObjectNotFoundException extends Exception {

    public ObjectNotFoundException() {
    }

    public ObjectNotFoundException(String detailMessage) {
        super(detailMessage);
    }

    public ObjectNotFoundException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ObjectNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
