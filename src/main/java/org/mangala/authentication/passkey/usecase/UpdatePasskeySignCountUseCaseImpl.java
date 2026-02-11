package org.mangala.authentication.passkey.usecase;

import lombok.RequiredArgsConstructor;
import org.mangala.authentication.passkey.adapter.repository.UserPasskeyRepository;
import org.mangala.authentication.passkey.domain.PasskeyNotFoundException;
import org.mangala.authentication.passkey.domain.SignCountMismatchException;
import org.mangala.authentication.passkey.domain.UserPasskeyEntity;
import org.mangala.authentication.passkey.usecase.command.UpdatePasskeySignCountCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdatePasskeySignCountUseCaseImpl implements UpdatePasskeySignCountUseCase {

    private final UserPasskeyRepository userPasskeyRepository;

    @Override
    @Transactional
    public void execute(UpdatePasskeySignCountCommand command) {
        UserPasskeyEntity passkey = userPasskeyRepository.findByCredentialIdAndIsActiveTrue(command.credentialId())
                .orElseThrow(PasskeyNotFoundException::new);

        // Validate sign count to detect cloned authenticators
        // Sign count should always increase (or stay 0 for security keys that don't support it)
        long currentCount = passkey.getSighCount();
        long newCount = command.newSignCount();

        if (currentCount > 0 && newCount <= currentCount) {
            throw new SignCountMismatchException();
        }

        passkey.setSighCount((int) newCount);
        userPasskeyRepository.save(passkey);
    }
}
