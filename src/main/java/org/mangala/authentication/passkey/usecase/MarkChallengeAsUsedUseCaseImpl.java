package org.mangala.authentication.passkey.usecase;

import lombok.RequiredArgsConstructor;
import org.mangala.authentication.passkey.adapter.repository.PasskeyChallengeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkChallengeAsUsedUseCaseImpl implements MarkChallengeAsUsedUseCase {

    private final PasskeyChallengeRepository repository;

    @Override
    public void execute(Long challengeId) {
        repository.findById(challengeId).ifPresent(challenge -> {
            challenge.setIsUsed(true);
            challenge.setUsedAt(LocalDateTime.now());
            repository.save(challenge);
        });
    }
}
