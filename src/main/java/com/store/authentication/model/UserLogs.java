package com.store.authentication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.store.authentication.utils.GenerateUUID;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "user_logs")
public class UserLogs {
    @Id
    private String id = GenerateUUID.generateShortUUID();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false, unique = true)
    private String deviceId;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String jwtToken;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column
    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    private String deviceType;
    private String operatingSystem;

}
