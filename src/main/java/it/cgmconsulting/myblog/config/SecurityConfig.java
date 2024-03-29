package it.cgmconsulting.myblog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import it.cgmconsulting.myblog.security.CustomUserDetailsService;
import it.cgmconsulting.myblog.security.JwtAuthenticationEntryPoint;
import it.cgmconsulting.myblog.security.JwtAuthenticationFilter;

@Configuration
// permette di utilizare l'annotation PreAutorized
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired CustomUserDetailsService customUserDetailsService;
    @Autowired private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    	return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    	http
        .cors()
            .and()
        .csrf()
            .disable()
        .exceptionHandling()
            .authenticationEntryPoint(unauthorizedHandler)
            .and()
        .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
        .authorizeRequests()
            .antMatchers("/",
                "/favicon.ico",
                "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg",
                "/**/*.html", "/**/*.css", "/**/*.js")
                .permitAll()
            .antMatchers("/auth/**", "/{pathvariable:[0-9A-Za-z]+}/auth/**", "/public/**",
                "/{pathvariable:[0-9A-Za-z]+}/public/**", "/swagger-ui/**", "/v3/api-docs/**",
                "/actuator/**")
                .permitAll()
            .anyRequest()
                .authenticated();

    	// Add our custom JWT security filter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



}
