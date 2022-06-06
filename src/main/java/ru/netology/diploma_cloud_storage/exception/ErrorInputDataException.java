package ru.netology.diploma_cloud_storage.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorInputDataException extends CloudStorageException {
    private static final String ISSUE = "Error input data";

    public ErrorInputDataException(String target) {
        super(ISSUE, target);
    }

    public ErrorInputDataException(String target, String description) {
        super(ISSUE + " (" + description + ")", target);
    }
}
