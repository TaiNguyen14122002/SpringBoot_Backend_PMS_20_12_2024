package com.TaiNguyen.ProjectManagementSystems.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class AppConfig {

    //cau hinh chuoi bo loc bao mat
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(Management -> Management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(Authorize -> Authorize.requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable())
                .cors(cors->cors.configurationSource(corsConfigrationSource()));


        return http.build();
    }

    private CorsConfigurationSource corsConfigrationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration cfg = new CorsConfiguration();
                 cfg.setAllowedOrigins(Arrays.asList(

                         "http://localhost:5173",
                         "https://react-js-project-management-systems.vercel.app",
                         "https://react-js-frontend-pms-20-12-2024.vercel.app",
                         "https://firebasestorage.googleapis.com/v0/b/pms-fe88f.appspot.com/o/files%2FBlack%20and%20White%20Auto%20Repair%20Logo%20(1).png?alt=media&token=5cbbc329-d36f-46e1-8526-53d6e2f11699",
                         "https://2ca174cfbbddfaeca00ed30b5b480967.serveo.net"

                 ));
//                cfg.setAllowedOrigins(Collections.singletonList("*"));

                cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                cfg.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
                cfg.setExposedHeaders(Arrays.asList("Authorization"));
                cfg.setAllowCredentials(true);
                cfg.setMaxAge(3600L);
                return cfg;
            }
        };
    }

    @Bean
    PasswordEncoder PasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
