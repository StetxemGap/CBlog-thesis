package com.korenko.CBlog.config;

import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.Map;

@Configuration
public class SecurityConfing {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/upload")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "style/**.css", "img/**.jpg", "img/**.png", "/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/activation").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/upload").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(authenticationSuccessHandler())
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .addLogoutHandler((request, response, authentication) -> {
                            if (authentication != null) {
                                String username = authentication.getName();
                                messagingTemplate.convertAndSend(
                                        "/topic/onlineStatus",
                                        Map.of("username", username, "isOnline", false)
                                );
                            }
                        })
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Users user = userRepo.findByUsername(userDetails.getUsername());
            if (user.getActivation()) {
                response.sendRedirect("/profile");
            } else {
                response.sendRedirect("/activation");
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

}
