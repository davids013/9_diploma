package ru.netology.diploma_cloud_storage.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AuthToken {
    @Length(min = 1, max = 256)
    @Pattern(regexp = "[\\w\\d\\s-.]+")
    @JsonProperty("auth-token")
    private String authToken;
}