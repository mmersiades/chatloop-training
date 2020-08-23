package com.chatloop.training.web.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.util.Arrays;
import java.util.List;


/**
 *
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {

    @Autowired
    private Environment env;
    @Autowired
    private AuthenticationExtractor authenticationExtractor;
    @Autowired
    private JwtAuthenticationEntryPoint entryPoint;

    @Configuration
    public class OurWebSecConfAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.cors().and().csrf().disable()
                    .authorizeRequests().antMatchers("/graphql").authenticated()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ;
            http.addFilter(new JwtFilter(authenticationManagerBean(), authenticationExtractor));

            log.info("HTTP Security configured");
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            log.info("Path /webhook is intentionally not secured (to be called by Contentful)");
            web.ignoring().antMatchers("/webhook/**");
            if(isLocalOrTest()) {
                log.warn("Local or test profile detected, HTTP endpoints /h2-console and /graphiql are not secured.");
                web.ignoring().antMatchers("/h2-console/**");
                web.ignoring().antMatchers("/graphiql/**");
            }
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(new NoopAuthProvider());
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return new NoopAuthManager();
        }
    }

    private boolean isLocalOrTest() {
        List<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        return activeProfiles.contains("test") || activeProfiles.contains("local");
    }
}

