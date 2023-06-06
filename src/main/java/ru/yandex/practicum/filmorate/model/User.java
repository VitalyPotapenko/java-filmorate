package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private int id;
    private transient String email;
    private transient String login;
    private transient String name;
    private transient LocalDate birthday;

    public User(UserDTO.CreateUserDTO userDTO){
        this.email = userDTO.getEmail();
        this.login = userDTO.getLogin();
        this.name = userDTO.getName();
        this.birthday = userDTO.getBirthday();
    }
}
