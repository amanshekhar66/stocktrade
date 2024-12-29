package stocktrade.stocktrade.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import stocktrade.stocktrade.filters.JwtAuthFilter;
import stocktrade.stocktrade.handlers.OAuth2SuccessHandler;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(
                auth ->{
                        auth.requestMatchers("/auth/**","/home","/swagger-ui/index.html").permitAll()
                        .anyRequest().authenticated();
                        })
                .csrf(csrf->csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oAuth2Config->oAuth2Config.failureUrl("/login?error=true")
                        .successHandler(oAuth2SuccessHandler))
                .exceptionHandling(exception -> exception.accessDeniedHandler(handleAccessDeniedException()));
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager createAuthenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    public AccessDeniedHandler handleAccessDeniedException() {
        return((request, response, accessDeniedException) -> {
            throw new AccessDeniedException("You are not authorized");
        });
    }
}
