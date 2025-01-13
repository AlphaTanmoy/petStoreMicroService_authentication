package com.store.authentication.repo;

import com.store.authentication.model.UserLogs;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserLogsRepository extends JpaRepository<UserLogs,String> {

    @Query(
            value = "SELECT COUNT(*) FROM user_logs ul JOIN users u ON ul.user_id = u.id WHERE u.email = :email"
            , nativeQuery = true
    )
    int findCountByEmail(@Param("email") String email);


    @Transactional
    @Modifying
    @Query(
            value = "DELETE FROM user_logs WHERE id = " +
                    "(SELECT ul.id FROM " +
                    "user_logs ul JOIN users u ON ul.user_id = u.id " +
                    "WHERE u.email = :email " +
                    "ORDER BY created_date DESC " +
                    "LIMIT 1 ) "
            , nativeQuery = true
    )
    void deleteAllByEmailAndLastCreated(@Param("email") String email);

}
