package ru.panic.template.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users_activity")
@Data
public class UserActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @Embedded
    private UserActivityData data;
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserActivityData{
        private String ipAddress;
        @Embedded
        private Geolocation geolocation;
        @Embedded
        private DeviceInfo deviceInfo;
        @Embedded
        private BrowserInfo browserInfo;
        private Long timestamp;

    }
    @NoArgsConstructor
    @AllArgsConstructor
    @lombok.Data
    public static class Geolocation{
        private Double latitude;
        private Double longitude;
        private Double accuracy;
    }
    @NoArgsConstructor
    @AllArgsConstructor
    @lombok.Data
    public static class DeviceInfo {
        private String deviceType;
        private String deviceName;
        private String operatingSystem;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @lombok.Data
    public static class BrowserInfo {
        private String browserName;
        private String browserVersion;
    }
}
