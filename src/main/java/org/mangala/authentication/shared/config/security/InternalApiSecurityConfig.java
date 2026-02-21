package org.mangala.authentication.shared.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties({InternalApiSecurityProperties.class, JwtProperties.class})
public class InternalApiSecurityConfig {

    private static final String INTERNAL_POLICY_READ_AUTHORITY = "INTERNAL_POLICY_READ";

    private final InternalApiSecurityProperties securityProperties;

    @Bean
    @Order(1)
    public SecurityFilterChain internalApiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/v1/internal/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (securityProperties.isMtlsEnabled()) {
            http.x509(x509 -> x509
                            .subjectPrincipalRegex(securityProperties.getSubjectPrincipalRegex())
                            .userDetailsService(internalClientUserDetailsService()))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(HttpMethod.GET,
                                    "/v1/internal/policies",
                                    "/v1/internal/policies/version")
                            .hasAuthority(INTERNAL_POLICY_READ_AUTHORITY)
                            .anyRequest().denyAll());
        } else {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        }

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .anonymous(Customizer.withDefaults())
                .build();
    }

    @Bean
    public UserDetailsService internalClientUserDetailsService() {
        return principal -> {
            if (securityProperties.getAllowedPrincipals().contains(principal)) {
                UserDetails user = User.withUsername(principal)
                        .password("{noop}n/a")
                        .authorities(INTERNAL_POLICY_READ_AUTHORITY)
                        .build();
                return user;
            }
            throw new UsernameNotFoundException("Internal principal is not allowed: " + principal);
        };
    }
}
