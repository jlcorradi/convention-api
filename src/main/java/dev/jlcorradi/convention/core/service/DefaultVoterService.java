package dev.jlcorradi.convention.core.service;

import dev.jlcorradi.convention.core.VoterIdDuplicationException;
import dev.jlcorradi.convention.core.dto.RegisterVoterDTO;
import dev.jlcorradi.convention.core.model.Voter;
import dev.jlcorradi.convention.core.repository.VoterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultVoterService implements VoterService {

    private final VoterRepository voterRepository;

    @Override
    public void registerVoter(RegisterVoterDTO registerVoterDTO) throws VoterIdDuplicationException {
        if (voterRepository.existsById(registerVoterDTO.getId())) {
            throw new VoterIdDuplicationException();
        }
        Voter voter = Voter.builder()
                .id(registerVoterDTO.getId())
                .name(registerVoterDTO.getName())
                .email(registerVoterDTO.getEmail())
                .build();

        voterRepository.save(voter);
    }
}
