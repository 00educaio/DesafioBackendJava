package caio_dev.Desafio_Livraria.config;

import caio_dev.Desafio_Livraria.service.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;

@Configuration //Anotação que define a classe como de configuração
public class SecurityConfig {
    private final MyUserDetailsService userDetailsService;

    public SecurityConfig(MyUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // Permite configurar as requisições http
        http
            .csrf(csrf -> csrf.disable()) // Desativa o csrf pra simplificar a config da API, já que não há sessṍes com cookies
            .authorizeHttpRequests(auth -> auth 
                .requestMatchers("/api/books").permitAll() // Endpoint público
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/teste-conexao").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Sem estado, ideal pra API REST onde a resquest passa todo o dado nescessário(ausencia de cookies)
            )
            .httpBasic(Customizer.withDefaults()); // Configura a autentificação para HttpBasic(user e senha passados nos HEADERS)
        return http.build(); //Finaliza a config retorna o objeto(SecurityFilterChain) que o SpringSecurity usa pra aplicar a regras
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService)
               .passwordEncoder(passwordEncoder());
        return builder.build();
    }
    
}