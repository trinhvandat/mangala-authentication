package org.mangala.authentication.shared.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class WebAuthnConfigValidationProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof WebAuthnConfigProperties config) {
            if (config.getRp() == null) {
                throw new IllegalStateException("RP configuration is required");
            }
            if (config.getRp().getId() == null || config.getRp().getId().isBlank()) {
                throw new IllegalStateException("RP ID is required");
            }
            if (config.getTimeout() == null || config.getTimeout() <= 0) {
                throw new IllegalStateException("Timeout must be positive");
            }
        }

        return bean;
    }
}
