package ru.netology.diploma_cloud_storage.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorDeleteFileException extends CloudStorageException {
    private static final String ISSUE = "Error delete file";

    public ErrorDeleteFileException(String target) {
        super(ISSUE, target);
    }
}
