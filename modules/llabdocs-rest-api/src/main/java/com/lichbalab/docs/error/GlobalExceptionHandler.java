package com.lichbalab.docs.error;

import com.lichbalab.cmc.core.CmcException;
import com.lichbalab.cmc.core.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

   private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ErrorResponseBuilder errorResponseBuilder;

    @Autowired
    public GlobalExceptionHandler(ErrorResponseBuilder errorResponseBuilder) {
        this.errorResponseBuilder = errorResponseBuilder;
    }

    @ExceptionHandler(CmcException.class)
    public ResponseEntity<ErrorResponse> handleCmsException(CmcException exception, HttpServletRequest request) {
        ErrorResponse error = errorResponseBuilder.buildErrorResponse(request, exception.getErrorCode(), exception.getParams());
        log.error(error.getMessage(), exception);
        return new ResponseEntity<>(error, getStatus(error.getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>  handleAnyOtherException(Exception exception, HttpServletRequest request) {
        ErrorResponse error = errorResponseBuilder.buildErrorResponse(request, ErrorCode.GENERAL, new Object[]{});
        log.error(error.getMessage(), exception);
        return new ResponseEntity<>(error, getStatus(error.getStatus()));
    }

    private static HttpStatus getStatus(int code) {
        HttpStatus status =  HttpStatus.resolve(code);
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return status;
    }
}