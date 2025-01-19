package com.store.authentication.error;

import com.store.authentication.enums.RESPONSE_TYPE;
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
    private RESPONSE_TYPE responseType;
    private ZonedDateTime timeStamp;
}
