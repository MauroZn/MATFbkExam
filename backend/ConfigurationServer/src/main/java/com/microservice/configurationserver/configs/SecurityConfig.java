package com.microservice.configurationserver.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Indica che questa classe è una configurazione di Spring
public class SecurityConfig {

    @Bean // Definisce un bean per la configurazione della catena dei filtri di sicurezza
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Configurazione della protezione CSRF (Cross-Site Request Forgery)
        http.csrf(csrf -> csrf.ignoringRequestMatchers(
                        // Esclude gli endpoint "/encrypt/**" e "/decrypt/**" dalla protezione CSRF
                        "/encrypt/**", "/decrypt/**"
                ))
                // Configura le autorizzazioni delle richieste
                .authorizeRequests(authz -> authz.anyRequest().authenticated()) // Richiede l'autenticazione per tutte le richieste
                // Abilita il form di login con configurazione di default
                .formLogin(Customizer.withDefaults())
                // Abilita l'autenticazione HTTP Basic con configurazione di default
                .httpBasic(Customizer.withDefaults());

        // Restituisce la catena di filtri configurata
        return http.build();
    }
}
