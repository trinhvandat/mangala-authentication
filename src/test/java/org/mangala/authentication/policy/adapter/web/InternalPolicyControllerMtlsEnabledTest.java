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

import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.x509;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InternalPolicyController.class)
@Import(InternalApiSecurityConfig.class)
@TestPropertySource(properties = {
        "application.security.internal-api.mtls-enabled=true",
        "application.security.internal-api.allowed-principals=gateway-service"
})
class InternalPolicyControllerMtlsEnabledTest {

    @Autowired
    private MockMvc mockMvc;

    @org.springframework.boot.test.mock.mockito.MockBean
    private PolicyQueryService policyQueryService;

    @Test
    void shouldRejectRequestWithoutClientCertificateWhenMtlsEnabled() throws Exception {
        mockMvc.perform(get("/v1/internal/policies"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowRequestWithTrustedClientCertificateWhenMtlsEnabled() throws Exception {
        ApiPermissionDTO policy = ApiPermissionDTO.builder()
                .id("rule-1")
                .httpMethod("GET")
                .pathPattern("/api/v1/wallets")
                .permissionCode("wallets:read")
                .priority(10)
                .active(true)
                .build();
        when(policyQueryService.getActivePolicies()).thenReturn(List.of(policy));

        X509Certificate certificate = mock(X509Certificate.class);
        X500Principal principal = new X500Principal("CN=gateway-service,OU=Platform");
        when(certificate.getSubjectX500Principal()).thenReturn(principal);
        when(certificate.getSubjectDN()).thenReturn(principal);

        mockMvc.perform(get("/v1/internal/policies").with(x509(certificate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].permissionCode").value("wallets:read"));
    }

    @Test
    void shouldReturnPolicyVersionForTrustedClientCertificate() throws Exception {
        when(policyQueryService.getPolicyVersion()).thenReturn(99L);

        X509Certificate certificate = mock(X509Certificate.class);
        X500Principal principal = new X500Principal("CN=gateway-service,OU=Platform");
        when(certificate.getSubjectX500Principal()).thenReturn(principal);
        when(certificate.getSubjectDN()).thenReturn(principal);

        mockMvc.perform(get("/v1/internal/policies/version").with(x509(certificate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(99));
    }
}
