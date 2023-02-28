package dev.jlcorradi.convention.core;

import dev.jlcorradi.convention.BusinessException;

public class DuplicatedVoteException extends BusinessException {

    public static final String ERROR_MSG = "Only one vote per voter is permitted.";

    public DuplicatedVoteException() {
        super(ERROR_MSG);
    }
}
