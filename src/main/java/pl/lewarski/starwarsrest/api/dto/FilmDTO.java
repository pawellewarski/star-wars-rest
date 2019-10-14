package pl.lewarski.starwarsrest.api.dto;

import lombok.Builder;
import lombok.Getter;
import pl.lewarski.starwarsrest.database.entity.FilmEntity;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
public class FilmDTO {

    private long filmId;
    private String filmName;

    public static FilmDTO fromEntity(FilmEntity entity) {
        return FilmDTO.builder()
                .filmId(entity.getId())
                .filmName(entity.getFilmName())
                .build();
    }

    public static Set<FilmDTO> fromEntitySet(Set<FilmEntity> entities) {
        Set<FilmDTO> films = new HashSet<>();
        for (FilmEntity filmEntity : entities) {
            films.add(FilmDTO.fromEntity(filmEntity));
        }
        return films;
    }
}
