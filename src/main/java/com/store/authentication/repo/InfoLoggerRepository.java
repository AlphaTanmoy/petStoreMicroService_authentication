package com.store.authentication.repo;

import com.store.authentication.model.InfoLogger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfoLoggerRepository extends JpaRepository<InfoLogger,String> {
}
