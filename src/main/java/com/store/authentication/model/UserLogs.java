package com.store.authentication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.store.authentication.enums.MICROSERVICE;
import com.store.authentication.model.superEntity.SuperEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "user_logs")
public class UserLogs extends SuperEntity {

    @ManyToOne
    @JoinColumn(name = "auth_user_id", nullable = false)
    private AuthUsers user;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false, unique = true)
    private String deviceId;

    @Column(nullable = false, length = 2000)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String jwtToken;

    private String deviceType;
    private String operatingSystem;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MICROSERVICE microservice_name;
}
