package com.korenko.CBlog.config;

import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.repo.UserRepo;
import com.korenko.CBlog.service.UsersOnline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfing {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UsersOnline usersOnline;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/upload")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/**.css", "/**.jpg", "/**.png", "/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/activation").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/upload").authenticated()
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
                                usersOnline.removeUser(authentication.getName());
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
            if (user.isActivation()) {
                response.sendRedirect("/profile");
                usersOnline.addUser(authentication.getName());
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
