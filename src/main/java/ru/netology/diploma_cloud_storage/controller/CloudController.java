package ru.netology.diploma_cloud_storage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netology.diploma_cloud_storage.domain.AuthToken;
import ru.netology.diploma_cloud_storage.domain.FileSize;
import ru.netology.diploma_cloud_storage.domain.Filename;
import ru.netology.diploma_cloud_storage.domain.Name;
import ru.netology.diploma_cloud_storage.service.CloudService;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.List;


@RestController
@Validated
@RequestMapping("/cloud/")
public class CloudController {
    private final CloudService service;

    @Autowired
    public CloudController(CloudService service) {
        this.service = service;
    }

    @PostMapping("file")
    @ResponseStatus(HttpStatus.OK)
    public void uploadFile(@RequestHeader(name = "auth-token") @Valid AuthToken authToken,
                           @RequestParam @Valid Filename filename,
                           @RequestPart @Valid @Pattern(regexp = "[A-z\\d]+") String hash,
                           @RequestPart @Valid @Pattern(regexp = "[01\\s]+") String file) {
        final String answer = String.format("-----> POST   /file \t-> Adding file '%s' by '%s'", filename.getFilename(), authToken.getAuthToken());
        System.out.println(answer);
        service.uploadFile(filename.getFilename(), hash, file, authToken);
    }

    @DeleteMapping("file")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(@RequestHeader(name = "auth-token") @Valid AuthToken authToken,
                           @RequestParam @Valid Filename filename) {
        final String answer = String.format("-----> DELETE /file \t-> Deleting file '%s' by '%s'",
                filename.getFilename(), authToken.getAuthToken());
        System.out.println(answer);
        service.deleteFile(filename.getFilename(), authToken);
    }

    @GetMapping(value = "file", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public MultiValueMap<String, String> downloadFile(
            @RequestHeader(name = "auth-token") @Valid AuthToken authToken,
            @RequestParam @Valid Filename filename) {
        final String answer = String.format("-----> GET    /file \t-> Getting file '%s' by '%s'",
                filename.getFilename(), authToken.getAuthToken());
        System.out.println(answer);
        return service.downloadFile(filename.getFilename(), authToken);
    }

    @PutMapping(value = "file", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void renameFile(@RequestHeader(name = "auth-token") @Valid AuthToken authToken,
                           @RequestParam @Valid Filename filename,
                           @RequestBody @Valid Name name) {
        final String answer = String.format("-----> PUT    /file \t-> Renaming file '%s' to '%s' by '%s'",
                filename.getFilename(), name.getName(), authToken.getAuthToken());
        System.out.println(answer);
        service.renameFile(filename.getFilename(), name.getName(), authToken);
    }

    @GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<FileSize> getFileList(@RequestHeader(name = "auth-token") @Valid AuthToken authToken,
                                      @RequestParam @Valid @Positive int limit) {
        final String answer = String.format("-----> GET    /list \t-> Getting %d file list by '%s'",
                limit, authToken.getAuthToken());
        System.out.println(answer);
        return service.getFileList(limit, authToken);
    }
}
