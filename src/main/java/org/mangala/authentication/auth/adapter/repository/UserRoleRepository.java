package org.mangala.authentication.auth.adapter.repository;

import org.mangala.authentication.auth.domain.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UUID> {

    boolean existsByUserIdAndRoleId(UUID userId, UUID roleId);

    @Query("""
            SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END
            FROM UserRoleEntity ur
            WHERE ur.user.id = :userId AND ur.role.code = :roleCode
            """)
    boolean existsByUserIdAndRoleCode(@Param("userId") UUID userId, @Param("roleCode") String roleCode);
}
