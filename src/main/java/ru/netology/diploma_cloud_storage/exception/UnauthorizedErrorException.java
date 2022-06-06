package ru.netology.diploma_cloud_storage.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UnauthorizedErrorException extends CloudStorageException {
    private static final String ISSUE = "Unauthorized error";

    public UnauthorizedErrorException(String target) {
        super(ISSUE, target);
    }
}
