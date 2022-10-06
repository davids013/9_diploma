package ru.netology.diploma_cloud_storage.configuration.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.diploma_cloud_storage.domain.ErrorMessage;
import ru.netology.diploma_cloud_storage.exception.UnauthorizedErrorException;

@RestControllerAdvice("ru.netology.diploma_cloud_storage.configuration")
public class AdviceConfiguration {
    @ExceptionHandler(UnauthorizedErrorException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessage unauthorizedErrorExceptionHandler(UnauthorizedErrorException e) {
        final int id = 0;
        final String message = e.getOutputMessage();
        System.err.println(e.getClass() + ": " + message);
        return new ErrorMessage(message, id);
    }
}
