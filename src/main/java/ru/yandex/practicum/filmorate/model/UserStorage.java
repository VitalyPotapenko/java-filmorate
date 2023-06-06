package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class UserStorage {
    private int id = 0;

    @Getter
    @Setter
    private HashMap<Integer, User> users = new HashMap<>();

    public User createUser(UserDTO.CreateUserDTO userDTO){
       User user = new User(userDTO);
       user.setId(++id);
       users.put(user.getId(), user);
       return user;
    }
}
