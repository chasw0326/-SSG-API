package com.ssg.ssgproductapi.exception;


import com.ssg.ssgproductapi.exception.custom.AlreadyExistsException;
import com.ssg.ssgproductapi.exception.custom.ForbiddenException;
import com.ssg.ssgproductapi.exception.custom.SsgRuntimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Getter
    @AllArgsConstructor
    static class ErrorResult<T> {
        private T message;

        public static <T> ErrorResult from(T exceptionMessage) {
            return new ErrorResult<>(exceptionMessage);
        }
    }

    private <T> ResponseEntity<ErrorResult<T>> createErrorResponse(T exception, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(ErrorResult.from(exception), headers, status);
    }

    // @Valid exception catch
    @ExceptionHandler({MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            MethodArgumentConversionNotSupportedException.class})
    public ResponseEntity<?> validExceptionHandler(BindException ex) {
        log.warn("valid Error: {}", ex.getMessage());
        ValidExceptionDTO dto = ValidExceptionDTO.toDto(ex);
        return createErrorResponse(dto, HttpStatus.CONFLICT);
    }

    // return CONFLICT
    @ExceptionHandler({AlreadyExistsException.class})
    public ResponseEntity<?> alreadyExistsHandler(AlreadyExistsException ex){
        log.warn("alreadyExist Error: {}", ex.getMessage());
        String message = ex.getMessage();
        return createErrorResponse(message, HttpStatus.CONFLICT);
    }

    // return FORBIDDEN
    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<?> forbiddenHandler(ForbiddenException ex){
        log.warn("forbidden Error: {}", ex.getMessage());
        String message = ex.getMessage();
        return createErrorResponse(message, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({SsgRuntimeException.class})
    public ResponseEntity<?> ssgRuntimeHandler(SsgRuntimeException ex){
        String message = ex.getMessage();
        log.warn("ssgRuntimeException: {}", message);
        return createErrorResponse(message, HttpStatus.BAD_REQUEST);
    }

    // for ValidateUtil
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<?> validateUtilHandler(ConstraintViolationException ex){
        log.warn("validate Error: {}", ex.getMessage());
        String message = ex.getMessage();
        return createErrorResponse(message, HttpStatus.BAD_REQUEST);
    }

    // Validation(@Valid ??????) exception catch
    @ExceptionHandler({
        IllegalArgumentException.class, IllegalStateException.class,
        TypeMismatchException.class, HttpMessageNotReadableException.class,
        MissingServletRequestParameterException.class, MultipartException.class,
    })
    public ResponseEntity<?> badRequestExceptionHandler(Exception ex) {
        log.warn("Bad request exception occurred: {}: {}", ex.getMessage());
        String message = ex.getMessage();
        return createErrorResponse(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<?> exceptionHandler(Exception ex) {
        String message = ex.getMessage();
        log.error("Unexpected Error: {}", message, ex);
        String clientMessage = "??? ??? ?????? ????????? ??????????????????. ?????? ??????????????? ???????????????.";
        return createErrorResponse(clientMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
