package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
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

    public Film updateFilm(FilmDTO.UpdateFilmDTO filmDTO){
        int id = filmDTO.getId();
        if (films.containsKey(id)) {
            Film film = films.get(id);
            film.setName(filmDTO.getName());
            film.setDescription(filmDTO.getDescription());
            film.setReleaseDate(filmDTO.getReleaseDate());
            film.setDuration(filmDTO.getDuration());
            return film;
        } else {
            throw new IllegalArgumentException("Не удалось обновить фильм. Фильма с  Id = " + id
                    + " не существует");
        }
    }
}
