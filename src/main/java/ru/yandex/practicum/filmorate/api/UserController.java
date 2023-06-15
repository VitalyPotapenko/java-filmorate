package ru.yandex.practicum.filmorate.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserDTO;
import ru.yandex.practicum.filmorate.model.UserStorage;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserStorage userStorage;

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.getUsers().values();
    }

    @PostMapping
    public User create(@Valid @RequestBody UserDTO.CreateUserDTO userDTO) throws ValidationException {
        log.info("Обрабатываем запрос на создание пользователя: " + userDTO);
        validate(userDTO);
        return userStorage.createUser(userDTO);
    }

    @PutMapping
    public User update(@Valid @RequestBody UserDTO.UpdateUserDTO userDTO) throws ValidationException {
        log.info("Обрабатываем запрос на обновление сведений о пользователе: " + userDTO);
        validate(userDTO);
        return userStorage.updateUser(userDTO);
    }

    private void validate(UserDTO.CreateUserDTO userDto) throws ValidationException {
        String login = userDto.getLogin();
        if (login == null || login.isEmpty() || login.contains(" ")) {
            throw new ValidationException("Передан некорректный логин пользователя: "
                    + "\"" + login + "\"");
        }
        if (userDto.getName() == null || userDto.getName().isEmpty()) {
            userDto.setName(userDto.getLogin());
        }
    }
}
