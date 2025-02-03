package com.store.authentication.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDeviceDetails {
    String operatingSystem;
    String deviceType;
}
