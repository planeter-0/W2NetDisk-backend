package com.west2java.netdisk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.west2java.netdisk.filter.CorsFilter;
import com.west2java.netdisk.filter.JWTAuthorizationFilter;
import com.west2java.netdisk.result.ExceptionMsg;
import com.west2java.netdisk.result.ResponseData;
import com.west2java.netdisk.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Arrays;

@Configuration
@EnableWebSecurity//指定为Spring Security配置类
@EnableGlobalMethodSecurity(prePostEnabled = true) // 启用方法安全设置
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler jwtAuthenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginProcessingUrl("/login").usernameParameter("username").passwordParameter("password").loginPage("/login").successHandler(
                jwtAuthenticationSuccessHandler).failureHandler(jwtAuthenticationFailureHandler)
                .and()
                .authorizeRequests()//定义url保护规则
                .antMatchers("/", "/login", "/register").permitAll()//登陆页面允许所有访问
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/dir/**", "/upload", "/rename", "/delete", "/used", "/page").authenticated()
                .antMatchers("/dir/**", "/upload", "/rename", "/delete", "/used", "/page").hasAnyRole("USER", "ADMIN")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .and()
                .addFilter(new JWTAuthorizationFilter(authenticationManager())); //解析tokenHeader并设置认证
        http.addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class);

        http.logout().permitAll();
        http.cors()
                .and()
                .csrf()
                .disable();

        http.exceptionHandling()
                //没有认证时，在这里处理结果，不要重定向
                .authenticationEntryPoint((req, resp, authException) -> {
                    log.warn("认证失败");
                    resp.setContentType("application/json;charset=utf-8");
                    resp.setStatus(401);
                    PrintWriter out = resp.getWriter();
                    ResponseData res = null;
                    if (authException instanceof InsufficientAuthenticationException) {
                        res = new ResponseData(ExceptionMsg.AUTHENTICATION_FAILED);
                    }
                    out.write(res.toString());
                    out.flush();
                    out.close();
                });
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
//        configuration.setAllowedOrigins(Arrays.asList("localhost:8088","localhost:8080","localhost:80"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "PUT", "DELETE"));
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Depth", "User-Agent", "X-File-Size", "X-Requested-With", "X-Requested-By", "If-Modified-Since", "X-File-Name", "X-File-Type", "Cache-Control", "Origin"));
        configuration.addExposedHeader("Authorization");
        configuration.setMaxAge(Duration.ofHours(1));
        source.registerCorsConfiguration("/**", configuration);//配置允许跨域访问的url
        return source;
    }

    //注入UserService
    @Bean
    UserDetailsService UserService() {
        return new UserService();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(UserService()).passwordEncoder(new BCryptPasswordEncoder() {
        });
    }
}
