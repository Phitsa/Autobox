package com.boxpro.config;

import com.boxpro.security.JwtAuthenticationFilter;
import com.boxpro.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // ⭐ ORDEM CORRIGIDA: MAIS ESPECÍFICO PRIMEIRO
                
                // 1. ENDPOINTS DE AUTENTICAÇÃO - PÚBLICOS
                .requestMatchers("/auth/**").permitAll()
                
                // 2. DOCUMENTAÇÃO DA API - PÚBLICO
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", 
                               "/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                
                // 3. RECURSOS ESTÁTICOS E BÁSICOS
                .requestMatchers("/", "/index.html", "/static/**", "/favicon.ico").permitAll()
                .requestMatchers("/public/**").permitAll()
                
                // 4. MONITORAMENTO E DEBUG
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/test/**").permitAll()
                
                // 🎯 5. EMPRESA CONTATOS - ENDPOINTS ESPECÍFICOS PRIMEIRO
                .requestMatchers("/api/empresa-contatos/status").permitAll()
                .requestMatchers("/api/empresa-contatos/tipos").permitAll()
                .requestMatchers("/api/empresa-contatos/empresa/**").permitAll()
                .requestMatchers("/api/empresa-contatos/**").permitAll()
                
                // 🎯 6. EMPRESA - ENDPOINTS ESPECÍFICOS
                .requestMatchers("/api/empresa/status").permitAll()
                .requestMatchers("/api/empresa/**").permitAll()
                
                // 🎯 7. OUTROS ENDPOINTS DA API - PÚBLICOS DURANTE DESENVOLVIMENTO
                .requestMatchers("/api/empresa-horarios/**").permitAll()
                .requestMatchers("/api/clientes/**").permitAll()
                .requestMatchers("/api/categorias/**").permitAll()
                .requestMatchers("/api/servicos/**").permitAll()
                .requestMatchers("/api/funcionarios/**").permitAll()
                .requestMatchers("/api/admin/**").permitAll()
                
                // 8. FALLBACK: Qualquer outra rota da API
                .requestMatchers("/api/**").permitAll()
                
                // 9. ÚLTIMO: Todos os outros endpoints requerem autenticação
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> headers
                .frameOptions().sameOrigin()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig.disable())
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 🔧 CORS OTIMIZADO PARA DESENVOLVIMENTO
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000", 
            "http://127.0.0.1:3000",
            "http://localhost:8080",
            "http://127.0.0.1:8080"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", 
            "Cache-Control", 
            "Content-Type", 
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Headers",
            "Access-Control-Allow-Methods"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}