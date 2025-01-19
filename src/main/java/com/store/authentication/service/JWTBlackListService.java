package com.store.authentication.service;

import com.store.authentication.enums.DATA_STATUS;
import com.store.authentication.enums.USER_ROLE;
import com.store.authentication.error.BadRequestException;
import com.store.authentication.model.JwtBlackList;
import com.store.authentication.model.User;
import com.store.authentication.repo.JWTBlackListRepository;
import com.store.authentication.request.JWTBlackListRequest;
import com.store.authentication.response.JWTBlackListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JWTBlackListService {

    private final JWTBlackListRepository jwtBlackListRepository;
    private final UserService userService;


    public boolean findBlackListedUserById(String userId){
        Long findUser = jwtBlackListRepository.findByUserId(userId);
        return findUser != 0;
    }


    public JWTBlackListResponse jwtBlackListOperator(JWTBlackListRequest jwtBlackListRequest, String actionTakerId, Boolean addToJwtBlackList) throws  BadRequestException{

        JWTBlackListResponse jwtBlackListResponse = new JWTBlackListResponse();
        BadRequestException badRequestException = new BadRequestException();

        Optional<User> actionTaker = userService.findUserById(actionTakerId);
        USER_ROLE adminOrMasterConfirmation = actionTaker.get().getRole();

        if(adminOrMasterConfirmation.toString().equals(USER_ROLE.ROLE_ADMIN.toString())
                || adminOrMasterConfirmation.toString().equals(USER_ROLE.ROLE_MASTER.toString())){
            badRequestException.setErrorMessage("Only Admin and Master Users can have this access! ");
        }

        Optional<User> foundUser = userService.findUserById(jwtBlackListRequest.getActionTakenForId());
        if(foundUser.isEmpty()){
            badRequestException.setErrorMessage("User not found");
            throw badRequestException;
        }

        Optional<JwtBlackList> foundUserFormJwtBlackList = jwtBlackListRepository.findById(jwtBlackListRequest.getActionTakenForId());
        if(foundUserFormJwtBlackList.isPresent()) {
            badRequestException.setErrorMessage("User already in JWT Black List");
            throw badRequestException;
        }

        DATA_STATUS currentDataStatus = foundUser.get().getDATASTATUS();
        if(currentDataStatus== DATA_STATUS.INACTIVE){
            badRequestException.setErrorMessage("User is already inactive");
            throw badRequestException;
        }else{
            foundUser.get().setDATASTATUS(DATA_STATUS.INACTIVE);
            userService.saveUser(foundUser.get());
        }

        if(addToJwtBlackList) {
            JwtBlackList jwtBlackList = new JwtBlackList();
            jwtBlackList.setActionTakenBy(actionTakerId);
            jwtBlackList.setComment(jwtBlackListRequest.getComment());
            jwtBlackList.setActionTakenOn(jwtBlackListRequest.getActionTakenForId());
            jwtBlackList.setDataStatus(DATA_STATUS.INACTIVE);
            jwtBlackListRepository.save(jwtBlackList);
            jwtBlackListResponse.setActionTakenOnUser(jwtBlackListRequest.getActionTakenForId());
            jwtBlackListResponse.setComment(jwtBlackListRequest.getComment());
            jwtBlackListResponse.setDataStatus(DATA_STATUS.INACTIVE);
        }
        else {
            jwtBlackListRepository.deleteById(jwtBlackListRequest.getActionTakenForId());
            jwtBlackListResponse.setActionTakenOnUser(jwtBlackListRequest.getActionTakenForId());
            jwtBlackListResponse.setComment(jwtBlackListRequest.getComment());
            jwtBlackListResponse.setDataStatus(DATA_STATUS.ACTIVE);
        }
        return jwtBlackListResponse;
    }
}
