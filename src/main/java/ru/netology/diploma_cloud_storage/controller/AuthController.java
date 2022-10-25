package ru.netology.diploma_cloud_storage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netology.diploma_cloud_storage.domain.AuthToken;
import ru.netology.diploma_cloud_storage.domain.User;
import ru.netology.diploma_cloud_storage.service.AuthService;

import javax.validation.Valid;

@RestController
@Validated
@RequestMapping("/")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("login")
    @ResponseStatus(HttpStatus.OK)
    public AuthToken login(@RequestBody @Valid User user) throws Exception {
        final String answer = String.format("-----> POST   /login \t-> Logging in by user '%s'",
                user.getLogin());
        System.out.println(answer);
        return authService.createAuthToken(user.getLogin(), user.getPassword());
    }

    @PostMapping("logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@RequestHeader(name = "auth-token") @Valid AuthToken authToken) {
        final String answer = String.format("-----> POST   /logout \t-> Logging out by '%s'",
                authToken.getAuthToken());
        System.out.println(answer);
        authService.logout(authToken);
    }
}
