package com.store.authentication.repo;

import com.store.authentication.model.AuthUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository  extends JpaRepository<AuthUsers,String> {

    AuthUsers findByEmail(String email);

    @Query(
            value = "SELECT COUNT(*) FROM auth_users u WHERE u.email = :email"
            , nativeQuery = true
    )
    Long countUserByEmail(@Param("email") String email);
}
