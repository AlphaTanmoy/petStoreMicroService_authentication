package com.store.authentication.repo;

import com.store.authentication.model.JwtBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JWTBlackListRepository extends JpaRepository<JwtBlackList,String> {

    @Query(
            value = "SELECT COUNT(*) FROM jwt_black_list jwt WHERE jwt.action_taken_on = :id"
            , nativeQuery = true
    )
    Long findByUserId(@Param("id") String id);


}
