package org.mangala.authentication.user.usecase;

import lombok.RequiredArgsConstructor;
import org.mangala.authentication.auth.adapter.repository.RoleRepository;
import org.mangala.authentication.auth.adapter.repository.UserRoleRepository;
import org.mangala.authentication.auth.domain.UserRoleEntity;
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
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserEntity execute(String email, UUID userId) {
        System.out.println("Email from usecase: " + email);
        UserEntity user = new UserEntity();

        if (userId != null) {
            user.setId(userId);
        }

        if (email != null && !email.isBlank()) {
            user.setEmail(email);
            user.setEmailVerified(false);
        }

        user.setCreatedAt(LocalDateTime.now());

        UserEntity persisted = userRepository.save(user);

        roleRepository.findByCodeAndIsActiveTrue("ROLE_USER")
                .ifPresent(role -> {
                    UserRoleEntity userRole = UserRoleEntity.create(persisted, role, "system");
                    userRoleRepository.save(userRole);
                });

        return persisted;
    }
}
