package pl.lewarski.starwarsrest.api.dto;

import lombok.Builder;
import pl.lewarski.starwarsrest.database.entity.CharacterEntity;
import pl.lewarski.starwarsrest.database.entity.ReportEntity;

import java.util.HashSet;
import java.util.Set;

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
                        .planetId(reportEntity.getPlanetId())
                        .planetName(reportEntity.getPlanetName())
                        .build())
                .characters(CharacterDTO.fromEntitySet(reportEntity.getCharacters()))
                .films(FilmDTO.fromEntitySet(characterEntity.getFilms()))
                .build();
    }

    public static ReportDTO fromEntity(ReportEntity reportEntity) {
        return ReportDTO.builder()
                .id(reportEntity.getId())
                .queryCharacterPhrase(reportEntity.getQueryCharacterPhrase())
                .queryPlanetName(reportEntity.getQueryPlanetName())
                .build();
    }
}

