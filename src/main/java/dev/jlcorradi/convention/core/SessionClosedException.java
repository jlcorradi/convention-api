package dev.jlcorradi.convention.core;

import dev.jlcorradi.convention.BusinessException;

public class SessionClosedException extends BusinessException {

    public static final String ERROR_MSG = "The Session identified by id #%s is already closed.";

    public SessionClosedException(Long sessionId) {
        super(ERROR_MSG.formatted(sessionId));
    }
}
