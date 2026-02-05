package org.mangala.authentication.passkey.usecase;

import lombok.RequiredArgsConstructor;
import org.mangala.authentication.passkey.adapter.repository.PasskeyChallengeRepository;
import org.mangala.authentication.passkey.domain.ChallengeNotFoundException;
import org.mangala.authentication.passkey.domain.PasskeyChallengeEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPasskeyChallengeUseCaseImpl implements GetPasskeyChallengeUseCase {

    private final PasskeyChallengeRepository repository;

    @Override
    public PasskeyChallengeEntity execute(String sessionId) {
        return repository.findBySessionId(sessionId)
                .orElseThrow(ChallengeNotFoundException::new);
    }
}
