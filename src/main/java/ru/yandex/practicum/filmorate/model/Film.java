package ru.yandex.practicum.filmorate.model;


import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private int id;
    private transient String name;
    private transient String description;
    private transient LocalDate releaseDate;
    private transient int duration;


    public Film(FilmDTO.CreateFilmDTO filmDTO){
        this.name = filmDTO.getName();
        this.description = filmDTO.getDescription();
        this.releaseDate = filmDTO.getReleaseDate();
        this.duration = filmDTO.getDuration();
    }

}
