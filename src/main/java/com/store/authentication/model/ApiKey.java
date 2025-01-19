package com.store.authentication.model;

import com.store.authentication.enums.DATE_RANGE_TYPE;
import com.store.authentication.model.superEntity.SuperEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "api_key")
public class ApiKey extends SuperEntity {

    private String apiKey;
    private String createdForUser;
    private String createdByUser;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DATE_RANGE_TYPE expiryDateForApiKey;

    private ZonedDateTime timeToExpire;
    private Boolean isExpired = false;

}
