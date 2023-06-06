package ru.yandex.practicum.filmorate.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserDTO;
import ru.yandex.practicum.filmorate.model.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage = new UserStorage();

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.getUsers().values();
    }

    @PostMapping
    public User create(@RequestBody UserDTO.CreateUserDTO userDTO) throws ValidationException {
        log.info("Обрабатываем запрос на создание пользователя: " + userDTO);

        if (isValid(userDTO)) {
            if (userDTO.getName() == null || userDTO.getName().isEmpty()) {
                userDTO.setName(userDTO.getLogin());
            }
            User user = userStorage.createUser(userDTO);
            HashMap<Integer, User> users = userStorage.getUsers();
            users.put(user.getId(), user);
            return user;
        } else {
            return null;
        }
    }

    @PutMapping
    public User createOrUpdate(@RequestBody UserDTO.UpdateUserDTO userDTO) throws ValidationException {
        log.info("Обрабатываем запрос на актуализцию сведений о пользователе: " + userDTO);
        User user;
        HashMap<Integer, User> users = userStorage.getUsers();
        if (isValid(userDTO)) {
            if (users.containsKey(userDTO.getId())) {
                int id = userDTO.getId();
                user = users.get(id);
                user.setName(userDTO.getName());
                user.setEmail(userDTO.getEmail());
                user.setLogin(userDTO.getLogin());
                user.setBirthday(userDTO.getBirthday());
            } else {
                throw new IllegalArgumentException("Не удалось обновить пользовател. Пользователя с  Id = " + userDTO.getId()
                        + " не существует");
            }
            users.put(user.getId(), user);
            return user;
        } else {
            return null;
        }
    }

    private boolean isValid(UserDTO.CreateUserDTO userDto) throws ValidationException {
        String email = userDto.getEmail();
        if (email == null || email.isEmpty() || !email.contains("@")) {
            throw new ValidationException("Передан некорректный адрес электронной почты пользователя: "
                    + "\"" + email + "\"");
        }
        String login = userDto.getLogin();
        if (login == null || login.isEmpty() || login.contains(" ")) {
            throw new ValidationException("Передан некорректный логин пользователя: "
                    + "\"" + login + "\"");
        }
        LocalDate now = LocalDate.now();
        LocalDate birthday = userDto.getBirthday();
        if (birthday.isAfter(now)) {
            throw new ValidationException("Дата рождения пользователя " + birthday
                    + " не может быть позже текущего момента " + now);
        }
        return true;
    }

}
