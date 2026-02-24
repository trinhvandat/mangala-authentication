package org.mangala.authentication.shared.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "application.security.internal-api")
public class InternalApiSecurityProperties {

    /**
     * Enable mTLS/x509 authentication for /v1/internal/** endpoints.
     */
    private boolean mtlsEnabled = false;

    /**
     * Extract service identity from certificate subject DN.
     */
    private String subjectPrincipalRegex = "CN=(.*?)(?:,|$)";

    /**
     * Allowed internal caller identities (usually certificate CN).
     */
    private List<String> allowedPrincipals = List.of("gateway-service");
}
