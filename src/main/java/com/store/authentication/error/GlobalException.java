package com.store.authentication.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.ZonedDateTime;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetails> handleBadRequestException(BadRequestException bdEx, WebRequest wbReq) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage(bdEx.getErrorMessage());
        errorDetails.setDetails(wbReq.getDescription(false));
        errorDetails.setErrorCode(bdEx.getErrorCode() != null ? bdEx.getErrorCode().toString() : "400");
        errorDetails.setErrorType(bdEx.getErrorType() != null ? bdEx.getErrorType() : "Bad Request");
        errorDetails.setTimeStamp(ZonedDateTime.now());

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}

