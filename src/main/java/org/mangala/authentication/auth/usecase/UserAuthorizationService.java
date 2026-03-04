package org.mangala.authentication.auth.usecase;

import lombok.RequiredArgsConstructor;
import org.mangala.authentication.auth.adapter.repository.RoleRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAuthorizationService {

    private final RoleRepository roleRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<String> findRolesByUserId(UUID userId) {
        return roleRepository.findRoleCodesByUserId(userId);
    }

    public List<String> findPermissionsByUserId(UUID userId) {
        String jpql = """
                SELECT DISTINCT p.code
                FROM UserRoleEntity ur
                JOIN ur.role r
                JOIN RolePermissionEntity rp ON rp.role = r
                JOIN rp.permission p
                WHERE ur.user.id = :userId
                  AND r.isActive = true
                  AND p.isActive = true
                """;
        return entityManager.createQuery(jpql, String.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
