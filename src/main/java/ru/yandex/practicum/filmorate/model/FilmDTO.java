package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

public class FilmDTO {

    @Data
    public static class CreateFilmDTO {
        protected String name;
        protected String description;
        protected LocalDate releaseDate;
        protected int duration;
    }

    @Data
    public static class UpdateFilmDTO extends CreateFilmDTO {
        private int id;
    }
}
