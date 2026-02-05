package org.mangala.authentication.passkey.usecase;

import lombok.RequiredArgsConstructor;
import org.mangala.authentication.passkey.adapter.repository.UserPasskeyRepository;
import org.mangala.authentication.passkey.domain.DuplicateCredentialException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CheckDuplicateCredentialUseCaseImpl implements CheckDuplicateCredentialUseCase {

    private final UserPasskeyRepository repository;

    @Override
    public void execute(byte[] credentialId) {
        if (repository.existsByCredentialId(credentialId)) {
            throw new DuplicateCredentialException();
        }
    }
}
