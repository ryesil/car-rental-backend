package com.prorental.carrental.security.config;


//Spring security is managed by this class.
//EndPoints will be saved by rules that are made here
//Filters, guards, password Encoder
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.prorental.carrental.security.AuthEntryPointJwt;
import com.prorental.carrental.security.AuthTokenFilter;
import com.prorental.carrental.security.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true) //Method basinda prePost?
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //We didn't write autowired b/c there is AllArgsConstructor

private UserDetailsServiceImpl userDetailsServiceImpl;

private final AuthEntryPointJwt unAuthorizedHandler;

@Bean //instructing Spring to create and manage an instance of BCryptPasswordEncoder as a bean in the application context, allowing it to be easily accessed by other components.
public PasswordEncoder passwordEncoder(){
    return  new BCryptPasswordEncoder();
}


@Bean
public AuthTokenFilter authenticationJwtTokenFilter(){
    return new AuthTokenFilter();
}


//handles login. we put a bean so that spring uses it.
@Bean
 public AuthenticationManager authenticationManager() throws Exception{
    return super.authenticationManager();
}

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());
    }

//    cors: cross-origin-resource-sharing
//    This is for endPoints
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().disable().exceptionHandling()
                .authenticationEntryPoint(unAuthorizedHandler)
                .and().authorizeHttpRequests()
                .antMatchers("/register", "/login")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }


    //this is for resources like html, css ...
    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

}
