package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

public class FilmDTO {

    @Data
    public static class CreateFilmDTO {
        @NotBlank
        protected String name;
        protected String description;
        protected LocalDate releaseDate;
        @Positive
        protected int duration;
    }

    @Data
    public static class UpdateFilmDTO extends CreateFilmDTO {
        private int id;

        public UpdateFilmDTO() {
        }

        public UpdateFilmDTO(CreateFilmDTO createFilmDTO) {
            this.name = createFilmDTO.getName();
            this.description = createFilmDTO.getDescription();
            this.duration = createFilmDTO.getDuration();
            this.releaseDate = createFilmDTO.getReleaseDate();
        }
    }
}
