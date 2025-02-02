package com.store.authentication.service;

import com.store.authentication.error.BadRequestException;
import com.store.authentication.model.AuthUsers;
import com.store.authentication.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public void UserRepository(UserRepository userRepository) throws BadRequestException{
        this.userRepository = userRepository;
    }

    public Optional<AuthUsers> findUserById(String id) throws BadRequestException {
        return userRepository.findById(id);
    }

    public AuthUsers findUserByEmail(String email) throws BadRequestException{
        AuthUsers findUser = userRepository.findByEmail(email);
        BadRequestException badRequestException = new BadRequestException();
        badRequestException.setErrorMessage("User with " + email + " does not exist");
        if(findUser==null) throw badRequestException;
        else return findUser;
    }

    public void saveUser(AuthUsers user){
        userRepository.save(user);
    }
}
