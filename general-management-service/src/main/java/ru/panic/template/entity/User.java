package ru.panic.template.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.panic.template.enums.Gender;
import ru.panic.template.enums.Rank;
@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    @Embedded
    private Data data;
    @Embedded
    private UserData userData;
    private Long timestamp;
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data{
        private Double btcBalance;
        private Double ethBalance;
        private Double ltcBalance;
        private Double tetherERC20Balance;
        private Double trxBalance;
        private Double xrpBalance;
        private Double maticBalance;
        private Double tonBalance;
        private String ipAddress;
        @Embedded
        private Level level;
        private Boolean isAccountNonLocked;
        private Boolean isMultiAccount;
        @AllArgsConstructor
        @NoArgsConstructor
        @lombok.Data
        public static class Level{
            private Rank rank;
            private Double progress;
        }
    }
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserData{
        private String firstname;
        private String lastname;
        private String birthday;
        private Gender gender;
        @Embedded
        private Address address;
        @NoArgsConstructor
        @AllArgsConstructor
        @lombok.Data
        public static class Address{
            private String country;
            private String street;
            private Integer postcode;
            private String city;
        }
    }
}
