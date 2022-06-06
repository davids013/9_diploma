package ru.netology.diploma_cloud_storage.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class CloudStorageException extends RuntimeException {
    protected String issue;
    protected String target;

    public String getOutputMessage(){
        return String.format("%s for '%s'", issue, target);
    }
}
