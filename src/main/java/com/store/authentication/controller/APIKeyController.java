package com.store.authentication.controller;

import com.store.authentication.config.JwtProvider;
import com.store.authentication.config.KeywordsAndConstants;
import com.store.authentication.error.BadRequestException;
import com.store.authentication.response.ApiKeyResponse;
import com.store.authentication.service.ApiKeyService;
import com.store.authentication.utils.ValidateForUUID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apiKey")
@RequiredArgsConstructor
public class APIKeyController {

    private final ApiKeyService apiKeyService;
    private final JwtProvider jwtProvider;

    @PostMapping("/createApiKey")
    public ApiKeyResponse createApiKey(
            @Valid @RequestHeader(value = KeywordsAndConstants.HEADER_AUTH_TOKEN, required = false) String token,
            @RequestBody String id
    ){
        BadRequestException badRequestException = new BadRequestException();
        String actionTakerId;
        if (token != null) {
            actionTakerId = jwtProvider.getIdFromJwtToken(token);
        } else {
            badRequestException.setErrorMessage("Invalid token");
            throw badRequestException;
        }
        ValidateForUUID.check(actionTakerId, "User");
        ValidateForUUID.check(id, "User");
        return apiKeyService.createApiKey(actionTakerId,id);
    }

    @PostMapping("/deleteApiKey")
    public ApiKeyResponse deleteApiKey(
            @Valid @RequestHeader(value = KeywordsAndConstants.HEADER_AUTH_TOKEN, required = false) String token,
            @RequestBody String id
    ){
        BadRequestException badRequestException = new BadRequestException();
        String actionTakerId;
        if (token != null) {
            actionTakerId = jwtProvider.getIdFromJwtToken(token);
        } else {
            badRequestException.setErrorMessage("Invalid token");
            throw badRequestException;
        }
        ValidateForUUID.check(actionTakerId, "User");
        ValidateForUUID.check(id, "User");
        return apiKeyService.deleteApiKey(actionTakerId,id);
    }

}