package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserStorage {
    private int id = 0;

    @Getter
    @Setter
    private HashMap<Integer, User> users = new HashMap<>();

    public User createUser(UserDTO.CreateUserDTO userDTO) {
        User user = new User(userDTO);
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(UserDTO.UpdateUserDTO userDTO) {
        int id = userDTO.getId();
        if (users.containsKey(id)) {
            User user = users.get(id);
            user.setName(userDTO.getName());
            user.setEmail(userDTO.getEmail());
            user.setLogin(userDTO.getLogin());
            user.setBirthday(userDTO.getBirthday());
            return user;
        } else {
            throw new IllegalArgumentException("Не удалось обновить пользователя. Пользователя с  Id = " + id
                    + " не существует");
        }
    }
}
