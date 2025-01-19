package com.store.authentication.controller;

import com.store.authentication.config.JwtProvider;
import com.store.authentication.config.KeywordsAndConstants;
import com.store.authentication.error.BadRequestException;
import com.store.authentication.request.JWTBlackListRequest;
import com.store.authentication.response.JWTBlackListResponse;
import com.store.authentication.service.JWTBlackListService;
import com.store.authentication.utils.ValidateForUUID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping()
@RequiredArgsConstructor
public class JWTBlackListController {

    private final JWTBlackListService jwtBlackListService;
    private final JwtProvider jwtProvider;

    @PostMapping("/addToBlackList")
    public JWTBlackListResponse addToBlackList(
            @Valid @RequestHeader(value = KeywordsAndConstants.HEADER_AUTH_TOKEN, required = false)
            String token,
            @RequestBody JWTBlackListRequest jwtBlackListRequest
    ) {

        BadRequestException badRequestException = new BadRequestException();
        String actionTakerId;
        if (token != null) {
            actionTakerId = jwtProvider.getIdFromJwtToken(token);
        } else {
            badRequestException.setErrorMessage("Invalid token");
            throw badRequestException;
        }
        ValidateForUUID.check(actionTakerId, "User");
        ValidateForUUID.check(jwtBlackListRequest.getActionTakenForId(), "User");

        return jwtBlackListService.jwtBlackListOperator(jwtBlackListRequest, actionTakerId, true);

    }

    @PostMapping("/removeFromBlackList")
    public JWTBlackListResponse removeFromBlackList(
            @Valid @RequestHeader(value = KeywordsAndConstants.HEADER_AUTH_TOKEN, required = false)
            String token,
            @RequestBody JWTBlackListRequest jwtBlackListRequest
    ) {

        BadRequestException badRequestException = new BadRequestException();
        String actionTakerId;
        if (token != null) {
            actionTakerId = jwtProvider.getIdFromJwtToken(token);
        } else {
            badRequestException.setErrorMessage("Invalid token");
            throw badRequestException;
        }
        ValidateForUUID.check(actionTakerId, "User");
        ValidateForUUID.check(jwtBlackListRequest.getActionTakenForId(), "User");

        return jwtBlackListService.jwtBlackListOperator(jwtBlackListRequest, actionTakerId, false);

    }

    @PostMapping("/findBlackListedUser/{userId}")
    public boolean isBlackListedUser(
            @PathVariable String userId
    ){
        boolean isBlackListed = jwtBlackListService.findBlackListedUserById(userId);
        return isBlackListed;
    }

}
