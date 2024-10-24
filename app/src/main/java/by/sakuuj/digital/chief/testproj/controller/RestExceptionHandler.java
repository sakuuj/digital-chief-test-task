package by.sakuuj.digital.chief.testproj.controller;

import by.sakuuj.digital.chief.testproj.exception.BulkIndexingException;
import by.sakuuj.digital.chief.testproj.exception.CanNotCreateIndexException;
import by.sakuuj.digital.chief.testproj.exception.EntityNotFoundException;
import lombok.Builder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.awt.geom.NoninvertibleTransformException;

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

    @ExceptionHandler({EntityNotFoundException.class, NoninvertibleTransformException.class})
    public ResponseEntity<ApiError> handleBadRequests(Exception exception) {

        var apiError = ApiError.builder()
                .errorMsg(exception.getMessage())
                .build();

        return ResponseEntity
                .badRequest()
                .body(apiError);
    }
}
