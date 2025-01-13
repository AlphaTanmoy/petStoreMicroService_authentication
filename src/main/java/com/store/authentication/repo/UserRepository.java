package com.store.authentication.repo;

import com.store.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User,String> {

    User findByEmail(String email);

}
