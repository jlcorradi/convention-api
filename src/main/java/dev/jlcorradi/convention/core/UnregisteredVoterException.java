package dev.jlcorradi.convention.core;

import dev.jlcorradi.convention.BusinessException;

public class UnregisteredVoterException extends BusinessException {

    public static final String ERROR_MSG = "Only registered voters are allowed.";

    public UnregisteredVoterException() {
        super(ERROR_MSG);
    }
}
