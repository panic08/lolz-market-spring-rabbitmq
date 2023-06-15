package ru.panic.template.dto;

import lombok.Data;
import ru.panic.template.entity.User;
import ru.panic.template.enums.Gender;
@Data
public class ChangeUserDataRequestDto {
        private String firstname;
        private String lastname;
        private String birthday;
        private Gender gender;
        private User.UserData.Address address;
}
