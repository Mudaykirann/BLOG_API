package com.API.BlogV2.Utils;

import com.API.BlogV2.Service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration // Marks this class as a source of bean definitions for the Spring context
@EnableWebSecurity // Enables Spring Security's web security support and provides the Spring MVC integration
@EnableMethodSecurity // <--- CRITICAL: Enables @PreAuthorize
public class SecurityConfig {

    @Autowired // Injects your custom service that loads user data from the database
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean // Tells Spring to manage the return value of this method as a Bean (the main security config)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http


                .cors(Customizer.withDefaults())
                // Disables CSRF protection (Common for REST APIs since they are usually stateless)
                .csrf(customizer -> customizer.disable())


                // Ensures that EVERY request sent to the server must be from an authenticated user
                .authorizeHttpRequests(request -> request
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/v1/api-docs/**",
                                "/api/v1/posts"
                        )
                        .permitAll()

                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/*/comments").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/*/posts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/search/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/category/**").permitAll()
                        .anyRequest().authenticated())

                // Enables the standard browser-based login form provided by Spring Security
                //.formLogin(Customizer.withDefaults())

                // Enables HTTP Basic authentication (the browser popup/header-based auth for APIs)
                //.httpBasic(Customizer.withDefaults())

                // Configures session policy; NEVER means Spring won't create a session, but will use one if it exists
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class)
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Match your Vite port
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));

        // Define allowed methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Allow all headers (including Authorization for your JWT)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

        // Allow credentials for OAuth2/Cookies
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}