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
public class Filename {
    @Length(min = 1, max = 64)
    @Pattern(regexp = "[()А-я\\w\\s\\.,-]+")
    private String filename;
}
