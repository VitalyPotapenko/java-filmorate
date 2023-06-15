package ru.yandex.practicum.filmorate.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDTO;
import ru.yandex.practicum.filmorate.model.FilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    @Autowired
    private FilmStorage filmStorage;

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.getFilms().values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody FilmDTO.CreateFilmDTO filmDTO) throws ValidationException {
        log.info("Обрабатываем запрос на создание фильма: " + filmDTO);
        validate(filmDTO);
        return filmStorage.createFilm(filmDTO);
    }

    @PutMapping
    public Film update(@Valid @RequestBody FilmDTO.UpdateFilmDTO filmDTO) throws ValidationException {
        log.info("Обрабатываем запрос на обновление сведений о фильме: " + filmDTO);
        validate(filmDTO);
        return filmStorage.updateFilm(filmDTO);
    }

    private void validate(FilmDTO.CreateFilmDTO filmDTO) throws ValidationException {
        int filmDescriptionLength = filmDTO.getDescription().length();
        if (filmDescriptionLength > 200) {
            throw new ValidationException("Описание не может быть длиннее 200 символов, а было " + filmDescriptionLength);
        }
        LocalDate filmReleaseDate = filmDTO.getReleaseDate();
        if (filmReleaseDate.isBefore(LocalDate.parse("28-12-1895", DateTimeFormatter.ofPattern("dd-MM-yyyy")))) {
            throw new ValidationException("До 28-12-1895 кино не было, а передали дату выхода фильма "
                    + filmReleaseDate);
        }
    }
}
