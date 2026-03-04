package org.mangala.authentication.auth.adapter.repository;

import org.mangala.authentication.auth.domain.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findByCodeAndIsActiveTrue(String code);

    @Query("""
            SELECT r.code FROM RoleEntity r
            JOIN UserRoleEntity ur ON ur.role = r
            WHERE ur.user.id = :userId AND r.isActive = true
            """)
    List<String> findRoleCodesByUserId(@Param("userId") UUID userId);
}
