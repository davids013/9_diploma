package ru.netology.diploma_cloud_storage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netology.diploma_cloud_storage.domain.AuthToken;
import ru.netology.diploma_cloud_storage.domain.FileSize;
import ru.netology.diploma_cloud_storage.domain.Filename;
import ru.netology.diploma_cloud_storage.domain.User;
import ru.netology.diploma_cloud_storage.service.CloudService;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.List;


@RestController
@Validated
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

    @PostMapping("login")   //TODO: fix it
    @ResponseStatus(HttpStatus.OK)
    public AuthToken login(@RequestBody @Valid User user) {
        final String answer = String.format("/login -> %s %s", user.getLogin(), user.getPassword());
        System.out.println(answer);
        return new AuthToken(user.getLogin());
    }

    @PostMapping("logout")  //TODO: fix it
    @ResponseStatus(HttpStatus.OK)
    public void logout(@RequestHeader(name = "auth-token") @Valid AuthToken authToken) {
        final String answer = String.format("/logout -> %s", authToken.getAuthToken());
        System.out.println(answer);
    }

    @PostMapping("file")
    @ResponseStatus(HttpStatus.OK)
    public void uploadFile(@RequestHeader(name = "auth-token") @Valid AuthToken authToken,
                           @RequestParam @Valid Filename filename,
                           @RequestPart @Valid @Pattern(regexp = "[A-z\\d]+") String hash,
                           @RequestPart @Valid @Pattern(regexp = "[01\\s]+") String file) {
        final String answer = String.format("filePost -> %s %s %s", authToken.getAuthToken(), filename.getFilename(), hash);
        System.out.println(answer);
        service.uploadFile(filename.getFilename(), hash, file);
    }

    @DeleteMapping("file")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(@RequestHeader(name = "auth-token") @Valid AuthToken authToken,
                           @RequestParam @Valid Filename filename) {
        final String answer = String.format("fileDelete -> %s %s", authToken.getAuthToken(), filename.getFilename());
        System.out.println(answer);
        service.deleteFile(filename.getFilename());
    }

    @GetMapping(value = "file", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public MultiValueMap<String, String> downloadFile(
            @RequestHeader(name = "auth-token") @Valid AuthToken authToken,
            @RequestParam @Valid Filename filename) {
        final String answer = String.format("fileGet -> %s %s", authToken.getAuthToken(), filename.getFilename());
        System.out.println(answer);
        return service.downloadFile(filename.getFilename());
    }

    @PutMapping("file")
    @ResponseStatus(HttpStatus.OK)
    public void renameFile(@RequestHeader(name = "auth-token") @Valid AuthToken authToken,
                           @RequestParam @Valid Filename filename,
                           @RequestBody @Valid @Pattern(regexp = "[()А-я\\w\\s\\.,-]+") String name) {
        final String answer = String.format("filePut -> %s %s %s", authToken.getAuthToken(), filename.getFilename(), name);
        System.out.println(answer);
        service.renameFile(filename.getFilename(), name);
    }

    @GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<FileSize> getFileList(@RequestHeader(name = "auth-token") @Valid AuthToken authToken,
                                      @RequestParam @Valid @Positive int limit) {
        final String answer = String.format("list -> %s %d", authToken.getAuthToken(), limit);
        System.out.println(answer);
        return service.getFileList(limit);
    }
}
