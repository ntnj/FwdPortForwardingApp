package com.elixsr.portforwarder.exceptions;

/**
 * Created by Cathan on 25/07/2017.
 */

public class RuleValidationException extends ValidationException {

    public RuleValidationException() {}

    public RuleValidationException(String message) {
        super(message);
    }
    public RuleValidationException(Throwable cause) {
        super(cause);
    }
    public RuleValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}
