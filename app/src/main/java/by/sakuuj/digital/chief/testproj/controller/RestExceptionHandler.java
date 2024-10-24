package by.sakuuj.digital.chief.testproj.controller;

import by.sakuuj.digital.chief.testproj.exception.BulkIndexingException;
import by.sakuuj.digital.chief.testproj.exception.CanNotCreateIndexException;
import by.sakuuj.digital.chief.testproj.exception.EntityNotFoundException;
import by.sakuuj.digital.chief.testproj.exception.NotMatchingEntityVersionException;
import lombok.Builder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

    @Builder
    public record ApiError(String errorMsg) {
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnhandled(Exception exception) {

        var apiError = ApiError.builder()
                .errorMsg(exception.getMessage())
                .build();

        return ResponseEntity
                .internalServerError()
                .body(apiError);
    }

    @ExceptionHandler({BulkIndexingException.class, CanNotCreateIndexException.class})
    public ResponseEntity<ApiError> handleInternalErrors(Exception exception) {

        var apiError = ApiError.builder()
                .errorMsg(exception.getMessage())
                .build();

        return ResponseEntity
                .internalServerError()
                .body(apiError);
    }

    @ExceptionHandler({
            EntityNotFoundException.class,
            NotMatchingEntityVersionException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiError> handleBadRequests(Exception exception) {

        var apiError = ApiError.builder()
                .errorMsg(exception.getMessage())
                .build();

        return ResponseEntity
                .badRequest()
                .body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {

        String errorMessage = exception.getFieldErrors()
                .stream()
                .map(error -> "'%s' %s".formatted(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        var apiError = ApiError.builder()
                .errorMsg(errorMessage)
                .build();

        return ResponseEntity
                .badRequest()
                .body(apiError);
    }
}
