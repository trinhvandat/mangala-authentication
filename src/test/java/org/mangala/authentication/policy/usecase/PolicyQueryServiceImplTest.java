package org.mangala.authentication.policy.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mangala.security.model.ApiPermissionDTO;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyQueryServiceImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private PolicyQueryServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PolicyQueryServiceImpl(jdbcTemplate);
    }

    @Test
    void shouldReturnActivePoliciesFromDatabase() {
        ApiPermissionDTO row = ApiPermissionDTO.builder()
                .id("policy-1")
                .httpMethod("GET")
                .pathPattern("/api/v1/wallets")
                .permissionCode("wallets:read")
                .conditionExpr(null)
                .serviceName("wallet-service")
                .priority(10)
                .active(true)
                .build();

        when(jdbcTemplate.query(anyString(), any(org.springframework.jdbc.core.RowMapper.class)))
                .thenReturn(List.of(row));

        List<ApiPermissionDTO> result = service.getActivePolicies();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getPermissionCode()).isEqualTo("wallets:read");
        assertThat(result.getFirst().isActive()).isTrue();
    }

    @Test
    void shouldReturnVersionFromDatabase() {
        when(jdbcTemplate.queryForObject(anyString(), org.mockito.ArgumentMatchers.eq(Long.class)))
                .thenReturn(42L);

        long version = service.getPolicyVersion();

        assertThat(version).isEqualTo(42L);
    }

    @Test
    void shouldFallbackToVersionOneWhenDbReturnsNull() {
        when(jdbcTemplate.queryForObject(anyString(), org.mockito.ArgumentMatchers.eq(Long.class)))
                .thenReturn(null);

        long version = service.getPolicyVersion();

        assertThat(version).isEqualTo(1L);
    }
}
