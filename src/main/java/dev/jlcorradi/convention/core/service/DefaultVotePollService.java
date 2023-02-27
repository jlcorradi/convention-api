package dev.jlcorradi.convention.core.service;

import dev.jlcorradi.convention.core.DuplicatedVoteException;
import dev.jlcorradi.convention.core.SessionClosedException;
import dev.jlcorradi.convention.core.UnregisteredVoterException;
import dev.jlcorradi.convention.core.dto.RegisterVoteDTO;
import dev.jlcorradi.convention.core.model.ConventionSession;
import dev.jlcorradi.convention.core.model.VotePoll;
import dev.jlcorradi.convention.core.model.Voter;
import dev.jlcorradi.convention.core.repository.VotePollRepository;
import dev.jlcorradi.convention.core.repository.VoterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultVotePollService implements VotePollService {

    private final ConventionSessionCache conventionSessionCache;
    private final VotePollRepository votePollRepository;
    private final VoterRepository voterRepository;

    @Override
    public void registerVote(RegisterVoteDTO registerVoteDTO) throws UnregisteredVoterException, SessionClosedException,
            DuplicatedVoteException {
        ConventionSession activeSession = conventionSessionCache.getActiveSession(registerVoteDTO.getConventionSessionId());

        Voter voter = voterRepository.findById(registerVoteDTO.getVoterId())
                .orElseThrow(UnregisteredVoterException::new);

        if (votePollRepository.findByVoterAndConventionSession(voter, activeSession)
                .isPresent()) {
            throw new DuplicatedVoteException();
        }

        VotePoll votePoll = VotePoll.builder()
                .voter(voter)
                .conventionSession(activeSession)
                .vote(registerVoteDTO.isVote())
                .build();
        votePollRepository.save(votePoll);
    }

}
