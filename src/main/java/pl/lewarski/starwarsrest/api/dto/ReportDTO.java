package pl.lewarski.starwarsrest.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.lewarski.starwarsrest.database.entity.CharacterEntity;
import pl.lewarski.starwarsrest.database.entity.FilmEntity;
import pl.lewarski.starwarsrest.database.entity.ReportEntity;

import java.util.HashSet;
import java.util.Set;

@ToString
@Setter
@Getter
@Builder
public class ReportDTO {
    private Long id;
    private String queryCharacterPhrase;
    private String queryPlanetName;
    private PlanetDTO planet;
    private Set<CharacterDTO> characters = new HashSet<>();
    private Set<FilmDTO> films = new HashSet<>();

    public static ReportDTO fromEntity(ReportEntity reportEntity, CharacterEntity characterEntity) {
        return ReportDTO.builder()
                .id(reportEntity.getId())
                .queryCharacterPhrase(reportEntity.getQueryCharacterPhrase())
                .queryPlanetName(reportEntity.getQueryPlanetName())
                .planet(PlanetDTO.builder()
                        .id(reportEntity.getPlanet().getId())
                        .planetName(reportEntity.getPlanet().getName())
                        .build())
                .build();
    }

    private static Set<FilmEntity> getFilmsSet(Set<CharacterEntity> characters) {
        Set<FilmEntity> filmEntities = new HashSet<>();
        for (CharacterEntity entity : characters) {
            filmEntities.addAll(entity.getFilms());
        }
        return filmEntities;
    }

    public static ReportDTO fromEntity(ReportEntity reportEntity) {
        return ReportDTO.builder()
                .id(reportEntity.getId())
                .queryCharacterPhrase(reportEntity.getQueryCharacterPhrase())
                .queryPlanetName(reportEntity.getQueryPlanetName())
                .build();
    }

    public static ReportDTO build(ReportEntity reportEntity, PlanetDTO planetDTO, Set<CharacterDTO> characters, Set<FilmDTO> films) {
        ReportDTO reportDTO = ReportDTO.fromEntity(reportEntity);
        reportDTO.setPlanet(planetDTO);
        reportDTO.setCharacters(characters);
        reportDTO.setFilms(films);
        return reportDTO;
    }
}

