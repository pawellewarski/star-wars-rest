package pl.lewarski.starwarsrest.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import pl.lewarski.starwarsrest.database.entity.PlanetEntity;

import java.util.List;

@Data
@Builder
public class PlanetDTO {

    private Long id;
    private String planetName;
    @JsonIgnore
    private List<String> residentsURL;

    public static PlanetDTO fromEntity(PlanetEntity planetEntity) {
        return PlanetDTO.builder()
                .id(planetEntity.getId())
                .planetName(planetEntity.getName())
                .build();
    }
}
