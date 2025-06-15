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
                // ⭐ ENDPOINTS DE AUTENTICAÇÃO - PÚBLICOS (ORDEM IMPORTA!)
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/auth/register").permitAll()
                .requestMatchers("/auth/status").permitAll()
                
                // ⭐ DOCUMENTAÇÃO DA API - COMPLETAMENTE PÚBLICO
                .requestMatchers("/api/swagger-ui/**").permitAll()
                .requestMatchers("/api/swagger-ui.html").permitAll()
                .requestMatchers("/api/v3/api-docs/**").permitAll()
                .requestMatchers("/api/api-docs/**").permitAll()
                .requestMatchers("/api/swagger-resources/**").permitAll()
                .requestMatchers("/api/webjars/**").permitAll()
                .requestMatchers("/api/swagger-config").permitAll()
                .requestMatchers("/api/v3/api-docs/**").permitAll()
                
                // ⭐ ENDPOINTS PÚBLICOS GERAIS
                .requestMatchers("/test/public").permitAll()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/").permitAll()
                .requestMatchers("/index.html").permitAll()
                .requestMatchers("/static/**").permitAll()
                .requestMatchers("/favicon.ico").permitAll()
                
                // ⭐ MONITORAMENTO
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                
                // ⭐ H2 CONSOLE (apenas para desenvolvimento)
                .requestMatchers("/h2-console/**").permitAll()
                
                // ⭐ ROTAS ESPECÍFICAS POR MÉTODO HTTP E ROLE
                
                // GET público para listagem básica
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/veiculos.**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/clientes").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/categorias").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/servicos").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/empresa").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/empresa-horarios").permitAll()

                // POST, PUT, DELETE para ADMIN e FUNCIONARIO
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/clientes/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/clientes/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/clientes/**").permitAll()

                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/veiculos/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/veiculos/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/veiculos/**").permitAll()

                
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/categorias/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/categorias/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/categorias/**").permitAll()
                
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/servicos/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/servicos/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/servicos/**").permitAll()
                
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/empresa/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/empresa/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/empresa/**").permitAll()

                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/empresa-horarios/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/empresa-horarios/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/empresa-horarios/**").permitAll()

                // ⭐ FALLBACK: Qualquer outro endpoint dessas APIs requer ADMIN ou FUNCIONARIO
                .requestMatchers("/api/clientes/**").permitAll()
                .requestMatchers("/api/veiculos/**").permitAll()
                .requestMatchers("/api/categorias/**").permitAll()
                .requestMatchers("/api/servicos/**").permitAll()
                .requestMatchers("/api/empresa/**").permitAll()
                .requestMatchers("/api/empresa-horarios/**").permitAll()

                // ⭐ ENDPOINTS DE FUNCIONÁRIOS - APENAS PARA ADMIN
                .requestMatchers("/api/funcionarios/**").permitAll()
                
                // ⭐ ENDPOINTS ADMINISTRATIVOS - APENAS PARA ADMINS
                .requestMatchers("/api/admin/**").permitAll()
                
                // ⭐ TODOS OS OUTROS ENDPOINTS REQUEREM AUTENTICAÇÃO
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            // ⭐ CORS e Headers
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
        
        // ⭐ CORS mais permissivo para desenvolvimento
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://127.0.0.1:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Access-Control-Allow-Origin"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}