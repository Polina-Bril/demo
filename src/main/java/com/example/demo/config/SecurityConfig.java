package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Bean
  @Override
  public UserDetailsService userDetailsService() {

    //User Role
    UserDetails user = User.withUsername("sergey")
        .passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder()::encode)
        .password("12345678").roles("USER").build();

    //Manager Role
    UserDetails manager = User.withUsername("poli")
        .passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder()::encode)
        .password("87654321").roles("TELEMETRY_REPORTER").build();


    InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();

    userDetailsManager.createUser(user);
    userDetailsManager.createUser(manager);

    return userDetailsManager;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeRequests()
//        .antMatchers("/reporter/get").hasRole("TELEMETRY_REPORTER")
        .antMatchers("/save", "/get-statistics", "/get-statistics-saved").hasRole("USER")
//        .anyRequest().authenticated()
        .and()
        .httpBasic()
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }
}
