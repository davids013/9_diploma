package ru.netology.diploma_cloud_storage.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
//@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig
        extends WebSecurityConfigurerAdapter {
//    @Autowired
//    private DataSource dataSource;

    @Bean
    public PasswordEncoder encoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        return new BCryptPasswordEncoder();
//        return NoOpPasswordEncoder.getInstance();
    }

//    @Bean
//    public BasicAuthenticationEntryPoint authenticationEntryPoint() {
//        BasicAuthenticationEntryPoint b = new BasicAuthenticationEntryPoint();
//        b.setRealmName("Realm");
//        return b;
//    }

//    @Bean
//    public RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter() throws Exception {
//        RequestHeaderAuthenticationFilter requestHeaderAuthFilter = new RequestHeaderAuthenticationFilter();
//        requestHeaderAuthFilter.setPrincipalRequestHeader("auth-token");
//        requestHeaderAuthFilter.setAuthenticationManager(authenticationManager());
//        requestHeaderAuthFilter.setExceptionIfHeaderMissing(false);
//        return requestHeaderAuthFilter;
//    }

//    @Bean
//    public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider() {
//        PreAuthenticatedAuthenticationProvider preAuthProvider = new PreAuthenticatedAuthenticationProvider();
//        preAuthProvider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(userDetailsService()));
//        return preAuthProvider;
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
//                .authenticationProvider(preAuthenticatedAuthenticationProvider())
                .inMemoryAuthentication()
                .withUser("admin").password(encoder().encode("iddqd")).roles("ADMIN");

//        auth.jdbcAuthentication().dataSource(dataSource)
//                .usersByUsernameQuery("SELECT name, surname, 'true' FROM persons WHERE name=?")
//                .authoritiesByUsernameQuery("SELECT name, phone_number FROM persons WHERE name=?");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .formLogin().and()
                .httpBasic()
//                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .cors().and()
                .authorizeRequests().antMatchers("/cloud/login").permitAll().and()
                .authorizeRequests().antMatchers("/cloud/test**").hasRole("ADMIN").and()
//                .and()
//                .addFilter(requestHeaderAuthenticationFilter())
                .authorizeRequests().anyRequest().authenticated();
    }

//                .csrf();
//                .formLogin()
//                .and().authorizeRequests().antMatchers("/cloud/test**").permitAll()
//                .and().authorizeRequests().antMatchers("/cloud/login**").permitAll()
//                .and().authorizeRequests().antMatchers("/cloud/file**").hasRole("ADMIN")
//                .and().authorizeRequests().antMatchers("/cloud/list**").hasAnyRole("VERIFIED", "ADMIN")
//                .and().authorizeRequests().anyRequest().authenticated();
//                .and()
//                .cors();
//                .and()
//                .authorizeRequests().anyRequest().permitAll();
//    }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("https://localhost:8080"));
//        configuration.setAllowedMethods(Arrays.asList("POST"));
//        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
}
