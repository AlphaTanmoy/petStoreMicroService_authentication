package com.store.authentication.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.ZonedDateTime;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetails> badRequestException(BadRequestException bdEx, WebRequest wbReq){
        ErrorDetails ed = new ErrorDetails();
        ed.setErrorMessage(bdEx.getMessage());
        ed.setDetails(wbReq.getDescription(false));
        ed.setTimeStamp(ZonedDateTime.now());
        return new ResponseEntity<>(ed, HttpStatus.BAD_REQUEST);
    }

}
