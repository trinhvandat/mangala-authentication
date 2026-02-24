package org.mangala.authentication.user.usecase;

import lombok.RequiredArgsConstructor;
import org.mangala.authentication.user.adapter.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CheckUserExistedUseCaseImpl implements CheckUserExistedUseCase {
    private final UserRepository userRepository;

    @Override
    public boolean execute(String email) {
        return userRepository.existsByEmail(email);
    }
}
