package com.API.BlogV2.Utils;

import com.API.BlogV2.Service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Marks this class as a source of bean definitions for the Spring context
@EnableWebSecurity // Enables Spring Security's web security support and provides the Spring MVC integration
public class SecurityConfig {

    @Autowired // Injects your custom service that loads user data from the database
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean // Tells Spring to manage the return value of this method as a Bean (the main security config)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                // Disables CSRF protection (Common for REST APIs since they are usually stateless)
                .csrf(customizer -> customizer.disable())

                // Ensures that EVERY request sent to the server must be from an authenticated user
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/v1/users/register","/api/v1/users/login")
                        .permitAll()
                        .anyRequest().authenticated())

                // Enables the standard browser-based login form provided by Spring Security
                .formLogin(Customizer.withDefaults())

                // Enables HTTP Basic authentication (the browser popup/header-based auth for APIs)
                .httpBasic(Customizer.withDefaults())

                // Configures session policy; NEVER means Spring won't create a session, but will use one if it exists
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.NEVER))
                .addFilterAt(jwtFilter,UsernamePasswordAuthenticationFilter.class)
                // Finalizes the configuration and returns the built SecurityFilterChain object
                .build();
    }

    @Bean // Defines how Spring should verify user credentials
    public AuthenticationProvider authenticationProvider() {
        // DaoAuthenticationProvider is the standard provider for username/password checks
        // We pass our service via the constructor (required in newer versions for immutability)
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(myUserDetailsService);

        // Tells the provider NOT to hash passwords (comparing plain text).
        // Note: Change to BCryptPasswordEncoder() for real production apps!
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));

        // Returns the configured provider to the Spring Security AuthenticationManager
        return provider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config){
        return config.getAuthenticationManager();
    }
}