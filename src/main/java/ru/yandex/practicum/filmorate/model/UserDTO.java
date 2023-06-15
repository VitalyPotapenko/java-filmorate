package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

public class UserDTO {

    @Data
    public static class CreateUserDTO {
        @Email
        @NotBlank
        protected String email;
        protected String login;
        protected String name;
        @Past
        protected LocalDate birthday;
    }

    @Data
    public static class UpdateUserDTO extends CreateUserDTO {
        private int id;

        public UpdateUserDTO() {
        }

        public UpdateUserDTO(CreateUserDTO createUserDto) {
            this.name = createUserDto.getName();
            this.login = createUserDto.getLogin();
            this.email = createUserDto.getEmail();
            this.birthday = createUserDto.getBirthday();

        }
    }
}
