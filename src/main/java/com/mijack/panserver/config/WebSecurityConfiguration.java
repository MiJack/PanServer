/*
 * Copyright 2019 Mi&Jack
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mijack.panserver.config;

import com.mijack.panserver.component.security.Base64PasswordEncoder;
import com.mijack.panserver.service.UserService;
import com.mijack.panserver.web.security.WebLoginAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mi&Jack
 */
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final String REMEMBER_ME_PARAMETER = "remember-me";

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    UserService userService;
    @Autowired
    Base64PasswordEncoder base64PasswordEncoder;

    @Autowired
    private WebLoginAuthenticationSuccessHandler webLoginAuthenticationSuccessHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(base64PasswordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //api接口不做csrf处理
        http.csrf().ignoringAntMatchers("/api/**");

        http.csrf().disable();
        // 静态资源不做权限保护
        http.authorizeRequests()
                .antMatchers("/css/**")
                .permitAll()
                .antMatchers("/js/**")
                .permitAll()
                // 404 可访问
                .regexMatchers("/404.html")
                .permitAll();

        http.formLogin()
                .loginPage("/login")
                .failureUrl("/login.html?error")
                .successHandler(webLoginAuthenticationSuccessHandler)
                .usernameParameter("user-name")
                .passwordParameter("password")
//                .authenticationDetailsSource(request -> {
//                    Map<String, String> map = new HashMap<>(16);
//                    map.put("username", request.getParameter("username"));
//                    map.put("email", request.getParameter("email"));
//                    map.put("password", request.getParameter("password"));
//                    return map;
//                })
                .and()
//                .rememberMe().key(REMEMBER_ME_PARAMETER)
//                .rememberMeParameter(REMEMBER_ME_PARAMETER)
//                .tokenRepository(tokenRepository())
//                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
        ;

    }

    @Bean
    PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setJdbcTemplate(jdbcTemplate);
        jdbcTemplate.execute("CREATE TABLE IF  NOT EXISTS persistent_logins (" +
                "  username VARCHAR ( 64 ) NOT NULL," +
                "  series VARCHAR ( 64 ) PRIMARY KEY," +
                "  token VARCHAR ( 64 ) NOT NULL," +
                "  last_used TIMESTAMP NOT NULL " +
                "  )");
        return jdbcTokenRepository;
    }
}
