package com.store.authentication.model.superEntity;

import com.store.authentication.utils.GenerateUUID;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;

@Data
@MappedSuperclass
public abstract class SuperEntityWithOutDataStatus {
    @Id
    private String id = GenerateUUID.generateShortUUID();

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdDate = ZonedDateTime.now();

    @Column(nullable = false)
    private ZonedDateTime expiryDate = ZonedDateTime.now();
}
