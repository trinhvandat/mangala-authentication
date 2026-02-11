package org.mangala.authentication.passkey.usecase;

import lombok.RequiredArgsConstructor;
import org.mangala.authentication.passkey.adapter.repository.UserPasskeyRepository;
import org.mangala.authentication.passkey.domain.PasskeyNotFoundException;
import org.mangala.authentication.passkey.domain.UserPasskeyEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetPasskeyByCredentialIdUseCaseImpl implements GetPasskeyByCredentialIdUseCase {

    private final UserPasskeyRepository userPasskeyRepository;

    @Override
    @Transactional(readOnly = true)
    public UserPasskeyEntity execute(byte[] credentialId) {
        return userPasskeyRepository.findByCredentialIdAndIsActiveTrue(credentialId)
                .orElseThrow(PasskeyNotFoundException::new);
    }
}
