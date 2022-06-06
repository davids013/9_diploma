package ru.netology.diploma_cloud_storage.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorUploadFileException extends CloudStorageException {
    private static final String ISSUE = "Error upload file";

    public ErrorUploadFileException(String target) {
        super(ISSUE, target);
    }
}
