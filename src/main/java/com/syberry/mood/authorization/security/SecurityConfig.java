package com.syberry.mood.authorization.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.syberry.mood.authorization.service.impl.UserDetailsServiceImpl;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Configuration bean for setting up application security.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
  private final UserDetailsServiceImpl userDetailsService;
  private final AuthEntryPointJwt authEntryPointJwt;
  private final JwtTokenFilter jwtTokenFilter;

  @Value("${app.cache-expiration}")
  private long cacheExpiration;

  /**
   * Defines authentication provider bean.
   *
   * @return customized authentication provider
   */
  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setUserDetailsService(userDetailsService);
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    return daoAuthenticationProvider;
  }

  /** Defines authentication manager bean.
   *
   * @param authConfig authentication config
   * @return authentication manager
   * @throws Exception exception
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    return authConfig.getAuthenticationManager();
  }

  /**
   * Defines encoder for passwords.
   *
   * @return password encoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Defines security filter chain  for users requests.
   *
   * @param http http security object
   * @return customized security filter chain
   * @throws Exception exception
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors().and()
        .csrf().disable()
        .exceptionHandling().authenticationEntryPoint(authEntryPointJwt).and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .authorizeHttpRequests()
        .antMatchers("/auth/login").permitAll()
        .antMatchers("/auth/refresh").permitAll()
        .antMatchers("/auth/reset").permitAll()
        .antMatchers("/auth/restore").permitAll()
        .anyRequest().authenticated();

    http.headers().frameOptions().sameOrigin();
    http.authenticationProvider(daoAuthenticationProvider());
    http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  /**
   * Defines cors filter with customized rules.
   *
   * @return customized cors filter
   */
  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowCredentials(true);
    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.addAllowedOriginPattern("*");
    configuration.addAllowedHeader("*");
    configuration.addAllowedMethod("GET");
    configuration.addAllowedMethod("POST");
    configuration.addAllowedMethod("PUT");
    configuration.addAllowedMethod("DELETE");
    configuration.addAllowedMethod("PATCH");
    configuration.addAllowedMethod("OPTIONS");
    configuration.addAllowedMethod("HEAD");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return new CorsFilter(source);
  }

  /**
   * Defines LoadingCache bean.
   *
   * @return LoadingCache bean
   */
  @Bean
  public LoadingCache<String, String> passwordResetTokenCache() {
    return CacheBuilder.newBuilder()
        .expireAfterWrite(cacheExpiration, TimeUnit.MINUTES)
        .build(new CacheLoader<>() {
          @Override
          public String load(String key) {
            return "";
          }
        });
  }
}
