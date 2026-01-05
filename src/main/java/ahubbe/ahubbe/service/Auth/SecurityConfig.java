package ahubbe.ahubbe.service.Auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic(httpBasic -> httpBasic.disable())
                .csrf(csrf -> csrf.disable())
                .cors(
                        cors ->
                                cors.configurationSource(
                                        request -> {
                                            var corsConfiguration =
                                                    new org.springframework.web.cors
                                                            .CorsConfiguration();
                                            corsConfiguration.setAllowedOrigins(
                                                    java.util.List.of("http://localhost:5173"));
                                            corsConfiguration.setAllowedMethods(
                                                    java.util.List.of(
                                                            "GET", "POST", "PUT", "DELETE",
                                                            "OPTIONS", "PATCH"));
                                            corsConfiguration.setAllowedHeaders(
                                                    java.util.List.of("*"));
                                            corsConfiguration.setAllowCredentials(true);
                                            return corsConfiguration;
                                        }))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/auth/**")
                                        .permitAll()
                                        .requestMatchers("/admin/**")
                                        .permitAll()
                                        .requestMatchers("/user/**")
                                        .permitAll()
                                        .requestMatchers(
                                                "/v3/api-docs/**", // API 명세 JSON 경로
                                                "/swagger-ui/**", // Swagger UI 리소스
                                                "/swagger-ui.html" // Swagger UI 접속 페이지
                                                )
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
