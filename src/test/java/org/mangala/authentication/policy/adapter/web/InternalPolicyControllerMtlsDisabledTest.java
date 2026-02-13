package org.mangala.authentication.policy.adapter.web;

import org.junit.jupiter.api.Test;
import org.mangala.authentication.shared.config.security.InternalApiSecurityConfig;
import org.mangala.authentication.policy.usecase.PolicyQueryService;
import org.mangala.security.model.ApiPermissionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InternalPolicyController.class)
@Import(InternalApiSecurityConfig.class)
@TestPropertySource(properties = {
        "application.security.internal-api.mtls-enabled=false"
})
class InternalPolicyControllerMtlsDisabledTest {

    @Autowired
    private MockMvc mockMvc;

    @org.springframework.boot.test.mock.mockito.MockBean
    private PolicyQueryService policyQueryService;

    @Test
    void shouldAllowRequestWithoutClientCertificateWhenMtlsDisabled() throws Exception {
        ApiPermissionDTO policy = ApiPermissionDTO.builder()
                .id("rule-1")
                .httpMethod("GET")
                .pathPattern("/api/v1/wallets")
                .permissionCode("wallets:read")
                .priority(10)
                .active(true)
                .build();
        when(policyQueryService.getActivePolicies()).thenReturn(List.of(policy));

        mockMvc.perform(get("/v1/internal/policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].permissionCode").value("wallets:read"));
    }
}
