package com.store.authentication.utils;

import com.store.authentication.enums.TIRE_CODE;
import com.store.authentication.error.BadRequestException;
import com.store.authentication.model.AuthUsers;
import com.store.authentication.repo.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class ValidateTire {

    private static UserRepository userRepository = null;

    public ValidateTire(UserRepository userRepository) {
        ValidateTire.userRepository = userRepository;
    }

    public static boolean hierarchyTireManagement(String ownerID, String userID) {
        Optional<AuthUsers> findOwnerUser = userRepository.findById(ownerID);
        Optional<AuthUsers> findUser = userRepository.findById(userID);

        if (findOwnerUser.isPresent() && findUser.isPresent()) {
            BadRequestException badRequestException = new BadRequestException();
            badRequestException.setErrorMessage("Either Owner User or Target User not found!");
        }

        TIRE_CODE ownerTierCode = findOwnerUser.get().getTireCode();
        TIRE_CODE userTierCode = findOwnerUser.get().getTireCode();

        boolean equals1 = Objects.equals(
                ownerTierCode.toString(),
                TIRE_CODE.TIRE0.toString());
        if (equals1
                && Objects.equals(
                userTierCode.toString(),
                TIRE_CODE.TIRE0.toString()
        )
        ) return false;

        if (equals1
        ) return true;

        if (Objects.equals(
                ownerTierCode.toString(), TIRE_CODE.TIRE1.toString())
                && Objects.equals(userTierCode.toString(), TIRE_CODE.TIRE1.toString())
        ) return false;

        if (Objects.equals(
                ownerTierCode.toString(), TIRE_CODE.TIRE2.toString())
                || Objects.equals(ownerTierCode.toString(), TIRE_CODE.TIRE3.toString())
                || Objects.equals(ownerTierCode.toString(), TIRE_CODE.TIRE4.toString())
        ) return false;

        else return false;
    }

}
