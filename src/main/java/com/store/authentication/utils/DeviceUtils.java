package com.store.authentication.utils;

import com.store.authentication.response.GetDeviceDetails;
import org.springframework.stereotype.Component;

@Component
public class DeviceUtils {

    public GetDeviceDetails findDeviceDetails(String userAgentHeader){
        String operatingSystem;
        if (userAgentHeader.toLowerCase().contains("windows")) {
            operatingSystem = "Windows";
        } else if (userAgentHeader.toLowerCase().contains("mac")) {
            operatingSystem = "MacOS";
        } else if (userAgentHeader.toLowerCase().contains("android")) {
            operatingSystem = "Android";
        } else if (userAgentHeader.toLowerCase().contains("iphone")) {
            operatingSystem = "iOS";
        } else if (userAgentHeader.toLowerCase().contains("linux")) {
            operatingSystem = "Linux";
        }else if (userAgentHeader.toLowerCase().contains("ubuntu")) {
            operatingSystem = "Ubuntu";
        }else {
            operatingSystem = "Unknown";
        }

        String deviceType = userAgentHeader.toLowerCase().contains("mobile") ? "Mobile" : "Desktop";

        GetDeviceDetails details = new GetDeviceDetails();
        details.setOperatingSystem(operatingSystem);
        details.setDeviceType(deviceType);
        return details;
    }

}
