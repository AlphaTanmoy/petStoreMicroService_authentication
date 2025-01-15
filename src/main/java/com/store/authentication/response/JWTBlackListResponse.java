package com.store.authentication.response;

import com.store.authentication.enums.DATA_STATUS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JWTBlackListResponse {
    private String actionTakenOnUser;
    private DATA_STATUS dataStatus;
    private String comment;
}
