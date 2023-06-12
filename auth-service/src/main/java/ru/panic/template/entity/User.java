package ru.panic.template.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.panic.template.entity.enums.Gender;
import ru.panic.template.entity.enums.Rank;

import java.util.Collection;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {
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
        private Double solBalance;
        private Double trxBalance;
        private Double trc20Balance;
        private Double xrpBalance;
        private Double dogeBalance;
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
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }
    @Override
    public String getUsername() {
        return this.username;
    }
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return false;
    }
}
