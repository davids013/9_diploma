package ru.netology.diploma_cloud_storage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Pattern;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Length(min = 1, max = 32)
    @Pattern(regexp = "^[A-z]+[\\w\\.@-]+[A-z\\d]+$")
    private String login;
    @Length(min = 6, max = 64)
    @Pattern(regexp = "[\\w\\.-]+")
    private String password;
}