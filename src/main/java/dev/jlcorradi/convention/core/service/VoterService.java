package dev.jlcorradi.convention.core.service;

import dev.jlcorradi.convention.core.VoterIdDuplicationException;
import dev.jlcorradi.convention.core.dto.RegisterVoterDTO;

public interface VoterService {
    void registerVoter(RegisterVoterDTO registerVoterDTO) throws VoterIdDuplicationException;
}
