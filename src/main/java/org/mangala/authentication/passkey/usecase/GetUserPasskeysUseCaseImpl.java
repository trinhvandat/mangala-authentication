package org.mangala.authentication.passkey.usecase;

import lombok.RequiredArgsConstructor;
import org.mangala.authentication.passkey.adapter.repository.UserPasskeyRepository;
import org.mangala.authentication.passkey.domain.UserPasskeyEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserPasskeysUseCaseImpl implements GetUserPasskeysUseCase {

    private final UserPasskeyRepository userPasskeyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserPasskeyEntity> execute(UUID userId) {
        return userPasskeyRepository.findByUserIdAndIsActiveTrue(userId);
    }
}
