package org.mangala.authentication.auth.adapter.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserAuthorizationQueryRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<String> findRolesByUserId(UUID userId) {
        String sql = """
                SELECT r.code
                FROM user_roles ur
                JOIN roles r ON r.id = ur.role_id
                WHERE ur.user_id = :userId
                  AND r.is_active = true
                """;
        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource("userId", userId), String.class);
    }

    public List<String> findPermissionsByUserId(UUID userId) {
        String sql = """
                SELECT DISTINCT p.code
                FROM user_roles ur
                JOIN roles r ON r.id = ur.role_id
                JOIN role_permissions rp ON rp.role_id = r.id
                JOIN permissions p ON p.id = rp.permission_id
                WHERE ur.user_id = :userId
                  AND r.is_active = true
                  AND p.is_active = true
                """;
        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource("userId", userId), String.class);
    }

    public void assignDefaultRole(UUID userId) {
        String sql = """
                INSERT INTO user_roles (user_id, role_id, created_by)
                SELECT :userId, r.id, 'system'
                FROM roles r
                WHERE r.code = 'ROLE_USER'
                ON CONFLICT (user_id, role_id) DO NOTHING
                """;
        jdbcTemplate.update(sql, new MapSqlParameterSource("userId", userId));
    }
}
