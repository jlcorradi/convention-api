package dev.jlcorradi.convention.core;

import dev.jlcorradi.convention.BusinessException;

public class VoterIdDuplicationException extends BusinessException {

    public static final String ERROR_MESSAGE = "The voter ID provider is already taken.";

    public VoterIdDuplicationException() {
        super(ERROR_MESSAGE);
    }
}
