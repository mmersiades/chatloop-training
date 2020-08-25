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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
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
            http.cors().configurationSource(request -> getCorsConfiguration())
                    .and().csrf().disable()
                    .authorizeRequests().antMatchers("/graphql").authenticated()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ;
            http.addFilter(new JwtFilter(authenticationManagerBean(), authenticationExtractor));

//            http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

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

        /**
         * Due to having troubles reproducing preflight issue myself (prefligh requests are not supported in Postman?),
         * will use both this method, as well as reusing same conf in above's configurationSource().
         *
         * Based on https://stackoverflow.com/questions/36968963/how-to-configure-cors-in-a-spring-boot-spring-security-application
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = getCorsConfiguration();
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            log.info("CORS configured, allowed origins {}, allowed methods {}, allowed headers {}",
                    configuration.getAllowedOrigins(), configuration.getAllowedMethods(), configuration.getAllowedHeaders());
            return source;
        }

        private CorsConfiguration getCorsConfiguration() {
            List<String> allowedOrigins = Collections.singletonList("*");
            List<String> allowedMethods = Collections.singletonList("*");
            List<String> allowedHeaders = Collections.singletonList("*");

            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(allowedOrigins);
            configuration.setAllowedMethods(allowedMethods);
            configuration.setAllowedHeaders(allowedHeaders);

            return configuration;
        }
    }

    private boolean isLocalOrTest() {
        List<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        return activeProfiles.contains("test") || activeProfiles.contains("local");
    }
}

