package wooyoungsoo.authserver.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import wooyoungsoo.authserver.global.filter.JwtAccessDeniedHandler;
import wooyoungsoo.authserver.global.filter.JwtAuthenticationEntryPoint;
import wooyoungsoo.authserver.global.filter.JwtFilter;
import wooyoungsoo.authserver.global.common.JwtProvider;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests((authorizeRequests) -> {
                    authorizeRequests.requestMatchers("/api/auth/**").permitAll();
                    authorizeRequests.anyRequest().hasAnyRole("USER", "ADMIN", "DOCTOR");
                }).addFilterBefore(new JwtFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class
                ).exceptionHandling((exceptionHandling) -> {
                    exceptionHandling.authenticationEntryPoint(new JwtAuthenticationEntryPoint());
                    exceptionHandling.accessDeniedHandler(new JwtAccessDeniedHandler());
                });

        return http.build();
    }
}