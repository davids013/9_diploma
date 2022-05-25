package ru.netology.diploma_cloud_storage.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.netology.diploma_cloud_storage.service.CloudService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
//@Validated
@RequestMapping("/cloud/")
@CrossOrigin
//        (origins = "http://localhost:8080",
//                methods = RequestMethod.POST,
//                allowedHeaders = "content-type",
//                allowCredentials = "true")
public class CloudController {
    private final CloudService service;

    @Autowired
    public CloudController(CloudService service) {
        this.service = service;
    }

    @GetMapping("test")
    public String test() {
        System.out.println("Test");
        return service.test();
    }

    @PostMapping("login")
    public AuthToken login(@RequestBody LoginBody loginBody) {
        System.out.println("/login -> " + loginBody.getLogin() + ": " + loginBody.getPassword());
//        return service.test();
        return new AuthToken(loginBody.getLogin());
    }

    @GetMapping("logout")
    public String logout() {
        System.out.println("/logout");
        return service.test();
    }

    @PostMapping("file")
    public String filePost(@RequestBody LoginBody loginBody) {
        System.out.println("filePost");
        return "filePost";
    }

    @DeleteMapping("file")
    public String fileDelete() {
        System.out.println("fileDelete");
        return "fileDelete";
    }

    @GetMapping("file")
    public String fileGet() {
        System.out.println("fileGet");
        return "fileGet";
    }

    @GetMapping("list")
    public String listGet() {
        return "list";
    }

    @Data
//    @Validated
    @AllArgsConstructor
    @NoArgsConstructor
    private static class LoginBody {
        //        @Size(min = 4, max = 32)
        private String login;
        //        @Size(min = 4)
        private String password;
    }

    @Data
//    @Validated
    @AllArgsConstructor
    @NoArgsConstructor
    private static class AuthToken {
        private String auth_token;
    }
}
