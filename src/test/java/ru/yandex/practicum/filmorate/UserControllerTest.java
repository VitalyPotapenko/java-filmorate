package ru.yandex.practicum.filmorate;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.api.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserDTO;
import ru.yandex.practicum.filmorate.model.UserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @MockBean
    UserStorage userStorage;
    @Autowired
    MockMvc mockMvc;
    private String usersUrl = "/users";
    private String testUserName = "TestUserName";
    private String testUserLogin = "login";
    private String testUserEmail = "test.email@mail.server";
    private String testUserBirthday = "1970-01-01";
    private UserDTO.CreateUserDTO createUserDto;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeEach
    void init() {
        createUserDto = new UserDTO.CreateUserDTO();
        createUserDto.setName(testUserName);
        createUserDto.setLogin(testUserLogin);
        createUserDto.setEmail(testUserEmail);
        createUserDto.setBirthday(LocalDate.parse(testUserBirthday, dateFormatter));
    }

    @Test
    void test_GetUsers_HappyCase() throws Exception {
        //given
        HashMap<Integer, User> testUsers = new HashMap<>();
        testUsers.put(1, new User(createUserDto));
        //when
        when(userStorage.getUsers()).thenReturn(testUsers);
        //then
        ResultActions response = mockMvc.perform(get(usersUrl).contentType(MediaType.APPLICATION_JSON));
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
        assertUserFieldsValues("$[0]", response);
    }

    @Test
    void test_CreateUser_HappyCase() throws Exception {
        //given
        //when
        when(userStorage.createUser(createUserDto)).thenReturn(new User(createUserDto));
        //then
        ResultActions response = mockMvc.perform(post(usersUrl).contentType(MediaType.APPLICATION_JSON)
                .content(FilmorateApplication.jsonMapper.writeValueAsString(createUserDto)));
        response.andExpect(status().isOk());
        assertUserFieldsValues("$", response);
    }


    @ParameterizedTest
    @ValueSource(strings = {"", "email", "email@", "email@.server"})
    void test_CreateUser_OnlyValidEmailAddressShouldBeAccepted(String email) throws Exception {
        //given
        createUserDto.setEmail(email);
        //when
        when(userStorage.createUser(createUserDto)).thenReturn(new User(createUserDto));
        //then
        ResultActions response = mockMvc.perform(post(usersUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(FilmorateApplication.jsonMapper.writeValueAsString(createUserDto)));
        response.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }


    @ParameterizedTest
    @MethodSource("provideDatesToParameterizedTest")
    void test_CreateUser_BirthdayMustBeInThePast(LocalDate date) throws Exception {
        //given
        createUserDto.setBirthday(date);
        //when
        when(userStorage.createUser(createUserDto)).thenReturn(new User(createUserDto));
        //then
        ResultActions response = mockMvc.perform(post(usersUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(FilmorateApplication.jsonMapper.writeValueAsString(createUserDto)));
        response.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "a b"})
    void test_CreateUser_LoginMustNotBeNullOrEmptyOrContainSpaces(String login) throws Exception {
        //given
        createUserDto.setLogin(login);
        //when
        when(userStorage.createUser(createUserDto)).thenReturn(new User(createUserDto));
        //then
        ResultActions response = mockMvc.perform(post(usersUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(FilmorateApplication.jsonMapper.writeValueAsString(createUserDto)));
        response.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException));
    }

    @Test
    void test_CreateUser_IfUserNameIsEmptyItShouldGetReplacedByLogin() throws Exception {
        //given
        createUserDto.setName("");
        UserDTO.CreateUserDTO resultCreateUserDto = new UserDTO.CreateUserDTO();
        resultCreateUserDto.setName(testUserLogin);
        resultCreateUserDto.setLogin(testUserLogin);
        resultCreateUserDto.setEmail(testUserEmail);
        resultCreateUserDto.setBirthday(LocalDate.parse(testUserBirthday, dateFormatter));
        //when
        when(userStorage.createUser(resultCreateUserDto)).thenReturn(new User(resultCreateUserDto));
        //then
        ResultActions response = mockMvc.perform(post(usersUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(FilmorateApplication.jsonMapper.writeValueAsString(createUserDto)));
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.name").value(testUserLogin))
                .andExpect((jsonPath("$.login").value(testUserLogin)));
    }

    @Test
    void test_UpdateUser_HappyCase() throws Exception {
        //given
        UserDTO.UpdateUserDTO updateUserDto = new UserDTO.UpdateUserDTO(createUserDto);
        //when
        when(userStorage.updateUser(updateUserDto)).thenReturn(new User(createUserDto));
        //then
        ResultActions response = mockMvc.perform(put(usersUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(FilmorateApplication.jsonMapper.writeValueAsString(updateUserDto)));
        response.andExpect(status().isOk());
        assertUserFieldsValues("$", response);
    }

    private void assertUserFieldsValues(String expression, ResultActions response) throws Exception {

        response.andExpect(jsonPath(expression + ".name").value(testUserName))
                .andExpect(jsonPath(expression + ".login").value(testUserLogin))
                .andExpect(jsonPath(expression + ".email").value(testUserEmail))
                .andExpect(jsonPath(expression + ".birthday").value(testUserBirthday));
    }

    private static Stream<LocalDate> provideDatesToParameterizedTest() {
        return Stream.of(LocalDate.now(), LocalDate.now().plusDays(1));
    }
}
