package com.israr.jwtpractice.config;

import com.israr.jwtpractice.security.CustomUserDetailsService;
import com.israr.jwtpractice.security.JwtAuthenticationEntryPoint;
import com.israr.jwtpractice.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationFilter authenticationFilter;
    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception{
        http
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/register/**").permitAll()
                                .requestMatchers("/index").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/nextPage")
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                );

        http.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint));
        if(jwtAuthenticationFilter !=null){
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);} else{
            throw new IllegalStateException(("JWTAuthenticationFilter is null"));
        }
        return http.build();

    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

}
