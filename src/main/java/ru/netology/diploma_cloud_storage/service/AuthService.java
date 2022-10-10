package ru.netology.diploma_cloud_storage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.netology.diploma_cloud_storage.domain.JwtToken;
import ru.netology.diploma_cloud_storage.db.entities.UserEntity;
import ru.netology.diploma_cloud_storage.domain.AuthToken;
import ru.netology.diploma_cloud_storage.exception.UnauthorizedErrorException;
import ru.netology.diploma_cloud_storage.repository.UserRepository;

import java.util.ArrayList;

@Component
public class AuthService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtToken jwtToken;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final UserEntity user = userRepository.findByLogin(username);
        if (user == null) {
            throw new UnauthorizedErrorException("Authentication",
                    "User '" + username + "' not found");
        }
        return new User(user.getLogin(), user.getPassword(), new ArrayList<>());
    }

    public AuthToken createAuthToken(String login, String password) {
        if (!userRepository.existsByLogin(login)) {
            final String encodedPassword = new BCryptPasswordEncoder().encode(password);
            userRepository.save(new UserEntity(login, encodedPassword));
        }
        authenticate(login, password);
        final UserDetails userDetails = loadUserByUsername(login);
        final String token = jwtToken.generateToken(userDetails);
        return new AuthToken(token);
    }

    private void authenticate(String login, String password) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
            System.out.println("Authentication success for user '" + login + "'");
        } catch (DisabledException e) {
            throw new UnauthorizedErrorException(login, "USER_DISABLED");
        } catch (BadCredentialsException e) {
            throw new UnauthorizedErrorException(login, "INVALID_CREDENTIALS");
        }
    }
}

