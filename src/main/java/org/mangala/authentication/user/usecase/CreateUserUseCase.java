package org.mangala.authentication.user.usecase;

import org.mangala.authentication.user.domain.UserEntity;

import java.util.UUID;

public interface CreateUserUseCase {
    /**
     * Create a new user
     * @param email Optional email address. If null, creates an anonymous user.
     * @param userId Optional user ID. If provided, will be used for the new user. If null, a new UUID will be generated.
     * @return Created user entity
     */
    UserEntity execute(String email, UUID userId);
}
