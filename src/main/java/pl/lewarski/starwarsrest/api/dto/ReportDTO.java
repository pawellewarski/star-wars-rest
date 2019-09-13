package pl.lewarski.starwarsrest.api.dto;

import java.util.HashSet;
import java.util.Set;

public class ReportDTO {
    private Long id;
    private String queryCharacterPhrase;
    private String queryPlanetName;
    private PlanetDTO planet;
    private Set<CharacterDTO> characters = new HashSet<>();
    private Set<FilmDTO> films = new HashSet<>();
}

