package ru.netology.diploma_cloud_storage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CloudFile {
    private String hash;
    private String file;
}
