package ru.netology.diploma_cloud_storage.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import ru.netology.diploma_cloud_storage.exception.UnauthorizedErrorException;
import ru.netology.diploma_cloud_storage.repository.UserRepository;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepo;
    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

//    @Autowired
//    private DataSource dataSource;

    @Bean
    public PasswordEncoder encoder() {
//        return NoOpPasswordEncoder.getInstance();
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//    private String encode(CharSequence rawPassword) {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(rawPassword);
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(username -> userRepo
                        .findByUsername(username)
                        .orElseThrow(
                                () -> new UnauthorizedErrorException("username", "user not found")
                        )
                );
//                .inMemoryAuthentication()
//                .withUser("admin").password(encode("iddqd")).roles("ADMIN").and()
//                .withUser("verified").password(encode("0000")).roles("VERIFIED").and()
//                .withUser("user").password(encode("0000")).roles("USER").and()
//                .withUser("guest").password("{noop}0000").roles("USER").accountLocked(true).and()
//                .withUser("reader").password(encode("0000")).roles("READ").and()
//                .withUser("writer").password(encode("0000")).roles("WRITE").and()
//                .withUser("deleter").password(encode("0000")).roles("DELETE");

//        auth.jdbcAuthentication().dataSource(dataSource)
//                .usersByUsernameQuery("SELECT name, surname, 'true' FROM persons WHERE name=?")
//                .authoritiesByUsernameQuery("SELECT name, phone_number FROM persons WHERE name=?");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // Set session management to stateless
                .cors()
                .and()
                .csrf().disable()

                // Set session management to stateless
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // Set unauthorized requests exception handler
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    ex.getMessage()
                            );
                        }
                )

                // Set permissions on endpoints// Set permissions on endpoints
                .and()
                .authorizeRequests().antMatchers("/cloud/test**").permitAll()
                .and()
                .authorizeRequests().antMatchers(HttpMethod.POST, "/cloud/login").permitAll()
//                .and().authorizeRequests().antMatchers("/cloud/file**").hasRole("ADMIN")
//                .and().authorizeRequests().antMatchers("/cloud/list**").hasAnyRole("VERIFIED", "ADMIN")
                .and()
                .authorizeRequests().anyRequest().authenticated()
                .and()
//                .cors();
//                .and()
//                .authorizeRequests().anyRequest().permitAll();

                // Add JWT token filter
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
        ;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("https://localhost:8080"));
//        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
//        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
}
