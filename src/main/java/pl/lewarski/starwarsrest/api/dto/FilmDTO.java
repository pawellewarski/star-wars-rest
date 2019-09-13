package pl.lewarski.starwarsrest.api.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FilmDTO {

    private int filmId;
    private String filmName;
}
