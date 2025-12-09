package com.clashofserres.cinematch.config;

import com.clashofserres.cinematch.frontend.view.LoginView;
import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// https://vaadin.com/docs/latest/flow/security/enabling-security
@EnableWebSecurity
@Configuration
@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SecurityConfig
{

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configure your static resources with public access
       http.authorizeHttpRequests(auth -> auth
               .requestMatchers("/public/**", "/images/**", "/line-awesome/**")
               .permitAll()
       );

        // Configure Vaadin's security using VaadinSecurityConfigurer
        http.with(VaadinSecurityConfigurer.vaadin(), configurer -> {
            // This is important to register your login view to the
            // navigation access control mechanism:
            configurer.loginView(LoginView.class);

            // You can add any possible extra configurations of your own
            // here (the following is just an example):
            //configurer.enableCsrfConfiguration(false);
        });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}