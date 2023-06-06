package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class FilmStorage {
    private int id = 0;

    @Getter
    @Setter
    private HashMap<Integer, Film> films = new HashMap<>();

    public Film createFilm(FilmDTO.CreateFilmDTO filmDTO) {
        Film film = new Film(filmDTO);
        film.setId(++id);
        films.put(film.getId(), film);
        return film;
    }
}
