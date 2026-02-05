package org.mangala.authentication.passkey.usecase;

import lombok.RequiredArgsConstructor;
import org.mangala.authentication.passkey.adapter.repository.UserPasskeyRepository;
import org.mangala.authentication.passkey.domain.UserPasskeyEntity;
import org.mangala.authentication.passkey.usecase.command.SavePasskeyCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class SavePasskeyUseCaseImpl implements SavePasskeyUseCase {

    private final UserPasskeyRepository repository;

    @Override
    public UserPasskeyEntity execute(SavePasskeyCommand command) {
        UserPasskeyEntity entity = new UserPasskeyEntity();
        entity.setUserId(command.getUserId());
        entity.setCredentialId(command.getCredentialId());
        entity.setPublicKey(command.getPublicKey());
        entity.setAlgorithm(command.getAlgorithm());
        entity.setSighCount(command.getSignCount().intValue());
        entity.setAaguid(command.getAaguid());
        entity.setTransports(command.getTransports());
        entity.setBackupEligible(command.getBackupEligible());
        entity.setBackupState(command.getBackupState());
        entity.setUserVerified(command.getUserVerified());
        entity.setAttestationFormat(command.getAttestationFormat());
        entity.setAttestationCertificate(command.getAttestationCertificate());
        entity.setDeviceName(command.getDeviceName());
        entity.setIsActive(true);
        entity.setCreatedAt(LocalDateTime.now());

        return repository.save(entity);
    }
}
