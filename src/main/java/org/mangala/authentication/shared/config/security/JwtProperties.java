package org.mangala.authentication.shared.config.security;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "application.authentication.jwt")
public class JwtProperties {

    @NotBlank
    private String secret;

    @NotBlank
    private String issuer;

    @NotBlank
    private String accessTokenAudience;

    @NotBlank
    private String refreshTokenAudience;

    @Min(60)
    private long accessTokenExpirationSeconds;

    @Min(300)
    private long refreshTokenExpirationSeconds;
}
