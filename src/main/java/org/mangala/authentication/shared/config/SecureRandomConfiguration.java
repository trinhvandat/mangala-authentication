package org.mangala.authentication.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class SecureRandomConfiguration {
    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }
}
