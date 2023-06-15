package ru.yandex.practicum.filmorate;

import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.api.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDTO;
import ru.yandex.practicum.filmorate.model.FilmStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FilmController.class)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FilmStorage filmStorage;
    private String filmsUrl = "/films";
    private String testFilmName = "TestFilmName";
    private String testDescription = "TestDescription";
    private int testFilmDuration = 180;
    private  String testFilmReleaseDate = "2023-06-15";
    private FilmDTO.CreateFilmDTO createFilmDto;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeEach
    void init() {
        createFilmDto = new FilmDTO.CreateFilmDTO();
        createFilmDto.setName(testFilmName);
        createFilmDto.setDescription(testDescription);
        createFilmDto.setDuration(testFilmDuration);
        createFilmDto.setReleaseDate(LocalDate.parse(testFilmReleaseDate, dateFormatter));
    }

    @Test
    void test_GetFilms_HappyCase() throws Exception {
        //given
        HashMap<Integer, Film> testFilms = new HashMap<>();
        testFilms.put(1, new Film(createFilmDto));
        //when
        when(filmStorage.getFilms()).thenReturn(testFilms);
        //then
        ResultActions response = mockMvc.perform(get(filmsUrl)
                .accept(MediaType.APPLICATION_JSON));
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
        assertFilmFieldsValues("$[0]", response);
    }

    @Test
    void test_CreateFilm_HappyCase() throws Exception {
        //given
        //when
        when(filmStorage.createFilm(createFilmDto)).thenReturn(new Film(createFilmDto));
        //then
        ResultActions response = mockMvc.perform(post(filmsUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(FilmorateApplication.jsonMapper.writeValueAsString(createFilmDto)));
        response.andExpect(status().isOk());
        assertFilmFieldsValues("$", response);
    }

    @Test
    void test_CreateFilm_NameMustNotBeEmpty() throws Exception {
        //given
        createFilmDto.setName("");
        //when
        when(filmStorage.createFilm(createFilmDto)).thenReturn(new Film(createFilmDto));
        //then
        ResultActions response = mockMvc.perform(post(filmsUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(FilmorateApplication.jsonMapper.writeValueAsString(createFilmDto)));
        response.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0})
    void test_CreateFilm_DurationMustBePositive(int dur) throws Exception {
        //given
        createFilmDto.setDuration(dur);
        //when
        when(filmStorage.createFilm(createFilmDto)).thenReturn(new Film(createFilmDto));
        //then
        ResultActions response = mockMvc.perform(post(filmsUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(FilmorateApplication.jsonMapper.writeValueAsString(createFilmDto)));
        response.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void test_CreateFilm_DescriptionMustBeNotLongerThan200Symbols() throws Exception {
        //given
        int descriptionLength = 201;
        boolean useLetters = true;
        boolean useNumbers = true;
        String description = RandomStringUtils.random(descriptionLength, useLetters, useNumbers);
        createFilmDto.setDescription(description);
        //when
        when(filmStorage.createFilm(createFilmDto)).thenReturn(new Film(createFilmDto));
        //then
        ResultActions response = mockMvc.perform(post(filmsUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(FilmorateApplication.jsonMapper.writeValueAsString(createFilmDto)));
        response.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException));
    }

    @Test
    void test_CreateFilm_ReleaseDateMustBeNotEarlierThan28Dec1895() throws Exception {
        //given
        createFilmDto.setReleaseDate(LocalDate.parse("1895-12-27", dateFormatter));
        //when
        when(filmStorage.createFilm(createFilmDto)).thenReturn(new Film(createFilmDto));
        //then
        ResultActions response = mockMvc.perform(post(filmsUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(FilmorateApplication.jsonMapper.writeValueAsString(createFilmDto)));
        response.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException));
    }

    @Test
    void test_UpdateFilm_HappyCase() throws Exception {
        //given
        FilmDTO.UpdateFilmDTO updateFilmDto = new FilmDTO.UpdateFilmDTO(createFilmDto);
        //when
        when(filmStorage.updateFilm((updateFilmDto))).thenReturn(new Film(createFilmDto));
        //then
        ResultActions response = mockMvc.perform(put(filmsUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(FilmorateApplication.jsonMapper.writeValueAsString(createFilmDto)));
        response.andExpect(status().isOk());
        assertFilmFieldsValues("$", response);
    }


    private void assertFilmFieldsValues(String expression, ResultActions response) throws Exception {

        response.andExpect(jsonPath(expression).isNotEmpty())
                .andExpect(jsonPath(expression + ".name").value(testFilmName))
                .andExpect(jsonPath(expression + ".description").value(testDescription))
                .andExpect(jsonPath(expression + ".duration").value(testFilmDuration))
                .andExpect(jsonPath(expression + ".releaseDate").value(testFilmReleaseDate));
    }
}
