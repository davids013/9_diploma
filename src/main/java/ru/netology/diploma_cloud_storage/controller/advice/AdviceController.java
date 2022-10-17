package ru.netology.diploma_cloud_storage.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.netology.diploma_cloud_storage.domain.ErrorMessage;
import ru.netology.diploma_cloud_storage.exception.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.io.IOException;
import java.io.PrintWriter;

@RestControllerAdvice("ru.netology.diploma_cloud_storage.controller")
public class AdviceController implements AuthenticationEntryPoint {

    @ExceptionHandler(UnauthorizedErrorException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessage unauthorizedErrorExceptionHandler(UnauthorizedErrorException e) {
        final int id = 0;
        return buildErrorMessage(e, id);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage defaultExHandler(Exception e) {
        final String message = e.getMessage();
        System.err.println(e.getClass() + ": " + message);
        final int id = -1;
        return new ErrorMessage(message, id);
    }

    @ExceptionHandler(ErrorDeleteFileException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage errorDeleteExceptionHandler(ErrorDeleteFileException e) {
        final int id = -2;
        return buildErrorMessage(e, id);
    }

    @ExceptionHandler(ErrorInputDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage errorInputDataExceptionHandler(ErrorInputDataException e) {
        final int id = -3;
        return buildErrorMessage(e, id);
    }

    @ExceptionHandler(ErrorGettingListException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage errorInputDataExceptionHandler(ErrorGettingListException e) {
        final int id = -4;
        return buildErrorMessage(e, id);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage validationExceptionHandler(ValidationException e) {
        final int id = -5;
        e.printStackTrace();
        return buildErrorMessage(new ErrorInputDataException("input", e.getMessage()), id);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        final int id = -6;
        final String target = "default message";
        String mes = e.getMessage();
        mes = (!mes.contains(target))
                ? mes
                : mes
                    .substring(mes.lastIndexOf(target) + target.length() + 2, mes.length() - 3)
                    .replace("\"", "'");
        return buildErrorMessage(new ErrorInputDataException("input", "filename " + mes), id);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e) {
        final int id = -7;
        final String message = e.getMessage().substring(0, e.getMessage().indexOf(";"));
        return buildErrorMessage(new ErrorInputDataException("input", message), id);
    }

    private ErrorMessage buildErrorMessage(CloudStorageException e, int id) {
        final String message = e.getOutputMessage();
        System.err.println(e.getClass() + ": " + message);
        return new ErrorMessage(message, id);
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        final PrintWriter writer = response.getWriter();
        final String str = "{\r\n" +
                            "\t\"message\": " + "\"Unauthorized error. Access denied\",\r\n" +
                            "\t\"id\": " + 0 +
                            "\r\n}";
        writer.print(str);
        writer.flush();
        System.err.println("UnauthorizedError: Access denied");
    }
}
