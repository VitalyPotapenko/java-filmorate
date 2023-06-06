package ru.yandex.practicum.filmorate.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDTO;
import ru.yandex.practicum.filmorate.model.FilmStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage = new FilmStorage();

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.getFilms().values();
    }

    @PostMapping
    public Film create(@RequestBody FilmDTO.CreateFilmDTO filmDTO) throws ValidationException {
        log.info("Обрабатываем запрос на создание фильма: " + filmDTO);
        if (isValid(filmDTO)) {
            Film film = filmStorage.createFilm(filmDTO);
            HashMap<Integer, Film> films = filmStorage.getFilms();
            films.put(film.getId(), film);
            return film;
        } else {
            return null;
        }
    }

    @PutMapping
    public Film update(@RequestBody FilmDTO.UpdateFilmDTO filmDTO) throws ValidationException {
        log.info("Обрабатываем запрос на актуализацию сведений о фильме: " + filmDTO);
        Film film;
        HashMap<Integer, Film> films = filmStorage.getFilms();
        if (isValid(filmDTO)) {
            if (films.containsKey(filmDTO.getId())) {
                int id = filmDTO.getId();
                film = films.get(id);
                film.setName(filmDTO.getName());
                film.setDescription(filmDTO.getDescription());
                film.setReleaseDate(filmDTO.getReleaseDate());
                film.setDuration(filmDTO.getDuration());
            } else {
                throw new IllegalArgumentException("Не удалось обновить фильм. Фильма с  Id = " + filmDTO.getId()
                        + " не существует");
            }
            films.put(film.getId(), film);
            return film;
        } else {
            return null;
        }
    }

    private boolean isValid(FilmDTO.CreateFilmDTO filmDTO) throws ValidationException {
        String filmName = filmDTO.getName();
        if (filmName == null || filmName.isEmpty()) {
            throw new ValidationException("Фильм не может не иметь названия");
        }

        int filmDescriptionLength = filmDTO.getDescription().length();
        if (filmDescriptionLength > 200) {
            throw new ValidationException("Описание не может быть длиннее 200 символов, а было " + filmDescriptionLength);
        }
        LocalDate filmReleaseDate = filmDTO.getReleaseDate();
        if (filmReleaseDate.isBefore(LocalDate.parse("28-12-1895", DateTimeFormatter.ofPattern("dd-MM-yyyy")))) {
            throw new ValidationException("Дата выхода фильма не может быть раньше 28-12-1895, а передали "
                    + filmReleaseDate);
        }

        int filmDuration = filmDTO.getDuration();
        if (filmDuration <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной, а была " + filmDuration);
        }
        return true;
    }

}
