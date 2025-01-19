package com.store.authentication.service;

import com.store.authentication.error.BadRequestException;
import com.store.authentication.model.TireManagement;
import com.store.authentication.repo.TireRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TireService {

    private final TireRepository tireRepository;

    public TireService(TireRepository tireRepository) {
        this.tireRepository = tireRepository;
    }

    public boolean hasAccess(String requestingTireCode, String apiPath) throws BadRequestException {
        TireManagement requestingTire = tireRepository.findByTireCode(requestingTireCode);
        if (requestingTire == null) {
            throw new IllegalArgumentException("Invalid tier code: " + requestingTireCode);
        }

        List<TireManagement> accessibleTiers = tireRepository.findByRankGreaterThanEqual(requestingTire.getRank());

        for (TireManagement tier : accessibleTiers) {
            if (tier.getApiAccessPath().contains(apiPath)) {
                return true;
            }
        }
        return false;
    }
}

