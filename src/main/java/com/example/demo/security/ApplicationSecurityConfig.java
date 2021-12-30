package com.example.demo.security;

import com.example.demo.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.demo.security.ApplicationRole.*;
import static com.example.demo.security.ApplicationUserPermission.*;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .passwordEncoder(passwordEncoder())
                .withUser("balde").password(passwordEncoder().encode("balde"))
                .authorities(STUDENT.getGrantedAuthorities())
                .and()
                .withUser("tom").password(passwordEncoder().encode("tom"))
                .authorities(ADMIN_TRAINEE.getGrantedAuthorities())
                .and()
                .withUser("admin").password(passwordEncoder().encode("admin"))
                .authorities(ADMIN.getGrantedAuthorities());
    }



    protected void configure(HttpSecurity http) throws Exception {
        http
                /*.csrf().csrfTokenRepository(repo())
                .and()*/
                .csrf().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager()))
                .authorizeRequests()
                .mvcMatchers("/api/**").hasAnyRole(STUDENT.name())
                .anyRequest()
                .authenticated();
    }

    protected void configure11(HttpSecurity http) throws Exception {
        http
                /*.csrf().csrfTokenRepository(repo())
                .and()*/
                .authorizeRequests()
                .mvcMatchers("/api/**").hasAnyRole(STUDENT.name())
                .anyRequest().authenticated()
                .and()

                .formLogin()
                .loginPage ("/login").permitAll()
                .defaultSuccessUrl("/courses", true)
                .and()
                .rememberMe()
                    .tokenValiditySeconds(12000)
                    .key("something verisecure")
                .and()
                .logout()
                .logoutUrl("/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("A", "B")
                .logoutSuccessUrl("/login");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/resources/**","/images/**","/styles/**", "/", "/index");
    }

    @Bean
    public CsrfTokenRepository repo() {
        HttpSessionCsrfTokenRepository repo = new HttpSessionCsrfTokenRepository();
        repo.setParameterName("_csrf");
        repo.setHeaderName("X_CSRF_TOKEN");
        return repo;
    }
}
