package com.store.authentication.repo;

import com.store.authentication.model.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface APIKeyRepository extends JpaRepository<ApiKey,String> {

    @Query(
            value = "SELECT * FROM api_key WHERE created_for_user = :createdFor"
            , nativeQuery = true
    )
    ApiKey findByCreatedForId(@Param("createdFor") String createdFor);

    @Query(
            value="SELECT COUNT(*) FROM ( " +
                    "DELETE FROM api_key WHERE  " +
                    " )"
            ,nativeQuery = true
    )
    List<ApiKey> deleteApiKeyIfExpired();

    @Query(
            value = "SELECT * FROM api_key " +
                    "WHERE created_for_user = :createdForUser"
            ,nativeQuery = true
    )
    List<ApiKey> findByUserId(
            @Param("createdForUser") String createdForUser
    );

}
