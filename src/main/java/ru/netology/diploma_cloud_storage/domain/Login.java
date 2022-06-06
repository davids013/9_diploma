package ru.netology.diploma_cloud_storage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Login {
    private String authToken;
}
