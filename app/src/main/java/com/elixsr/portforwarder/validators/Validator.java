package com.elixsr.portforwarder.validators;

import com.elixsr.portforwarder.exceptions.ValidationException;

/**
 * Created by Cathan on 25/07/2017.
 */

public interface Validator<T> {

    boolean validate(T type) throws ValidationException;

}
