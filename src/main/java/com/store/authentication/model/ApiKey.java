package com.store.authentication.model;

import com.store.authentication.model.superEntity.SuperEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "info_logger")
public class ApiKey extends SuperEntity {

    private String apiKey;
    private String createdForUser;
    private String createdByUser;

}
