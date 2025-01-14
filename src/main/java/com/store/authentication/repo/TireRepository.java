package com.store.authentication.repo;

import com.store.authentication.model.TireManagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TireRepository extends JpaRepository<TireManagement, String> {
    TireManagement findByTireCode(String tireCode);
    List<TireManagement> findByRankGreaterThanEqual(int rank);
}
