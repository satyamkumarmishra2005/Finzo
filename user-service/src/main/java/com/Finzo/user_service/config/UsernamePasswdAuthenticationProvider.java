package com.Finzo.user_service.config;

import org.hibernate.mapping.UserDefinedType;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class UsernamePasswdAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    public UsernamePasswdAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
