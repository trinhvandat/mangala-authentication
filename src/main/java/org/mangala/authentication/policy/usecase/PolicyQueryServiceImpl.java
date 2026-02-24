package org.mangala.authentication.policy.usecase;

import lombok.RequiredArgsConstructor;
import org.mangala.security.model.ApiPermissionDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyQueryServiceImpl implements PolicyQueryService {

    private static final String SELECT_ACTIVE_POLICIES = """
            SELECT
                ap.id::text AS id,
                ap.http_method,
                ap.path_pattern,
                p.code AS permission_code,
                ap.condition_expr,
                ap.service_name,
                ap.priority,
                ap.is_active
            FROM api_permissions ap
            INNER JOIN permissions p ON p.id = ap.permission_id
            WHERE ap.is_active = true
              AND p.is_active = true
            ORDER BY ap.priority DESC, ap.path_pattern ASC, ap.http_method ASC
            """;

    private static final String SELECT_POLICY_VERSION = """
            SELECT version
            FROM policy_version
            WHERE id = 1
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<ApiPermissionDTO> getActivePolicies() {
        return jdbcTemplate.query(SELECT_ACTIVE_POLICIES, (rs, rowNum) -> ApiPermissionDTO.builder()
                .id(rs.getString("id"))
                .httpMethod(rs.getString("http_method"))
                .pathPattern(rs.getString("path_pattern"))
                .permissionCode(rs.getString("permission_code"))
                .conditionExpr(rs.getString("condition_expr"))
                .serviceName(rs.getString("service_name"))
                .priority(rs.getInt("priority"))
                .active(rs.getBoolean("is_active"))
                .build());
    }

    @Override
    public long getPolicyVersion() {
        Long version = jdbcTemplate.queryForObject(SELECT_POLICY_VERSION, Long.class);
        return version != null ? version : 1L;
    }
}
