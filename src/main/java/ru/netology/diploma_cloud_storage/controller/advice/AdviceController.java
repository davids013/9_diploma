package ru.netology.diploma_cloud_storage.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.diploma_cloud_storage.domain.ErrorMessage;
import ru.netology.diploma_cloud_storage.exception.*;

import javax.validation.ValidationException;
import java.util.concurrent.atomic.AtomicInteger;

@RestControllerAdvice("ru.netology.diploma_cloud_storage.controller")
public class AdviceController {
    private final AtomicInteger errorId = new AtomicInteger(-1);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage defaultExHandler(Exception e) {
        final String message = e.getMessage();
        System.err.println(e.getClass() + ": " + message);
        final int id = errorId.incrementAndGet();
        return new ErrorMessage(message, id);
    }

    @ExceptionHandler(UnauthorizedErrorException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessage unauthorizedErrorExceptionHandler(UnauthorizedErrorException e) {
        return buildErrorMessage(e);
    }

    @ExceptionHandler(ErrorDeleteFileException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage errorDeleteExceptionHandler(ErrorDeleteFileException e) {
        return buildErrorMessage(e);
    }

    @ExceptionHandler(ErrorInputDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage errorInputDataExceptionHandler(ErrorInputDataException e) {
        return buildErrorMessage(e);
    }

    @ExceptionHandler(ErrorGettingListException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage errorInputDataExceptionHandler(ErrorGettingListException e) {
        return buildErrorMessage(e);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage validationExceptionHandler(ValidationException e) {
        e.printStackTrace();
        return buildErrorMessage(new ErrorInputDataException("input", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        e.printStackTrace();
        final String target = "default message";
        String mes = e.getMessage();
        mes = (!mes.contains(target))
                ? mes
                : mes
                .substring(mes.lastIndexOf(target) + target.length() + 2, mes.length() - 3)
                .replace("\"", "'");
        return buildErrorMessage(new ErrorInputDataException("input", "filename " + mes));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage missingRequestHeaderExceptionHandler(MissingRequestHeaderException e) {
        final String mes = e.getMessage();
        return buildErrorMessage(new UnauthorizedErrorException("request", mes));
    }



//    @ExceptionHandler(ErrorUploadFileException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ErrorMessage errorUploadFileExceptionHandler(ErrorUploadFileException e) {
//        final int id = -2;    //TODO: fix id generating
//        return buildErrorMessage(e, id);
//    }

    private ErrorMessage buildErrorMessage(CloudStorageException e) {
        final String message = e.getOutputMessage();
        final int id = errorId.incrementAndGet();
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
