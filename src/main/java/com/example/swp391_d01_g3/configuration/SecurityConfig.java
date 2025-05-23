//package com.example.swp391_d01_g3.configuration;
//
//import com.example.casestudy_g2_m4.common.CustomAuthenticationEntryPoint;
//import com.example.casestudy_g2_m4.common.CustomAuthenticationSuccessHandler;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Autowired
//    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
//
//    @Autowired
//    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Bean
//    public static PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/*.css","/","/login", "/css/**", "/js/**", "/favicon.ico","/register").permitAll() // Các đường dẫn không cần login
//                        .requestMatchers("/dashboard/**","/rooms","/finance","/hotel-inf","/list_booking","/show-form-add").hasRole("ADMIN") // Yêu cầu role ADMIN
//                        .requestMatchers("/user/**").hasRole("CUSTOMER") // Yêu cầu Role CUSTOMER
//                        .requestMatchers("/staff/**").hasRole("STAFF") // Yêu cầu Role STAFF
//                        .requestMatchers("/add_booking").hasAnyRole("ADMIN", "CUSTOMER") // Cho phép cả ADMIN và CUSTOMER
//                        .anyRequest().authenticated()
//                )
//                .csrf(AbstractHttpConfigurer::disable)
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .loginProcessingUrl("/login")
//                        .usernameParameter("email")
//                        .passwordParameter("password")
//                        .successHandler(customAuthenticationSuccessHandler)
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                        .logoutSuccessUrl("/")
//                        .deleteCookies("JSESSIONID")
//                        .invalidateHttpSession(true)
//                        .clearAuthentication(true)
//                        .permitAll()
//                )
//                .sessionManagement(session -> session
//                        .invalidSessionUrl("/")
//                        .maximumSessions(1)
//                        .expiredUrl("/")
//                )
//                .exceptionHandling(ex -> ex
//                        .authenticationEntryPoint(customAuthenticationEntryPoint)
//                )
//                .requestCache(RequestCacheConfigurer::disable
//                );
//
//        http.authenticationProvider(authenticationProvider());
//
//        return http.build();
//    }
//}