package com.store.authentication.service;

import com.store.authentication.enums.CREATION_STATUS;
import com.store.authentication.model.ApiKey;
import com.store.authentication.repo.APIKeyRepository;
import com.store.authentication.response.ApiKeyResponse;
import com.store.authentication.utils.GenerateUUID;
import com.store.authentication.utils.ValidateForUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ValidateForUUID validateForUUID;
    private final GenerateUUID generateUUID;
    private final APIKeyRepository apiKeyRepository;


    public ApiKeyResponse createApiKey(String actionTakerId, String id){
        String apiKey = GenerateUUID.generateAPIKey();
        ApiKey newEntry = new ApiKey();
        newEntry.setCreatedByUser(actionTakerId);
        newEntry.setCreatedForUser(id);
        newEntry.setApiKey(apiKey);
        apiKeyRepository.save(newEntry);
        return new ApiKeyResponse(
              actionTakerId,
              id,
              apiKey,
              CREATION_STATUS.CREATED
        );
    }

    public ApiKeyResponse deleteApiKey(String actionTakerId, String id){
        ApiKey findApiKey = apiKeyRepository.findByCreatedForId(id);
        apiKeyRepository.delete(findApiKey);
        return new ApiKeyResponse(
                actionTakerId,
                id,
                "",
                CREATION_STATUS.DELETED
        );
    }

}
