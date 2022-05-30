package ru.netology.diploma_cloud_storage.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diploma_cloud_storage.service.CloudService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController
//@Validated
@RequestMapping("/cloud/")
@CrossOrigin
        (origins = "http://localhost:8080",
                methods = RequestMethod.POST,
                allowedHeaders = "content-type",
                allowCredentials = "true")
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
        final String answer = String.format("/login -> %s %s", loginBody.getLogin(), loginBody.getPassword());
        System.out.println(answer);
        return new AuthToken(loginBody.getLogin());
    }

    @PostMapping("logout")
    public ResponseEntity<String> logout(@RequestHeader(name = "auth-token") String authToken) {
        final String answer = String.format("/logout -> %s", authToken);
        System.out.println(answer);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @PostMapping(value = "file", produces = MediaType.APPLICATION_JSON_VALUE)
    public String filePost(@RequestHeader(name = "auth-token") String authToken,
                           @RequestParam String filename,
                           @RequestPart String hash, @RequestPart MultipartFile file) {
        final String answer = String.format("filePost -> %s %s %s %d", authToken, filename, hash, file.hashCode());
        System.out.println(answer);
        return answer;
    }

    @DeleteMapping("file")
    public String fileDelete(@RequestHeader(name = "auth-token") String authToken,
                             @RequestParam String filename) {
        final String answer = String.format("fileDelete -> %s %s", authToken, filename);
        System.out.println(answer);
        return answer;
    }

    @GetMapping("file")
    public String fileGet(@RequestHeader(name = "auth-token") String authToken,
                          @RequestParam String filename) {
        final String answer = String.format("fileGet -> %s %s", authToken, filename);
        System.out.println(answer);
        return answer + " need multipart!";
    }

    @PutMapping("file")
    public String filePut(@RequestHeader(name = "auth-token") String authToken,
                                       @RequestParam String filename,
                                       @RequestBody String name) {
        final String answer = String.format("filePut -> %s %s %s", authToken, filename, name);
        System.out.println(answer);
        return answer;
    }

    @GetMapping("list")
    public List<String> listGet(@RequestHeader(name = "auth-token") String authToken,
                          @RequestParam int limit) {
        final String answer = String.format("list -> %s %d", authToken, limit);
        System.out.println(answer);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < limit; i++) list.add(answer + " " + i);
        return list;
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
    @AllArgsConstructor
    private static class AuthToken {
        private final String auth_token;
    }

    @Data
    @AllArgsConstructor
    private static class ErrorMessage {
        private final String message;
        private final int id;
    }

    @Data
    @AllArgsConstructor
    private static class File {
        private final String hash;
        private final String file;
    }
}
