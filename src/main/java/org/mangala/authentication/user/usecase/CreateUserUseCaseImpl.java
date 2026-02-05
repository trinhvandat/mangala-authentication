package org.mangala.authentication.user.usecase;

import lombok.RequiredArgsConstructor;
import org.mangala.authentication.user.adapter.repository.UserRepository;
import org.mangala.authentication.user.domain.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepository userRepository;

    @Override
    public UserEntity execute(String email, UUID userId) {
        UserEntity user = new UserEntity();

        if (userId != null) {
            user.setId(userId);
        }

        if (email != null && !email.isBlank()) {
            user.setEmail(email);
            user.setEmailVerified(false);
        }

        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
}
