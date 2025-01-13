package com.store.authentication.model;

import com.store.authentication.enums.INFO_LOG_TYPE;
import com.store.authentication.utils.GenerateUUID;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "info_logger")
public class InfoLogger {

    @Id
    private String id = GenerateUUID.generateShortUUID();

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private INFO_LOG_TYPE type;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;
}
