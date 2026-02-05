package org.mangala.authentication.passkey.usecase;

import lombok.RequiredArgsConstructor;
import org.mangala.authentication.passkey.adapter.repository.PasskeyChallengeRepository;
import org.mangala.authentication.passkey.domain.PasskeyChallengeEntity;
import org.mangala.authentication.passkey.usecase.command.CreatePasskeyChallengeCommand;
import org.mangala.authentication.passkey.usecase.mapper.PasskeyChallengeCommandMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class CreatePasskeyChallengeUseCaseImpl implements CreatePasskeyChallengeUseCase {

    private final PasskeyChallengeCommandMapper mapper;
    private final PasskeyChallengeRepository passkeyChallengeRepository;

    @Override
    @Transactional
    public PasskeyChallengeEntity execute(CreatePasskeyChallengeCommand command) {
        var entity = mapper.toEntity(command);
        return passkeyChallengeRepository.save(entity);
    }
}
