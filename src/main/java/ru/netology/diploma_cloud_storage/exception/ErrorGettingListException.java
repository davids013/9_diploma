package ru.netology.diploma_cloud_storage.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorGettingListException extends CloudStorageException {
    private static final String ISSUE = "Error getting list";

    public ErrorGettingListException(String target) {
        super(ISSUE, target);
    }
}
