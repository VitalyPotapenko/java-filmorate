package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

public class UserDTO {

    @Data
    public static class CreateUserDTO {
        protected String email;
        protected String login;
        protected String name;
        protected LocalDate birthday;
    }

    @Data
    public static class UpdateUserDTO extends CreateUserDTO {
        private int id;
    }
}
