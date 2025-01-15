package com.store.authentication.repo;

import com.store.authentication.model.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface APIKeyRepository extends JpaRepository<ApiKey,String> {

    @Query(
            value = "SELECT * FROM api_key WHERE created_for_user = :createdFor"
            , nativeQuery = true
    )
    ApiKey findByCreatedForId(@Param("createdFor") String createdFor);

}
