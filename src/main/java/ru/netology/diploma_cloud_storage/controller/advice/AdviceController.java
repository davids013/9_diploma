package ru.netology.diploma_cloud_storage.controller.advice;

import javax.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.diploma_cloud_storage.domain.ErrorMessage;
import ru.netology.diploma_cloud_storage.exception.*;

@RestControllerAdvice("ru.netology.diploma_cloud_storage.controller")
public class AdviceController {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage defaultExHandler(Exception e) {
        final String message = e.getMessage();
        System.err.println(e.getClass() + ": " + message);
        final int id = -1;    //TODO: fix id generating
        return new ErrorMessage(message, id);
    }

    @ExceptionHandler(UnauthorizedErrorException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessage unauthorizedErrorExceptionHandler(UnauthorizedErrorException e) {
        final int id = -2;    //TODO: fix id generating
        return buildErrorMessage(e, id);
    }

    @ExceptionHandler(ErrorDeleteFileException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage errorDeleteExceptionHandler(ErrorDeleteFileException e) {
        final int id = -3;    //TODO: fix id generating
        return buildErrorMessage(e, id);
    }

    @ExceptionHandler(ErrorInputDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage errorInputDataExceptionHandler(ErrorInputDataException e) {
        final int id = -4;    //TODO: fix id generating
        return buildErrorMessage(e, id);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage validationExceptionHandler(ValidationException e) {
        final int id = -5;    //TODO: fix id generating
        return buildErrorMessage(new ErrorInputDataException("input", e.getMessage()), id);
    }

//    @ExceptionHandler(ErrorUploadFileException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ErrorMessage errorUploadFileExceptionHandler(ErrorUploadFileException e) {
//        final int id = -2;    //TODO: fix id generating
//        return buildErrorMessage(e, id);
//    }

    private ErrorMessage buildErrorMessage(CloudStorageException e, int id) {
        final String message = e.getOutputMessage();
        System.err.println(e.getClass() + ": " + message);
        return new ErrorMessage(message, id);
    }

//    @ExceptionHandler(ValidationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public String constViolExHandler(ValidationException e) {
//        System.err.println(e.getClass() + ": " + e.getMessage());
//        return "{\"Status\":" + HttpStatus.BAD_REQUEST.value() +
//                ",\"ValidationException\":\"" + e.getMessage() + "\"}";
//    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public String metArgNotValidExHandler(MethodArgumentNotValidException e) {
//        final String message = e.getMessage();
//        final String target = "default message [";
//        System.err.println(e.getClass() + ": " + message);
//        final int index = message.lastIndexOf(target) + target.length();
//        return "{\"Status\":" + HttpStatus.BAD_REQUEST.value() +
//                ",\"MethodArgumentNotValidException\":\"" + message.substring(index, message.length() - 3) + "\"}";
//    }
}
