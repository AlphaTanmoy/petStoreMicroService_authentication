package com.store.authentication.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
    private String errorMessage;
    private String details;
    private String errorCode; // Custom error code
    private String errorType; // Custom error type
    private ZonedDateTime timeStamp;
}
