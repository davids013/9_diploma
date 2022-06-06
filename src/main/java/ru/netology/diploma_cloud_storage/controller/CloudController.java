package ru.netology.diploma_cloud_storage.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diploma_cloud_storage.db.entities.FileEntity;
import ru.netology.diploma_cloud_storage.domain.Name;
import ru.netology.diploma_cloud_storage.service.CloudService;

import java.util.ArrayList;
import java.util.List;


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

    @GetMapping(value = "test")
    public void test(@RequestParam String filename) {
        System.out.println("Test");
    }

    @PostMapping("login")   //TODO: fix it
    @ResponseStatus(HttpStatus.OK)
    public AuthToken login(@RequestBody LoginBody loginBody) {
        final String answer = String.format("/login -> %s %s", loginBody.getLogin(), loginBody.getPassword());
        System.out.println(answer);
        return new AuthToken(loginBody.getLogin());
    }

    @PostMapping("logout")  //TODO: fix it
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> logout(@RequestHeader(name = "auth-token") String authToken) {
        final String answer = String.format("/logout -> %s", authToken);
        System.out.println(answer);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @PostMapping(value = "file")
    @ResponseStatus(HttpStatus.OK)
    public void uploadFile(@RequestHeader(name = "auth-token") String authToken,
                           @RequestParam String filename,
                           @RequestPart String hash, @RequestPart MultipartFile file) {
        final String answer = String.format("filePost -> %s %s %s", authToken, filename, hash);
        System.out.println(answer);
        service.uploadFile(filename, hash, file);
    }

    @DeleteMapping("file")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(@RequestHeader(name = "auth-token") String authToken,
                           @RequestParam String filename) {
        final String answer = String.format("fileDelete -> %s %s", authToken, filename);
        System.out.println(answer);
        service.deleteFile(filename);
    }

    @GetMapping(value = "file", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public MultiValueMap<String, String> downloadFile(@RequestHeader("auth-token") String authToken,
                                                 @RequestParam String filename) {
        final String answer = String.format("fileGet -> %s %s", authToken, filename);
        System.out.println(answer);
        return service.downloadFile(filename);
    }

    @PutMapping("file")
    @ResponseStatus(HttpStatus.OK)
    public void renameFile(@RequestHeader(name = "auth-token") String authToken,
                                       @RequestParam String filename,
                                       @RequestBody Name name) {
        final String answer = String.format("filePut -> %s %s %s", authToken, filename, name);
        System.out.println(answer);
        service.renameFile(filename, name.getName());
    }

    @GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)    //TODO: fix it
    @ResponseStatus(HttpStatus.OK)
    public List<FileEntity> listGet(@RequestHeader(name = "auth-token") String authToken,
                                    @RequestParam int limit) {
        final String answer = String.format("list -> %s %d", authToken, limit);
        System.out.println(answer);
        List<FileEntity> list = new ArrayList<>();
        for (int i = 0; i < limit; i++)
            list.add(new FileEntity("filename" + i, "hash", "file"));
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
}
