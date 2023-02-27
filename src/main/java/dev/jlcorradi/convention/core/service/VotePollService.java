package dev.jlcorradi.convention.core.service;

import dev.jlcorradi.convention.core.SessionClosedException;
import dev.jlcorradi.convention.core.UnregisteredVoterException;
import dev.jlcorradi.convention.core.dto.RegisterVoteDTO;

public interface VotePollService {
    void registerVote(RegisterVoteDTO registerVoteDTO) throws UnregisteredVoterException, SessionClosedException;
}
