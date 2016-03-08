package com.elixsr.portforwarder.exceptions;

/**
 * Created by Niall McShane on 07/03/2016.
 */
public class BindException extends Exception {

    public BindException() {
    }

    public BindException(String detailMessage) {
        super(detailMessage);
    }

    public BindException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BindException(Throwable throwable) {
        super(throwable);
    }
}
