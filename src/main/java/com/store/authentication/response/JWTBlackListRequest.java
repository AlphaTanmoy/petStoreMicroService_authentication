package com.store.authentication.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JWTBlackListRequest {
    private String comment;
    private String actionTakerId;
    private String actionTakenForId;
}
