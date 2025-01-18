package com.store.authentication.utils;

import com.store.authentication.customDTO.TwoParameterDTO;
import com.store.authentication.enums.DATE_RANGE_TYPE;
import com.store.authentication.request.ApiKeyGenerationRequest;
import org.springframework.stereotype.Component;

@Component
public class DateUtil {
    public static TwoParameterDTO checkValid(ApiKeyGenerationRequest apiKeyGenerationRequest) {
        DATE_RANGE_TYPE dateRangeType = apiKeyGenerationRequest.getExpiryDate();
        int expiryTime = switch (dateRangeType) {
            case ONE_DAYS -> 1;
            case ONE_WEAK -> 7;
            case FIFTEEN_DAYS -> 15;
            case ONE_MONTH -> 30;
            case THREE_MONTH -> 90;
            case SIX_MONTHS -> 180;
            case ONE_YEAR -> 265;
            case MAX -> 4000;
            default -> -1;
        };

        boolean isValid = !(expiryTime == -1);

        return new TwoParameterDTO<>(isValid,expiryTime);
    }
}
